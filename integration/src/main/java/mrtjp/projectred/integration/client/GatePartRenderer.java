package mrtjp.projectred.integration.client;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import codechicken.lib.vec.RedundantTransformation;
import codechicken.multipart.api.part.render.PartRenderer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import mrtjp.projectred.core.Configurator;
import mrtjp.projectred.integration.part.GatePart;
import mrtjp.projectred.core.client.RecordingBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;

import javax.annotation.Nullable;

public class GatePartRenderer implements PartRenderer<GatePart> {

    public static final PartRenderer<GatePart> INSTANCE = new GatePartRenderer();

    private GatePartRenderer() {
    }

    @Override
    public void renderStatic(GatePart part, @Nullable RenderType layer, CCRenderState ccrs) {
        if (layer == null || (layer == RenderTypes.cutoutMovingBlock() && Configurator.CLIENT.staticGates.get())) {
            ccrs.brightness = 0;
            GateModelRenderer.instance().renderStatic(ccrs, part, RedundantTransformation.INSTANCE);
        }
    }

    @Override
    public void renderDynamic(GatePart part, PoseStack mStack, RecordingBufferSource buffers, int packedLight, int packedOverlay, float partialTicks) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        ccrs.bind(new TransformingVertexConsumer(buffers.getBuffer(RenderTypes.cutoutMovingBlock()), mStack), DefaultVertexFormat.BLOCK);
        GateModelRenderer.instance().renderDynamic(ccrs, part, RedundantTransformation.INSTANCE, mStack, buffers, packedLight, packedOverlay, partialTicks);
    }
}
