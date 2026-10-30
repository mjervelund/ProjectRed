package mrtjp.projectred.expansion.data;

import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.BlockModelGenerators;
import mrtjp.projectred.core.data.ProjectRedModelProvider;
import mrtjp.projectred.core.block.ProjectRedBlock;
import mrtjp.projectred.expansion.block.BatteryBoxBlock;
import mrtjp.projectred.expansion.client.FrameBlockModel;
import mrtjp.projectred.expansion.client.FrameMotorBlockModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import static mrtjp.projectred.expansion.ProjectRedExpansion.MOD_ID;
import static mrtjp.projectred.expansion.init.ExpansionBlocks.*;

@SuppressWarnings("DataFlowIssue")
public class ExpansionBlockStateModelProvider extends ProjectRedModelProvider {

    // XY rotations for non-rotatable sided devices
    private static final int[][] DEVICE_SIDED_ROTATIONS = { {0, 0}, {2, 2}, {1, 2}, {1, 0}, {1, 1}, {1, 3} };

    public ExpansionBlockStateModelProvider(PackOutput output) {
        super(output, MOD_ID);
    }

    @Override
    protected void registerModels() {
        addRotatableOppositeMatchingFacesBlock(PROJECT_BENCH_BLOCK.get());
        addBatteryBoxBlock(BATTERY_BOX_BLOCK.get());
        addTriStateFrontFacedPoweredMachineBlock(AUTO_CRAFTER_BLOCK.get());
        addBiStateSideAndTopMachineBlock(CHARGING_BENCH_BLOCK.get());
        addSidedOppositeMatchingFacesDeviceBlock(FIRE_STARTER_BLOCK.get());
        addTriStateSidedPoweredDeviceBlock(FRAME_ACTUATOR_BLOCK.get());
        addSideAndTopActiveModel(TRANSPOSER_BLOCK.get());
        addSideAndTopActiveModel(BLOCK_BREAKER_BLOCK.get());
        addSidedOppositeMatchingFacesDeviceBlock(DEPLOYER_BLOCK.get());

        // Advanced models rendered programmatically. Only Particle info provided by model file
        addDynamicModel(FRAME_BLOCK.get(), new FrameBlockModel.Unbaked());
        addDynamicModel(FRAME_MOTOR_BLOCK.get(), new FrameMotorBlockModel.Unbaked());
    }

