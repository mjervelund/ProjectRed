package mrtjp.projectred.transmission.client;

import mrtjp.projectred.core.client.ProjectRedItemRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.RedundantTransformation;
import com.mojang.blaze3d.vertex.PoseStack;
import mrtjp.projectred.transmission.WireType;
import mrtjp.projectred.transmission.item.FaceWirePartItem;
import mrtjp.projectred.core.client.RecordingBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class WirePartItemRenderer extends ProjectRedItemRenderer {

    public WirePartItemRenderer() { }

    @Override
    public void renderItem(ItemStack stack, ItemDisplayContext transformType, PoseStack mStack, RecordingBufferSource getter, int packedLight, int packedOverlay) {

        if (!(stack.getItem() instanceof FaceWirePartItem)) return;

        WireType type = ((FaceWirePartItem) stack.getItem()).getType();

        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        getter.bind(ccrs, RenderTypes.cutoutMovingBlock(), mStack);

        WireModelRenderer.renderInventory(ccrs, type.getThickness(), type.getItemColour() << 8 | 0xFF, type.getTextures().get(0), RedundantTransformation.INSTANCE);
    }
}
