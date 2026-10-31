package mrtjp.projectred.integration.client;

import codechicken.lib.vec.RedundantTransformation;
import mrtjp.projectred.core.client.StaticPartModel;
import mrtjp.projectred.integration.part.GatePart;
import net.neoforged.neoforge.model.data.ModelData;

/**
 * Chunk-baked gate geometry ({@link StaticPartModel}). Only called on the client thread, from
 * {@link GatePart#getModelData()}, i.e. when the gate asked for a re-render ({@code tile().markRender()}).
 * Not cached: a gate's static look depends on many fields, and baking only happens when it changes.
 */
public final class GateStaticModels {

    private GateStaticModels() {
    }

    public static ModelData gate(GatePart gate) {
        return StaticPartModel.of(StaticPartModel.bake(ccrs ->
                GateModelRenderer.instance().renderStatic(ccrs, gate, RedundantTransformation.INSTANCE))).toModelData();
    }
}