    private void addDynamicModel(Block block, CustomUnbakedBlockStateModel model) {
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block,
                MultiVariant.of(new CustomBlockStateModelBuilder.Simple(model))));
    }

    private void addRotatableOppositeMatchingFacesBlock(Block block) {
        addRotatableVariants(block, createOppositeMatchingFaceModel(block));
    }

    private void addBatteryBoxBlock(Block block) {
        forAllStates(block, state -> {
            int charge = state.getValue(BatteryBoxBlock.CHARGE_LEVEL);
            return new Variant(createBatteryModel(block, charge));
        });
    }

    private void addTriStateFrontFacedPoweredMachineBlock(Block block) {

        Identifier m0 = createFrontFacedMachineModel(block, 0);
        Identifier m1 = createFrontFacedMachineModel(block, 1);
        Identifier m3 = createFrontFacedMachineModel(block, 2);

        // m2 not possible here (cannot be working and not charged)
        addRotatablePoweredMachineVariants(block, m0, m1, m1, m3);
    }

    private void addBiStateSideAndTopMachineBlock(Block block) {

        Identifier m0 = createSideAndTopStateModel(block, 0);
        Identifier m1 = createSideAndTopStateModel(block, 1);

        addChargedMachineVariants(block, m0, m1);
    }

    private void addSidedOppositeMatchingFacesDeviceBlock(Block block) {
        Identifier inactive = createOppositeMatchingFaceDeviceModel(block, false);
        Identifier active = createOppositeMatchingFaceDeviceModel(block, true);

        addSidedDeviceVariants(block, inactive, active);
    }

    private void addTriStateSidedPoweredDeviceBlock(Block block) {
        Identifier m0 = createSideStateModel(block, 0);
        Identifier m1 = createSideStateModel(block, 1);
        Identifier m2 = createSideStateModel(block, 2);

        addSidedPoweredDeviceVariants(block, m0, m1, m1, m2);
    }

    private void addSideAndTopActiveModel(Block block) {
        Identifier inactive = createSideAndTopActiveModel(block, false);
        Identifier active = createSideAndTopActiveModel(block, true);

        addSidedDeviceVariants(block, inactive, active);
    }

    private void addRotatableVariants(Block block, Identifier model) {
        forAllStates(block, state -> rotateY(new Variant(model), state.getValue(ProjectRedBlock.ROTATION) * 90));
    }

    private void addRotatablePoweredMachineVariants(Block block, Identifier idle, Identifier charged, Identifier working, Identifier chargedAndWorking) {
        forAllStates(block, state -> {
            int r = state.getValue(ProjectRedBlock.ROTATION);
            boolean isWorking = state.getValue(ProjectRedBlock.WORKING);
            boolean isCharged = state.getValue(ProjectRedBlock.CHARGED);

            Identifier model = isWorking && isCharged ? chargedAndWorking
                    : isWorking ? working
                    : isCharged ? charged
                    : idle;

            return rotateY(new Variant(model), r * 90);
        });
    }

    private void addChargedMachineVariants(Block block, Identifier idle, Identifier charged) {
        forAllStates(block, state -> {
            boolean isCharged = state.getValue(ProjectRedBlock.CHARGED);

            Identifier model = isCharged ? charged : idle;
            return new Variant(model);
        });
    }

    private void addSidedDeviceVariants(Block block, Identifier inactive, Identifier active) {
        forAllStates(block, state -> {
            int s = state.getValue(ProjectRedBlock.SIDE);
            boolean isActive = state.getValue(ProjectRedBlock.ACTIVE);

            return rotateY(rotateX(new Variant(isActive ? active : inactive), DEVICE_SIDED_ROTATIONS[s][0] * 90), DEVICE_SIDED_ROTATIONS[s][1] * 90);
        });
    }

    private void addSidedPoweredDeviceVariants(Block block, Identifier idle, Identifier charged, Identifier working, Identifier chargedAndWorking) {
        forAllStates(block, state -> {
            int s = state.getValue(ProjectRedBlock.SIDE);
            boolean isWorking = state.getValue(ProjectRedBlock.WORKING);
            boolean isCharged = state.getValue(ProjectRedBlock.CHARGED);
            Identifier model = isWorking && isCharged ? chargedAndWorking
                    : isWorking ? working
                    : isCharged ? charged
                    : idle;

            return rotateY(rotateX(new Variant(model), DEVICE_SIDED_ROTATIONS[s][0] * 90), DEVICE_SIDED_ROTATIONS[s][1] * 90);
        });
    }

    private Identifier createOppositeMatchingFaceModel(Block block) {
        String texture = BuiltInRegistries.BLOCK.getKey(block).getPath();
        return cube(texture,
                        modLoc("block/" + texture + "_bottom"),
                        modLoc("block/" + texture + "_top"),
                        modLoc("block/" + texture + "_front_back"),
                        modLoc("block/" + texture + "_front_back"),
                        modLoc("block/" + texture + "_left_right"),
                        modLoc("block/" + texture + "_left_right"));
    }

    private Identifier createBatteryModel(Block block, int charge) {
        String texture = BuiltInRegistries.BLOCK.getKey(block).getPath();
        String modelName = texture + (charge > 0 ? "_charge" + charge : "");
        return cubeBottomTop(modelName,
                modLoc("block/" + texture + "_side_" + charge),
                modLoc("block/" + texture + "_bottom"),
                modLoc("block/" + texture + "_top"));
    }

    private Identifier createFrontFacedMachineModel(Block block, int state) {
        String texture = BuiltInRegistries.BLOCK.getKey(block).getPath();
        String modelName = texture + (state > 0 ? "_state" + state : "");
        return orientableWithBottom(modelName,
                modLoc("block/" + texture + "_side"),
                modLoc("block/" + texture + "_front_" + state),
                modLoc("block/" + texture + "_bottom"),
                modLoc("block/" + texture + "_top"));
    }

    private Identifier createSideAndTopStateModel(Block block, int state) {
        String texture = BuiltInRegistries.BLOCK.getKey(block).getPath();
        String modelName = texture + (state > 0 ? "_state" + state : "");
        return cubeBottomTop(modelName,
                modLoc("block/" + texture + "_side_" + state),
                modLoc("block/" + texture + "_bottom"),
                modLoc("block/" + texture + "_top_" + state));
    }

    private Identifier createSideStateModel(Block block, int state) {
        String texture = BuiltInRegistries.BLOCK.getKey(block).getPath();
        String modelName = texture + (state > 0 ? "_state" + state : "");
        return cubeBottomTop(modelName,
                modLoc("block/" + texture + "_side_" + state),
                modLoc("block/" + texture + "_bottom"),
                modLoc("block/" + texture + "_top"));
    }

    private Identifier createSideAndTopActiveModel(Block block, boolean active) {
        String texture = BuiltInRegistries.BLOCK.getKey(block).getPath();
        String activeKey = active ? "_active" : "";
        String modelName = texture + activeKey;
        return cubeBottomTop(modelName,
                modLoc("block/" + texture + "_side" + activeKey),
                modLoc("block/" + texture + "_bottom"),
                modLoc("block/" + texture + "_top" + activeKey));
    }

    private Identifier createOppositeMatchingFaceDeviceModel(Block block, boolean active) {
        String texture = BuiltInRegistries.BLOCK.getKey(block).getPath();
        String activeKey = active ? "_active" : "";
        String modelName = texture + activeKey;
        return cube(modelName,
                        modLoc("block/" + texture + "_bottom" + activeKey),
                        modLoc("block/" + texture + "_top" + activeKey),
                        modLoc("block/" + texture + "_front_back" + activeKey),
                        modLoc("block/" + texture + "_front_back" + activeKey),
                        modLoc("block/" + texture + "_left_right" + activeKey),
                        modLoc("block/" + texture + "_left_right" + activeKey));
    }
}
