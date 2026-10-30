package mrtjp.projectred.expansion.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import mrtjp.projectred.core.client.RecordingBufferSource;
import mrtjp.projectred.expansion.MovementManager;
import mrtjp.projectred.expansion.MovingStructure;
import mrtjp.projectred.expansion.MovingStructureInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Draws blocks of moving structures at their animated offset (the chunk mesh skips them, see SectionCompilerMixin).
 */
public class MovingBlockRenderManager {

    private static final Direction[] SIDES_AND_NULL = { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, null };

    public static boolean isMoving(MovementManager manager, BlockPos pos) {
        return manager.getMovementInfo(pos).isMoving();
    }

    public static boolean isAdjacentToMoving(MovementManager manager, BlockPos pos) {
        for (int s = 0; s < 6; s++) {
            var rPos = pos.relative(Direction.values()[s]);
            if (manager.getMovementInfo(rPos).isMoving()) return true;
        }
        return false;
    }

    private static float partialTick() {
        return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
    }

    private static RenderType renderType(ChunkSectionLayer layer) {
        return switch (layer) {
            case SOLID -> RenderTypes.solidMovingBlock();
            case TRANSLUCENT -> RenderTypes.translucentMovingBlock();
            default -> RenderTypes.cutoutMovingBlock();
        };
    }

    public static void onSubmitCustomGeometry(SubmitCustomGeometryEvent event) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        MovementManager manager = MovementManager.getInstance(level);
        if (manager.hasNoMovingStructures()) return;

        RandomSource random = RandomSource.create();
        var modelSet = Minecraft.getInstance().getModelManager().getBlockStateModelSet();
        RecordingBufferSource buffers = new RecordingBufferSource();
        List<BlockStateModelPart> parts = new ArrayList<>();
        QuadInstance instance = new QuadInstance();

        Vec3 cam = event.getLevelRenderState().cameraRenderState.pos;
        PoseStack stack = event.getPoseStack();
        stack.pushPose();
        stack.translate(-cam.x, -cam.y, -cam.z);

        for (MovingStructure structure : manager.getMovingStructures()) {
            // Set up render offset based on progress of movement
            var offset = structure.getRenderOffset(partialTick());
            stack.pushPose();
            stack.translate(offset.x, offset.y, offset.z);

            Iterator<BlockPos> it = structure.iteratePreMove();
            while (it.hasNext()) {
                BlockPos p = it.next();
                BlockState state = level.getBlockState(p);
                if (state.isAir()) continue;

                parts.clear();
                random.setSeed(state.getSeed(p));
                modelSet.get(state).collectParts(level, p, state, random, parts);
                instance.setLightCoords(LightCoordsUtil.getLightCoords(level, p));

                stack.pushPose();
                stack.translate(p.getX(), p.getY(), p.getZ());
                PoseStack.Pose pose = stack.last();
                for (BlockStateModelPart part : parts) {
                    for (Direction side : SIDES_AND_NULL) {
                        for (BakedQuad quad : part.getQuads(side)) {
                            buffers.getBuffer(renderType(quad.materialInfo().layer())).putBakedQuad(pose, quad, instance);
                        }
                    }
                }
                stack.popPose(); //p
            }
            stack.popPose(); //offset
        }
        stack.popPose(); //cam

        buffers.submit(event.getSubmitNodeCollector());
    }

    public static void onExtractBlockOutline(ExtractBlockOutlineRenderStateEvent event) {
        // Check for movement. Return if not moving
        MovementManager manager = MovementManager.getClientInstanceNullable();
        if (manager == null || manager.hasNoMovingStructures()) return;
        BlockPos pos = event.getBlockPos();
        MovingStructureInfo info = manager.getMovementInfo(pos);
        if (!info.isMoving()) return;

        Level level = event.getLevel();
        BlockState state = event.getBlockState();
        if (state.isAir() || !level.getWorldBorder().isWithinBounds(pos)) return;

        VoxelShape shape = state.getShape(level, pos);
        var offset = info.getRenderOffset(partialTick());
        float lineWidth = Minecraft.getInstance().getWindow().getAppropriateLineWidth();

        // Replace the outline: same shape, drawn at the animated position
        event.getLevelRenderState().blockOutlineRenderState = new BlockOutlineRenderState(pos, event.isInTranslucentPass(), event.isHighContrast(), Shapes.empty(),
                List.of((renderState, collector, poseStack, levelRenderState) -> {
                    Vec3 cam = levelRenderState.cameraRenderState.pos;
                    poseStack.pushPose();
                    poseStack.translate(pos.getX() - cam.x + offset.x, pos.getY() - cam.y + offset.y, pos.getZ() - cam.z + offset.z);
                    collector.submitShapeOutline(poseStack, shape, RenderTypes.lines(), 0x66000000, lineWidth, false); // colour from vanilla's outline
                    poseStack.popPose();
                    return true;
                }));
        event.setCanceled(true);
    }
}
