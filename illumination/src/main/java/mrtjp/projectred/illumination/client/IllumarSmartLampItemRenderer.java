package mrtjp.projectred.illumination.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.List;
import mrtjp.projectred.core.client.ProjectRedItemRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import codechicken.lib.render.BlockRenderer;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.MultiIconTransformation;
import com.mojang.blaze3d.vertex.PoseStack;
import mrtjp.projectred.core.client.HaloRenderer;
import mrtjp.projectred.illumination.block.IllumarSmartLampBlock;
import net.minecraft.client.Minecraft;
import mrtjp.projectred.core.client.RecordingBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class IllumarSmartLampItemRenderer extends ProjectRedItemRenderer {

    private static final Cuboid6 BLOCK_BOUNDS = Cuboid6.full.copy().expand(-0.02D);
    private static final Cuboid6 GLOW_BOUNDS = Cuboid6.full.copy().expand(0.02D);
    private static final RandomSource random = RandomSource.create();

    private final byte[] signal = new byte[16];
    private long lastSignalAnimateTime = -1L;

    public IllumarSmartLampItemRenderer() {
    }

    @Override
    public void renderItem(ItemStack stack, ItemDisplayContext transformType, PoseStack mStack, RecordingBufferSource getter, int packedLight, int packedOverlay) {
        Item item = stack.getItem();
        if (!(item instanceof BlockItem blockItem)) return;

        if (!(blockItem.getBlock() instanceof IllumarSmartLampBlock block)) return;

        // Render actual block. Required because renderWrapped does not play nice with
        // halo rendering. Halo completely obscures wrapped render.

        // Obtain texture from original block model
        TextureAtlasSprite[] icons = new TextureAtlasSprite[6];
        for (Direction dir : Direction.values()) {
            icons[dir.get3DDataValue()] = sideSprite(block.defaultBlockState(), dir);
        }
        MultiIconTransformation iconT = new MultiIconTransformation(icons);

        // Render block
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        getter.bind(ccrs, RenderTypes.cutoutMovingBlock(), mStack);

        ccrs.setPipeline(iconT);
        BlockRenderer.renderCuboid(ccrs, BLOCK_BOUNDS, 0);

        // Animate signals
        animateSignal();

        // Render halo
        HaloRenderer.renderInventoryMultiHalo(ccrs, mStack, getter, GLOW_BOUNDS, Vector3.ZERO, signal);
        HaloRenderer.addItemRendererMultiBloom(transformType, mStack, Vector3.ZERO, GLOW_BOUNDS, signal);
    }

    private void animateSignal() {

        Level level = Minecraft.getInstance().level;
        long time = level != null ? level.getGameTime() : System.currentTimeMillis() / 50L; // approximate time progression if no level

        // Only do this once per tick
        if (time == lastSignalAnimateTime) return;
        lastSignalAnimateTime = time;

        // Sine-wave animation
        double t = (Math.sin(time / 200.0) + 1.0) / 2.0 * 15.0; // Sine wave with bounds [0, 15]
        double d = 1.5; // Max distance (max active colours / 2)

        for (int i = 0; i < 16; i++) {
            double diff = Math.min(Math.abs(t - i), d);
            double brightness = 1.0 - diff / d;
            signal[i] = (byte) (255 * brightness);
        }
    }


    // The sprite on the given face of the block's model (the item used to wrap the lit block model).
    private static TextureAtlasSprite sideSprite(BlockState state, Direction side) {
        List<BlockStateModelPart> parts = new ArrayList<>();
        Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state).collectParts(random, parts);
        for (BlockStateModelPart part : parts) {
            var quads = part.getQuads(side);
            if (!quads.isEmpty()) return quads.get(0).materialInfo().sprite();
        }
        return Minecraft.getInstance().getModelManager().getBlockStateModelSet().getParticleMaterial(state).sprite();
    }
}
