package mrtjp.projectred.transmission.client;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import mrtjp.projectred.core.client.ProjectRedPartRenderer;
import mrtjp.projectred.core.client.RecordingBufferSource;
import mrtjp.projectred.transmission.part.BaseCenterWirePart;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/**
 * Dynamic path, for wires that are not chunk-baked (static_wire_renderer off, except bundled cables). Static ones
 * render through {@link WireStaticModels}.
 */
public class CenterWirePartRenderer extends ProjectRedPartRenderer<BaseCenterWirePart> {

    public static final CenterWirePartRenderer INSTANCE = new CenterWirePartRenderer();

    @Override
    protected boolean shouldRenderDynamic(BaseCenterWirePart part) {
        return !part.isStaticModelActive();
    }

    @Override
    public void renderDynamic(BaseCenterWirePart part, PoseStack mStack, RecordingBufferSource buffers, int packedLight, int packedOverlay, float partialTicks) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        ccrs.bind(new TransformingVertexConsumer(buffers.getBuffer(RenderTypes.solidMovingBlock()), mStack), DefaultVertexFormat.BLOCK);
        FramedWireModelRenderer.render(ccrs, part);
    }
}
