package mrtjp.projectred.expansion.client;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakedQuadVertexBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

import static mrtjp.projectred.expansion.ProjectRedExpansion.MOD_ID;

public class FrameBlockModel implements DynamicBlockStateModel {

    // Mask -> part
    private static final HashMap<Integer, BlockStateModelPart> bakedParts = new HashMap<>();

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        var frameData = level.getModelData(pos).get(FrameModelData.DATA);
        int mask = frameData != null ? frameData.mask() : 0;
        parts.add(getOrGeneratePart(mask));
    }

    @Override
    public Material.Baked particleMaterial() {
        return new Material.Baked(FrameModelRenderer.getFrameIcon(), false);
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return 0;
    }

    private BlockStateModelPart getOrGeneratePart(int mask) {
        synchronized (bakedParts) {
            return bakedParts.computeIfAbsent(mask, m -> new Part(generateQuads(m), particleMaterial()));
        }
    }

    private static List<BakedQuad> generateQuads(int mask) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.computeLighting = false; // No lighting whilst baking!
        ccrs.brightness = 0;
        BakedQuadVertexBuilder builder = new BakedQuadVertexBuilder();
        ccrs.bind(builder, DefaultVertexFormat.BLOCK);
        FrameModelRenderer.renderStatic(ccrs, mask);

        return builder.getQuads();
    }

    // No cull yet: all quads are unculled
    private record Part(List<BakedQuad> quads, Material.Baked particleMaterial) implements BlockStateModelPart {

        @Override
        public List<BakedQuad> getQuads(@Nullable Direction direction) {
            return direction == null ? quads : List.of();
        }

        @Override
        public boolean useAmbientOcclusion() {
            return true;
        }

        @Override
        public @BakedQuad.MaterialFlags int materialFlags() {
            return 0;
        }
    }

    public static final class Unbaked implements CustomUnbakedBlockStateModel {

        public static final Identifier TYPE = Identifier.fromNamespaceAndPath(MOD_ID, "frame");
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public BlockStateModel bake(ModelBaker baker) {
            return new FrameBlockModel();
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
        }

        @Override
        public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
            return CODEC;
        }
    }
}
