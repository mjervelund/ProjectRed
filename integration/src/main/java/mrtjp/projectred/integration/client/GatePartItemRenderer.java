package mrtjp.projectred.integration.client;

import mrtjp.projectred.core.client.ProjectRedItemRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.RedundantTransformation;
import com.mojang.blaze3d.vertex.PoseStack;
import mrtjp.projectred.integration.item.BaseGatePartItem;
import mrtjp.projectred.core.client.RecordingBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class GatePartItemRenderer extends ProjectRedItemRenderer {

    public GatePartItemRenderer() {
    }

    @Override
    public void renderItem(ItemStack stack, ItemDisplayContext transformType, PoseStack mStack, RecordingBufferSource getter, int packedLight, int packedOverlay) {

        if (!(stack.getItem() instanceof BaseGatePartItem gateItem)) return;

        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        getter.bind(ccrs, RenderTypes.cutoutMovingBlock(), mStack);
        GateModelRenderer.instance().renderInventory(ccrs, stack, gateItem.getGateType().ordinal(), 0, RedundantTransformation.INSTANCE, mStack, getter, packedLight, packedOverlay);
    }
}
