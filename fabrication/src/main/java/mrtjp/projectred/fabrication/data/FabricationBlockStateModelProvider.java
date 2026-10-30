package mrtjp.projectred.fabrication.data;

import mrtjp.projectred.core.data.ProjectRedModelProvider;
import mrtjp.projectred.core.block.ProjectRedBlock;
import mrtjp.projectred.fabrication.ProjectRedFabrication;
import mrtjp.projectred.fabrication.block.FabricationMachineBlock;
import mrtjp.projectred.fabrication.block.ICWorkbenchBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

import javax.annotation.Nonnull;

import static mrtjp.projectred.fabrication.init.FabricationBlocks.*;

@SuppressWarnings("DataFlowIssue")
public class FabricationBlockStateModelProvider extends ProjectRedModelProvider {

    public FabricationBlockStateModelProvider(PackOutput output) {
        super(output, ProjectRedFabrication.MOD_ID);
    }

    @Override
    public @Nonnull String getName() {
        return "ProjectRed-Fabrication Block Models";
    }

    @Override
    protected void registerModels() {

        addICWorkbenchVariants(IC_WORKBENCH_BLOCK.get());

        addFabricationMachineVariants(PLOTTING_TABLE_BLOCK.get());
        addFabricationMachineVariants(LITHOGRAPHY_TABLE_BLOCK.get());
        addFabricationMachineVariants(PACKAGING_TABLE_BLOCK.get());
    }

    private static final ModelTemplate DOMED_MACHINE = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(ProjectRedFabrication.MOD_ID, "block/domed_machine")), Optional.empty(),
            TextureSlot.DOWN, TextureSlot.UP, TextureSlot.NORTH, TextureSlot.SOUTH, TextureSlot.WEST, TextureSlot.EAST);

    private void addICWorkbenchVariants(Block block) {
        Identifier empty = createICWorkbenchModel(block, false);
        Identifier full = createICWorkbenchModel(block, true);
        forAllStates(block, state -> BlockModelGenerators.plainModel(state.getValue(ICWorkbenchBlock.BLUEPRINT_PROPERTY) ? full : empty));
    }

    private void addFabricationMachineVariants(Block block) {
        addRotatableDomedMachineVariants(block,
                createDomedMachineModelFileForBlock(block, 2),
                createDomedMachineModelFileForBlock(block, 1),
                createDomedMachineModelFileForBlock(block, 0));
    }

    private void addRotatableDomedMachineVariants(Block block, Identifier workingModel, Identifier chargedModel, Identifier idleModel) {
        forAllStates(block, state -> {
            int r = state.getValue(ProjectRedBlock.ROTATION);
            boolean isWorking = state.getValue(FabricationMachineBlock.WORKING);
            boolean isCharged = state.getValue(FabricationMachineBlock.CHARGED);

            Identifier modelFile = isWorking && isCharged ? workingModel : isCharged ? chargedModel : idleModel;
            return rotateY(BlockModelGenerators.plainModel(modelFile), r * 90);
        });
    }

    private Identifier createDomedMachineModelFileForBlock(Block block, int chargeState) {
        String textureName = BuiltInRegistries.BLOCK.getKey(block).getPath();
        String modelName = textureName + (chargeState > 0 ? "_state" + chargeState : "");
        return DOMED_MACHINE.create(modLoc("block/" + modelName), new TextureMapping()
                .put(TextureSlot.DOWN, new Material(modLoc("block/" + textureName + "_bottom")))
                .put(TextureSlot.UP, new Material(modLoc("block/" + textureName + "_top")))
                .put(TextureSlot.NORTH, new Material(modLoc("block/" + textureName + "_front_" + chargeState)))
                .put(TextureSlot.SOUTH, new Material(modLoc("block/" + textureName + "_side")))
                .put(TextureSlot.WEST, new Material(modLoc("block/" + textureName + "_side")))
                .put(TextureSlot.EAST, new Material(modLoc("block/" + textureName + "_side"))), blockModels.modelOutput);
    }

    private Identifier createICWorkbenchModel(Block block, boolean hasBlueprint) {
        String textureName = BuiltInRegistries.BLOCK.getKey(block).getPath();
        String suffix = hasBlueprint ? "" : "_empty";
        String modelName = textureName + suffix;
        return cube(modelName,
                modLoc("block/" + textureName + "_bottom"),
                modLoc("block/" + textureName + "_top" + suffix),
                modLoc("block/" + textureName + "_front" + suffix),
                modLoc("block/" + textureName + "_front" + suffix),
                modLoc("block/" + textureName + "_side" + suffix),
                modLoc("block/" + textureName + "_side" + suffix));
    }
}
