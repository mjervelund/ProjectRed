package mrtjp.projectred.illumination.client;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import codechicken.lib.vec.Cuboid6;
import com.mojang.blaze3d.vertex.PoseStack;
import mrtjp.projectred.core.BundledSignalsLib;
import mrtjp.projectred.core.client.HaloRenderer;
import mrtjp.projectred.illumination.tile.IllumarSmartLampBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.phys.AABB;

public class IllumarSmartLampBlockEntityRenderer implements BlockEntityRenderer<IllumarSmartLampBlockEntity, BlockEntityRenderState> {

    public static final IllumarSmartLampBlockEntityRenderer INSTANCE = new IllumarSmartLampBlockEntityRenderer();

    public static final Cuboid6 GLOW_BOUNDS = Cuboid6.full.copy().expand(0.05D);

    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }

    // Halos are queued for HaloRenderer while extracting; nothing else to submit.
    @Override
    public void extractRenderState(IllumarSmartLampBlockEntity tile, BlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(tile, state, breakProgress);
        byte[] signal = tile.getSignal();
        if (!BundledSignalsLib.isSignalZero(signal)) {
            HaloRenderer.addMultiLight(tile.getBlockPos(), GLOW_BOUNDS, tile.getSignal());
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
    public AABB getRenderBoundingBox(IllumarSmartLampBlockEntity blockEntity) {
        return GLOW_BOUNDS
                .copy()
                .add(blockEntity.getBlockPos())
                .aabb();
    }
}
