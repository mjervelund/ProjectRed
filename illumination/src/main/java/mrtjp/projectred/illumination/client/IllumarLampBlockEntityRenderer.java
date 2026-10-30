package mrtjp.projectred.illumination.client;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import codechicken.lib.vec.Cuboid6;
import com.mojang.blaze3d.vertex.PoseStack;
import mrtjp.projectred.core.client.HaloRenderer;
import mrtjp.projectred.illumination.block.IllumarLampBlock;
import mrtjp.projectred.illumination.tile.IllumarLampBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class IllumarLampBlockEntityRenderer implements BlockEntityRenderer<IllumarLampBlockEntity, BlockEntityRenderState> {

    public static final IllumarLampBlockEntityRenderer INSTANCE = new IllumarLampBlockEntityRenderer();

    public static final Cuboid6 GLOW_BOUNDS = Cuboid6.full.copy().expand(0.05D);

    private IllumarLampBlockEntityRenderer() {
    }

    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }

    // Halos are queued for HaloRenderer while extracting; nothing else to submit.
    @Override
    public void extractRenderState(IllumarLampBlockEntity tile, BlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(tile, state, breakProgress);
        if (tile.getLevel() != null) {
            BlockState blockState = tile.getLevel().getBlockState(tile.getBlockPos());
            if (blockState.getBlock() instanceof IllumarLampBlock && tile.isLit()) {
                HaloRenderer.addLight(tile.getBlockPos(), GLOW_BOUNDS, tile.color);
            }
        }
    }

    @Override
    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public AABB getRenderBoundingBox(IllumarLampBlockEntity blockEntity) {
        return GLOW_BOUNDS
                .copy()
                .add(blockEntity.getBlockPos())
                .aabb();
    }
}
