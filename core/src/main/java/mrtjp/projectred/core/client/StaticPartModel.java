package mrtjp.projectred.core.client;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakedQuadVertexBuilder;
import codechicken.multipart.api.part.MultiPart;
import codechicken.multipart.api.part.render.StaticPartRenderer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * Chunk-baked (static) geometry of a Project Red part, handed to CB Multipart through {@link MultiPart#getModelData()}.
 * <p>
 * CB Multipart 3.6 only gives a {@link StaticPartRenderer} the part's {@link ModelData}, on the chunk building thread.
 * Parts therefore bake their quads in {@code getModelData()}, which NeoForge's {@code ModelDataManager} only calls on
 * the client thread when the tile asked for a refresh ({@code tile().markRender()}); the chunk thread only reads the
 * immutable result. This replaces the 1.21.1 {@code PartRenderer.renderStatic(part, layer, ccrs)} path.
 */
public interface StaticPartModel {

    ModelProperty<StaticPartModel> PROPERTY = new ModelProperty<>();

    /**
     * Called on the chunk building thread.
     */
    void collectParts(BlockAndTintGetter level, BlockPos pos, RandomSource rand, List<BlockStateModelPart> parts);

    default ModelData toModelData() {
        return ModelData.of(PROPERTY, this);
    }

    static StaticPartModel of(QuadCollection quads) {
        BlockStateModelPart part = quadsPart(quads);
        return (level, pos, rand, parts) -> {
            if (part != null) parts.add(part);
        };
    }

    /**
     * Bakes what {@code render} draws through the given {@link CCRenderState} into quads (block format, sprites set by
     * the usual {@code IconTransformation}s). Same settings as CB Multipart 3.5's static part baking.
     */
    static QuadCollection bake(Consumer<CCRenderState> render) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        BakedQuadVertexBuilder builder = ccrs.bindBaking();
        render.accept(ccrs);
        QuadCollection.Builder quads = new QuadCollection.Builder();
        builder.getAsUnculled(quads);
        ccrs.reset();
        return quads.build();
    }

    /**
     * A model part with the given quads, all unculled; {@code null} when there are none.
     */
    static @Nullable BlockStateModelPart quadsPart(QuadCollection quads) {
        List<BakedQuad> all = quads.getAll();
        if (all.isEmpty()) return null;
        TextureAtlasSprite sprite = all.getFirst().materialInfo().sprite();
        Material.Baked particle = new Material.Baked(sprite, false);
        return new BlockStateModelPart() {
            @Override
            public List<BakedQuad> getQuads(@Nullable Direction direction) {
                return quads.getQuads(direction);
            }

            @Override
            public boolean useAmbientOcclusion() {
                return true;
            }

            @Override
            public Material.Baked particleMaterial() {
                return particle;
            }

            @Override
            public @BakedQuad.MaterialFlags int materialFlags() {
                return quads.materialFlags();
            }
        };
    }

    /**
     * The {@link StaticPartRenderer} for every part type that uses this model data.
     */
    final class Renderer implements StaticPartRenderer<MultiPart> {

        public static final Renderer INSTANCE = new Renderer();

        private Renderer() {
        }

        @Override
        public void collectParts(ModelData modelData, BlockAndTintGetter level, BlockPos pos, RandomSource rand, List<BlockStateModelPart> parts) {
            StaticPartModel model = modelData.get(PROPERTY);
            if (model != null) {
                model.collectParts(level, pos, rand, parts);
            }
        }
    }
}
