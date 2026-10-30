package mrtjp.projectred.core.data;

import mrtjp.projectred.core.block.ProjectRedBlock;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import static mrtjp.projectred.core.ProjectRedCore.MOD_ID;
import static mrtjp.projectred.core.init.CoreBlocks.ELECTROTINE_GENERATOR_BLOCK;
import static mrtjp.projectred.core.init.CoreItems.*;

public class CoreModelProvider extends ProjectRedModelProvider {

    public CoreModelProvider(PackOutput output) {
        super(output, MOD_ID);
    }

    @Override
    public String getName() {
        return "ProjectRed-Core Models";
    }

    @Override
    protected void registerModels() {
        quadStateFrontFacedPoweredMachineModel(ELECTROTINE_GENERATOR_BLOCK.get());

                generated(PLATE_ITEM);
        generated(CONDUCTIVE_PLATE_ITEM);
        generated(WIRED_PLATE_ITEM);
        generated(BUNDLED_PLATE_ITEM);
        generated(PLATFORMED_PLATE_ITEM);
        generated(ANODE_ITEM);
        generated(CATHODE_ITEM);
        generated(POINTER_ITEM);
        generated(SILICON_CHIP_ITEM);
        generated(ENERGIZED_SILICON_CHIP_ITEM);
        generated(RED_ALLOY_INGOT_ITEM);
        generated(ELECTROTINE_ALLOY_INGOT_ITEM);
        generated(ELECTROTINE_DUST_ITEM);
        generated(RUBY_ITEM);
        generated(SAPPHIRE_ITEM);
        generated(PERIDOT_ITEM);
        generated(SAND_COAL_COMP_ITEM);
        generated(RED_IRON_COMP_ITEM);
        generated(ELECTROTINE_IRON_COMP_ITEM);
        generated(SILICON_BOULE_ITEM);
        generated(SILICON_ITEM);
        generated(RED_SILICON_COMP_ITEM);
        generated(GLOW_SILICON_COMP_ITEM);
        generated(ELECTROTINE_SILICON_COMP_ITEM);
        generated(INFUSED_SILICON_ITEM);
        generated(ENERGIZED_SILICON_ITEM);
        generated(ELECTROTINE_SILICON_ITEM);
        generated(COPPER_COIL_ITEM);
        generated(IRON_COIL_ITEM);
        generated(GOLD_COIL_ITEM);
        generated(MOTOR_ITEM);
        generated(WOVEN_CLOTH_ITEM);
        generated(SAIL_ITEM);
        generated(WHITE_ILLUMAR_ITEM);
        generated(ORANGE_ILLUMAR_ITEM);
        generated(MAGENTA_ILLUMAR_ITEM);
        generated(LIGHT_BLUE_ILLUMAR_ITEM);
        generated(YELLOW_ILLUMAR_ITEM);
        generated(LIME_ILLUMAR_ITEM);
        generated(PINK_ILLUMAR_ITEM);
        generated(GRAY_ILLUMAR_ITEM);
        generated(LIGHT_GRAY_ILLUMAR_ITEM);
        generated(CYAN_ILLUMAR_ITEM);
        generated(PURPLE_ILLUMAR_ITEM);
        generated(BLUE_ILLUMAR_ITEM);
        generated(BROWN_ILLUMAR_ITEM);
        generated(GREEN_ILLUMAR_ITEM);
        generated(RED_ILLUMAR_ITEM);
        generated(BLACK_ILLUMAR_ITEM);

        generated(DRAW_PLATE_ITEM);
        generated(SCREWDRIVER_ITEM);
        generated(MULTIMETER_ITEM);
    }

    private void quadStateFrontFacedPoweredMachineModel(Block block) {
        Identifier idleModel = createFrontFacedPoweredMachineModel(block, 0);
        Identifier chargedModel = createFrontFacedPoweredMachineModel(block, 1);
        Identifier workingModel = createFrontFacedPoweredMachineModel(block, 2);
        Identifier chargedWorkingModel = createFrontFacedPoweredMachineModel(block, 3);

        forAllStates(block, state -> {
            int r = state.getValue(ProjectRedBlock.ROTATION);
            boolean isWorking = state.getValue(ProjectRedBlock.WORKING);
            boolean isCharged = state.getValue(ProjectRedBlock.CHARGED);

            Identifier model = !isWorking && !isCharged ? idleModel :
                    isWorking && !isCharged ? workingModel :
                    !isWorking && isCharged ? chargedModel :
                    chargedWorkingModel;

            return rotateY(new Variant(model), r * 90);
        });
        itemWithModel(block, idleModel);
    }

    private Identifier createFrontFacedPoweredMachineModel(Block block, int state) {
        String texture = name(block);
        String modelName = texture + (state > 0 ? "_state" + state : "");

        return orientableWithBottom(modelName,
                modLoc("block/" + texture + "_side"),
                modLoc("block/" + texture + "_front_" + state),
                modLoc("block/" + texture + "_bottom"),
                modLoc("block/" + texture + "_top"));
    }
}
