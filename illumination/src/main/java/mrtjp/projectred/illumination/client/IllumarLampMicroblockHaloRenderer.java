package mrtjp.projectred.illumination.client;

import codechicken.lib.vec.Cuboid6;
import codechicken.microblock.part.MicroblockPart;
import codechicken.multipart.api.part.render.DynamicPartRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import mrtjp.projectred.core.client.HaloRenderer;
import mrtjp.projectred.illumination.part.IllumarLampMicroMaterial;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Halos for lit Illumar lamp microblocks (covers, panels, slabs, hollow covers, corners, edges, posts).
 * <p>
 * 1.21.1 drew them from {@code MicroMaterialClient.renderDynamic}. CB Multipart 3.6 has no per-material dynamic
 * hook yet (its {@code MicroMaterialClient.submitDynamic} is a TODO) and registers only static renderers for
 * microblock parts, so this dynamic renderer is registered for the microblock part types and only acts on
 * microblocks whose material is an {@link IllumarLampMicroMaterial}. The microblock geometry itself stays
 * chunk-baked by CB Multipart.
 */
public class IllumarLampMicroblockHaloRenderer implements DynamicPartRenderer<MicroblockPart, IllumarLampMicroblockHaloRenderer.State> {

    @Override
    public State createDynamicState() {
        return new State();
    }

    @Override
    public void extractDynamicState(BEState beState, State state, MicroblockPart part, float partialTick, Vec3 camera, @Nullable CrumblingOverlay breakProgress) {
        if (part.getMaterial() instanceof IllumarLampMicroMaterial lamp) {
            state.box = part.getBounds().copy().expand(0.025D);
            state.colour = lamp.getLightColor();
        } else {
            state.box = null;
        }
    }

    @Override
    public void submitDynamic(BEState beState, State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.box == null) return;
        HaloRenderer.addLight(beState.pos(), state.box, state.colour);
    }

    public static final class State {

        private @Nullable Cuboid6 box;
        private int colour;
    }
}
