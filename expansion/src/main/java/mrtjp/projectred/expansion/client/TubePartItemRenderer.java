package mrtjp.projectred.expansion.client;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import codechicken.lib.model.PerspectiveModelState;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.RedundantTransformation;
import com.mojang.blaze3d.vertex.PoseStack;
import mrtjp.projectred.expansion.TubeType;
import mrtjp.projectred.expansion.item.TubePartItem;
import mrtjp.projectred.core.client.RecordingBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class TubePartItemRenderer implements IItemRenderer {

    public TubePartItemRenderer() { }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public PerspectiveModelState getModelState() {
        return TransformUtils.DEFAULT_BLOCK;
    }

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
