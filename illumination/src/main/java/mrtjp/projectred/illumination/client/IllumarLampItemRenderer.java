package mrtjp.projectred.illumination.client;

import mrtjp.projectred.core.client.ProjectRedItemRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import codechicken.lib.model.bakedmodels.WrappedItemModel;
import codechicken.lib.render.BlockRenderer;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import com.mojang.blaze3d.vertex.PoseStack;
import mrtjp.projectred.core.client.HaloRenderer;
import mrtjp.projectred.illumination.block.IllumarLampBlock;
import mrtjp.projectred.core.client.RecordingBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class IllumarLampItemRenderer extends WrappedItemModel extends ProjectRedItemRenderer {

    private static final Cuboid6 BLOCK_BOUNDS = Cuboid6.full.copy().expand(-0.02D);
    private static final Cuboid6 GLOW_BOUNDS = Cuboid6.full.copy().expand(0.02D);
    private static final RandomSource random = RandomSource.create();

    public IllumarLampItemRenderer(BakedModel wrapped) {
        super(wrapped);
    }

    @Override
    public void renderItem(ItemStack stack, ItemDisplayContext transformType, PoseStack mStack, RecordingBufferSource getter, int packedLight, int packedOverlay) {
        Item item = stack.getItem();
        if (!(item instanceof BlockItem blockItem)) return;

        if (!(blockItem.getBlock() instanceof IllumarLampBlock block)) return;

        if (!block.isInverted()) {
            // Non-inverted blocks can use default Minecraft BlockItem model. Shouldn't happen
            // because this renderer should only be registered to inverted variants.
            renderWrapped(stack, mStack, getter, packedLight, packedOverlay, false);
            return;
        }

        // Render actual block. Required because renderWrapped does not play nice with
        // halo rendering. Halo completely obscures wrapped render.

        // Obtain texture from original block model
        TextureAtlasSprite icon = wrapped.getQuads(null, Direction.UP, random).get(0).getSprite();

        // Render block
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        getter.bind(ccrs, RenderTypes.cutoutMovingBlock(), mStack);

        ccrs.setPipeline(new IconTransformation(icon));
        BlockRenderer.renderCuboid(ccrs, BLOCK_BOUNDS, 0);

        // Render halo
        HaloRenderer.renderInventoryHalo(ccrs, mStack, getter, GLOW_BOUNDS, Vector3.ZERO, block.getColor());
        HaloRenderer.addItemRendererBloom(transformType, mStack, Vector3.ZERO, GLOW_BOUNDS, block.getColor());
    }

}
