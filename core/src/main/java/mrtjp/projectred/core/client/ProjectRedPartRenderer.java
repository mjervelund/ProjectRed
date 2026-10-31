package mrtjp.projectred.core.client;

import codechicken.multipart.api.part.MultiPart;
import codechicken.multipart.api.part.render.DynamicPartRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Base for Project Red's part renderers on CB Multipart 3.6's extract/submit dynamic rendering.
 * <p>
 * The extract step keeps a reference to the part (extraction and submission both run on the client thread in the
 * same frame); {@link #renderDynamic} then draws it as before through a {@link RecordingBufferSource}.
 * Static (chunk-baked) geometry goes through {@link StaticPartModel} instead.
 */
public abstract class ProjectRedPartRenderer<P extends MultiPart> implements DynamicPartRenderer<P, ProjectRedPartRenderer.State<P>> {

    @Override
    public State<P> createDynamicState() {
        return new State<>();
    }

    @Override
    public void extractDynamicState(BEState beState, State<P> state, P part, float partialTick, Vec3 camera, @Nullable CrumblingOverlay breakProgress) {
        state.part = shouldRenderDynamic(part) ? part : null;
        state.partialTick = partialTick;
    }

    @Override
    public void submitDynamic(BEState beState, State<P> state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        P part = state.part;
        if (part == null) return;
        state.part = null;
        RecordingBufferSource buffers = new RecordingBufferSource();
        renderDynamic(part, poseStack, buffers, beState.packedLight(), OverlayTexture.NO_OVERLAY, state.partialTick);
        buffers.submit(collector);
        submitExtra(part, poseStack, collector, beState.packedLight(), state.partialTick);
    }

    /**
     * False skips {@link #renderDynamic} this frame, e.g. when everything the part draws is chunk-baked
     * ({@link StaticPartModel}) and it has no animated parts.
     */
    protected boolean shouldRenderDynamic(P part) {
        return true;
    }

    /**
     * For content that has to go through the collector directly (items, text), after {@link #renderDynamic}.
     */
    protected void submitExtra(P part, PoseStack poseStack, SubmitNodeCollector collector, int packedLight, float partialTicks) {
    }

    public abstract void renderDynamic(P part, PoseStack mStack, RecordingBufferSource buffers, int packedLight, int packedOverlay, float partialTicks);

    public static final class State<P> {

        private @Nullable P part;
        private float partialTick;
    }
}
