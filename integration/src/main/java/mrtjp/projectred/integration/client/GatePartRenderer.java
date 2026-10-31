package mrtjp.projectred.integration.client;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import codechicken.lib.vec.RedundantTransformation;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import mrtjp.projectred.core.client.ProjectRedPartRenderer;
import mrtjp.projectred.core.client.RecordingBufferSource;
import mrtjp.projectred.integration.part.GatePart;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/**
 * Dynamic gate rendering. With static_gate_renderer on (default) the gate body is chunk-baked
 * ({@link GateStaticModels}) and only animated parts (pointers, custom renders) are drawn here.
 */
public class GatePartRenderer extends ProjectRedPartRenderer<GatePart> {

    public static final GatePartRenderer INSTANCE = new GatePartRenderer();

    private GatePartRenderer() {
    }

    @Override
    protected boolean shouldRenderDynamic(GatePart part) {
        return !part.isStaticModelActive() || GateModelRenderer.instance().hasDynamic(part);
    }

    @Override
    public void renderDynamic(GatePart part, PoseStack mStack, RecordingBufferSource buffers, int packedLight, int packedOverlay, float partialTicks) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        ccrs.bind(new TransformingVertexConsumer(buffers.getBuffer(RenderTypes.cutoutMovingBlock()), mStack), DefaultVertexFormat.BLOCK);
        if (!part.isStaticModelActive()) {
            GateModelRenderer.instance().renderStatic(ccrs, part, RedundantTransformation.INSTANCE);
        }
        GateModelRenderer.instance().renderDynamic(ccrs, part, RedundantTransformation.INSTANCE, mStack, buffers, packedLight, packedOverlay, partialTicks);
    }
}
