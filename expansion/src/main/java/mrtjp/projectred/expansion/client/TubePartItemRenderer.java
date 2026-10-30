package mrtjp.projectred.expansion.client;

import mrtjp.projectred.core.client.ProjectRedItemRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.RedundantTransformation;
import com.mojang.blaze3d.vertex.PoseStack;
import mrtjp.projectred.expansion.TubeType;
import mrtjp.projectred.expansion.item.TubePartItem;
import mrtjp.projectred.core.client.RecordingBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class TubePartItemRenderer extends ProjectRedItemRenderer {

    public TubePartItemRenderer() { }

    @Override
    public void renderItem(ItemStack stack, ItemDisplayContext transformType, PoseStack mStack, RecordingBufferSource getter, int packedLight, int packedOverlay) {

        if (!(stack.getItem() instanceof TubePartItem pipe)) return;

        TubeType type = pipe.getType();

        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        getter.bind(ccrs, RenderTypes.cutoutMovingBlock(), mStack);

        TubeModelRenderer.renderInventory(ccrs, type.getTextures().get(0), RedundantTransformation.INSTANCE);
    }
}
