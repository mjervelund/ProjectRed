package mrtjp.projectred.transmission.client;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import mrtjp.projectred.core.client.ProjectRedPartRenderer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import mrtjp.projectred.core.Configurator;
import mrtjp.projectred.transmission.part.BaseFaceWirePart;
import mrtjp.projectred.transmission.part.BundledCablePart;
import mrtjp.projectred.core.client.RecordingBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;

import javax.annotation.Nullable;

public class FaceWirePartRenderer extends ProjectRedPartRenderer<BaseFaceWirePart> {

    public static final FaceWirePartRenderer INSTANCE = new FaceWirePartRenderer();

    // 26.3 port: everything renders on the dynamic path (ProjectRedPartRenderer); the static (chunk) path isn't ported.
    public void renderStatic(BaseFaceWirePart part, @Nullable RenderType layer, CCRenderState ccrs) {
        if (layer == null || (layer == RenderTypes.solidMovingBlock() && useStaticRenderer(part))) {
            ccrs.brightness = 0;
            WireModelRenderer.render(ccrs, part);
        }
    }

    @Override
    public void renderDynamic(BaseFaceWirePart part, PoseStack mStack, RecordingBufferSource buffers, int packedLight, int packedOverlay, float partialTicks) {
        {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.brightness = packedLight;
            ccrs.overlay = packedOverlay;
            ccrs.bind(new TransformingVertexConsumer(buffers.getBuffer(RenderTypes.solidMovingBlock()), mStack), DefaultVertexFormat.BLOCK);
            WireModelRenderer.render(ccrs, part);
        }
    }

    private boolean useStaticRenderer(BaseFaceWirePart part) {
        // Force static rendering for bundled cables, as they don't
        // change their appearance based on signal.
        if (part instanceof BundledCablePart) {
            return true;
        }

        return Configurator.CLIENT.staticWires.get();
    }
}
