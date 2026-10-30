package mrtjp.projectred.illumination.data;

import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.resources.Identifier;
import java.util.HashMap;
import java.util.Map;
import mrtjp.projectred.core.data.ProjectRedModelProvider;
import mrtjp.projectred.core.block.ProjectRedBlock;
import mrtjp.projectred.illumination.BlockLightType;
import mrtjp.projectred.illumination.ProjectRedIllumination;
import mrtjp.projectred.illumination.block.IllumarLampBlock;
import mrtjp.projectred.illumination.block.IllumarSmartLampBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import static mrtjp.projectred.illumination.init.IlluminationBlocks.ILLUMAR_SMART_LAMP;

public class IlluminationBlockStateModelProvider extends ProjectRedModelProvider {

    // XY rotations for non-rotatable sided devices
    private static final int[][] DEVICE_SIDED_ROTATIONS = { {0, 0}, {2, 2}, {1, 2}, {1, 0}, {1, 1}, {1, 3} };

    public IlluminationBlockStateModelProvider(PackOutput output) {
        super(output, ProjectRedIllumination.MOD_ID);
    }

    @Override
    protected void registerModels() {
        for (BlockLightType lampType : BlockLightType.values()) {
            for (int color = 0; color < 16; color++) {
                addIllumarLampVariants(lampType.getBlock(color, true));
                addIllumarLampVariants(lampType.getBlock(color, false));
            }
        }

        addSidedBlockVariants(ILLUMAR_SMART_LAMP.get(),
                createSmartLampModel(ILLUMAR_SMART_LAMP.get(), false),
                createSmartLampModel(ILLUMAR_SMART_LAMP.get(), true));
    }

    private final Map<String, Identifier> lampModels = new HashMap<>();

    private void addIllumarLampVariants(Block block) {
        forAllStates(block, state -> new Variant(createLampModel(block, state.getValue(IllumarLampBlock.LIT))));
    }

    private Identifier createLampModel(Block block, boolean lit) {
        String textureName = BlockLightType.ILLUMAR_LAMP.getRegistryID(((IllumarLampBlock) block).getColor(), false) + (lit ? "_on" : ""); // Always use non-inverted unlocal name for textures
        return lampModels.computeIfAbsent(textureName, n -> cubeAll(n, modLoc("block/" + n)));
    }

    private void addSidedBlockVariants(Block block, Identifier offModel, Identifier onModel) {
        forAllStates(block, state -> {
            int s = state.getValue(ProjectRedBlock.SIDE);
            boolean lit = state.getValue(IllumarSmartLampBlock.LEVEL) > 0;
            return rotateY(rotateX(new Variant(lit ? onModel : offModel), DEVICE_SIDED_ROTATIONS[s][0] * 90), DEVICE_SIDED_ROTATIONS[s][1] * 90);
        });
    }

    private Identifier createSmartLampModel(Block block, boolean lit) {
        String texture = BuiltInRegistries.BLOCK.getKey(block).getPath();
        String litKey = lit ? "_on" : "";
        String modelName = texture + litKey;
        return cubeBottomTop(modelName,
                modLoc("block/" + texture + "_side" + litKey),
                modLoc("block/" + texture + "_bottom"),
                modLoc("block/" + texture + "_top" + litKey));
    }
}
