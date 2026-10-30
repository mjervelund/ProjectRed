package mrtjp.projectred.illumination.client;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.api.part.render.PartRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import mrtjp.projectred.core.client.HaloRenderer;
import mrtjp.projectred.illumination.part.MultipartLightPart;
import mrtjp.projectred.core.client.RecordingBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.jetbrains.annotations.Nullable;

public class MultipartLightPartRenderer implements PartRenderer<MultipartLightPart> {

    public static final MultipartLightPartRenderer INSTANCE = new MultipartLightPartRenderer();

    private MultipartLightPartRenderer() {
    }

    @Override
    public void renderStatic(MultipartLightPart part, @Nullable RenderType layer, CCRenderState ccrs) {
        if (layer == null || layer == RenderTypes.cutoutMovingBlock()) {
            ccrs.brightness = 0;
            part.getProperties().render(part, Vector3.ZERO, ccrs);
        }
    }

    @Override
    public void renderDynamic(MultipartLightPart part, PoseStack pStack, RecordingBufferSource buffers, int packedLight, int packedOverlay, float partialTicks) {
        if (part.isLightOn())
            HaloRenderer.addLight(part.pos(), part.getProperties().getGlowBounds(part.getSide()), part.getColor());
    }
}
