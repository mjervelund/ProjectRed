package mrtjp.projectred.transmission.client;

import codechicken.lib.vec.RedundantTransformation;
import codechicken.microblock.api.MicroMaterial;
import codechicken.microblock.api.MicroMaterialClient;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import mrtjp.projectred.core.client.StaticPartModel;
import mrtjp.projectred.transmission.part.BaseCenterWirePart;
import mrtjp.projectred.transmission.part.BaseFaceWirePart;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.neoforged.neoforge.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Chunk-baked wire geometry ({@link StaticPartModel}). Only called on the client thread, from the parts'
 * {@code getModelData()}. Baked models are shared by every wire with the same shape, colour and texture.
 */
public final class WireStaticModels {

    private static final Cache<FaceKey, StaticPartModel> FACE = CacheBuilder.newBuilder().maximumSize(4096).build();
    private static final Cache<CenterKey, QuadCollection> CENTER = CacheBuilder.newBuilder().maximumSize(4096).build();

    private WireStaticModels() {
    }

    public static ModelData face(BaseFaceWirePart wire) {
        FaceKey key = new FaceKey(WireModelRenderer.modelKey(wire), wire.getRenderHue(), wire.getIcon());
        return get(FACE, key, () -> StaticPartModel.of(StaticPartModel.bake(ccrs ->
                WireModelRenderer.render(ccrs, key.modelKey, key.hue, key.icon, RedundantTransformation.INSTANCE))))
                .toModelData();
    }

    public static ModelData center(BaseCenterWirePart wire) {
        int modelKey = FramedWireModelRenderer.modelKey(wire);
        @Nullable MicroMaterial material = wire.getMaterial();
        CenterKey key = new CenterKey(modelKey, wire.getRenderHue(), wire.getIcon(), material != null);
        QuadCollection quads = get(CENTER, key, () -> StaticPartModel.bake(ccrs ->
                FramedWireModelRenderer.renderWire(ccrs, key.modelKey, key.hue, key.icon, key.jacketed)));
        @Nullable BlockStateModelPart wirePart = StaticPartModel.quadsPart(quads);
        if (material == null) {
            return StaticPartModel.of(quads).toModelData();
        }
        // The jacket is a microblock material: collected on the chunk thread with the level, so it gets its tints.
        var boxes = FramedWireModelRenderer.getOrGenerateJacketModel(modelKey).getMaterialBoxes();
        StaticPartModel model = (level, pos, rand, parts) -> {
            if (wirePart != null) parts.add(wirePart);
            MicroMaterialClient client = MicroMaterialClient.get(material);
            if (client != null) {
                client.collectParts(level, pos, boxes, parts);
            }
        };
        return model.toModelData();
    }

    private static <K, V> V get(Cache<K, V> cache, K key, Callable<V> loader) {
        try {
            return cache.get(key, loader);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private record FaceKey(int modelKey, int hue, TextureAtlasSprite icon) { }

    private record CenterKey(int modelKey, int hue, TextureAtlasSprite icon, boolean jacketed) { }
}
