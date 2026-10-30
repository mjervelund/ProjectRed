package mrtjp.projectred.core.client;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakedQuadVertexBuilder;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.MultiIconTransformation;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A full block whose texture set is oriented by its state's side and rotation. Parts are baked once per state.
 */
public abstract class FullyOrientableBlockModel implements DynamicBlockStateModel {

    private final Map<BlockState, BlockStateModelPart> partMap = new HashMap<>();

    //region Implementation abstracts
    protected abstract RenderData getBlockRenderData(@Nullable BlockState state);

    protected abstract BlockState getItemRenderState();

    protected abstract TextureAtlasSprite getParticleIcon();
    //endregion

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        // DynamicBlockStateModel's state-less overload passes air
        if (state.isAir()) {
            state = getItemRenderState();
        }
        parts.add(getOrGeneratePart(state));
    }

    @Override
    public Material.Baked particleMaterial() {
        return new Material.Baked(getParticleIcon(), false);
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return 0;
    }

    private BlockStateModelPart getOrGeneratePart(BlockState state) {
        synchronized (partMap) {
            return partMap.computeIfAbsent(state, this::generatePart);
        }
    }

    private BlockStateModelPart generatePart(BlockState state) {
        // Prep render
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.computeLighting = false;
        ccrs.brightness = 0;
        BakedQuadVertexBuilder builder = new BakedQuadVertexBuilder();
        ccrs.bind(builder, DefaultVertexFormat.BLOCK);

        // Render full block model with orient transform
        RenderData data = getBlockRenderData(state);
        CCModel m = CCModel.quadModel(24)
                .generateBlock(0, Cuboid6.full, 0)
                .apply(Rotation.sideOrientation(data.side, data.rotation).at(Vector3.CENTER))
                .computeNormals()
                .shrinkUVs(0.0005);

        m.render(ccrs, data.iconT);

        // Bake and separate sides. Full block, so no uncullable quads.
        List<List<BakedQuad>> sides = new ArrayList<>(6);
        for (int s = 0; s < 6; s++) sides.add(new ArrayList<>());
        for (BakedQuad quad : builder.bake()) {
            sides.get(quad.direction().ordinal()).add(quad);
        }
        return new Part(sides, particleMaterial());
    }

    public record RenderData(int side, int rotation, MultiIconTransformation iconT) {
    }

    private record Part(List<List<BakedQuad>> sides, Material.Baked particleMaterial) implements BlockStateModelPart {

        @Override
        public List<BakedQuad> getQuads(@Nullable Direction direction) {
            return direction == null ? List.of() : sides.get(direction.ordinal());
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
}
