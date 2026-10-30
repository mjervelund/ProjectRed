package mrtjp.projectred.fabrication.data;

import mrtjp.projectred.core.data.ProjectRedModelProvider;
import mrtjp.projectred.integration.init.IntegrationClientInit;
import net.minecraft.data.PackOutput;

import static mrtjp.projectred.fabrication.ProjectRedFabrication.MOD_ID;
import static mrtjp.projectred.fabrication.init.FabricationBlocks.*;
import static mrtjp.projectred.fabrication.init.FabricationItems.*;
import static mrtjp.projectred.fabrication.init.FabricationParts.FABRICATED_GATE_ITEM;

public class FabricationItemModelProvider extends ProjectRedModelProvider {

    public FabricationItemModelProvider(PackOutput output) {
        super(output, MOD_ID);
    }

    @Override
    protected void registerModels() {

        simpleItemBlock(IC_WORKBENCH_BLOCK.get());

        simpleItemBlock(PLOTTING_TABLE_BLOCK.get());
        simpleItemBlock(LITHOGRAPHY_TABLE_BLOCK.get());
        simpleItemBlock(PACKAGING_TABLE_BLOCK.get());

        generated(IC_BLUEPRINT_ITEM);
        generated(BLANK_PHOTOMASK_ITEM);
        generated(PHOTOMASK_SET_ITEM);
        generated(ROUGH_SILICON_WAFER_ITEM);
//        generated(ETCHED_SILICON_WAFER_ITEM);
        generated(VALID_DIE_ITEM);
        generated(INVALID_DIE_ITEM);

        specialItem(FABRICATED_GATE_ITEM.get(), IntegrationClientInit.GATE_ITEM_RENDERER);
   }
}
