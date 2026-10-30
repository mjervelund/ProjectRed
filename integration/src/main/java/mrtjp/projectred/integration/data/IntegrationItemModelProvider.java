package mrtjp.projectred.integration.data;

import mrtjp.projectred.core.data.ProjectRedModelProvider;
import mrtjp.projectred.integration.GateType;
import mrtjp.projectred.integration.client.GatePartItemRenderer;
import net.minecraft.data.PackOutput;

import static mrtjp.projectred.integration.ProjectRedIntegration.MOD_ID;

public class IntegrationItemModelProvider extends ProjectRedModelProvider {

    public IntegrationItemModelProvider(PackOutput output) {
        super(output, MOD_ID);
    }

    @Override
    protected void registerModels() {

        for (GateType type : GateType.values()) {
            if (type.isExternalGate()) continue;
            clazz(type.getItem(), GatePartItemRenderer.class);
        }
    }
}
