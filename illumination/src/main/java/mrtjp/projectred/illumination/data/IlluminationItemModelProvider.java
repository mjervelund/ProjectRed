package mrtjp.projectred.illumination.data;

import mrtjp.projectred.illumination.init.IlluminationClientInit;
import mrtjp.projectred.core.data.ProjectRedModelProvider;
import mrtjp.projectred.illumination.BlockLightType;
import mrtjp.projectred.illumination.MultipartLightType;
import net.minecraft.data.PackOutput;

import static mrtjp.projectred.illumination.ProjectRedIllumination.MOD_ID;
import static mrtjp.projectred.illumination.init.IlluminationBlocks.ILLUMAR_SMART_LAMP;

public class IlluminationItemModelProvider extends ProjectRedModelProvider {

    public IlluminationItemModelProvider(PackOutput output) {
        super(output, MOD_ID);
    }

    @Override
    protected void registerModels() {

        for (BlockLightType type : BlockLightType.values()) {
            for (int color = 0; color < 16; color++) {
                itemWithModel(type.getBlock(color, false), modLoc("block/" + type.getRegistryID(color, false)));
                // Inverted lamps draw their glow, so they use the lamp item renderer
                specialItem(type.getBlock(color, true), IlluminationClientInit.LAMP_ITEM_RENDERER);
            }
        }

        specialItem(ILLUMAR_SMART_LAMP.get(), IlluminationClientInit.SMART_LAMP_ITEM_RENDERER);

        for (MultipartLightType type : MultipartLightType.values()) {
            for (int color = 0; color < 16; color++) {
                specialItem(type.getItem(color, false), IlluminationClientInit.LIGHT_ITEM_RENDERER);
                specialItem(type.getItem(color, true), IlluminationClientInit.LIGHT_ITEM_RENDERER);
            }
        }
    }
}
