package mrtjp.projectred.expansion.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static mrtjp.projectred.expansion.ProjectRedExpansion.MOD_ID;
import static mrtjp.projectred.expansion.init.ExpansionBlocks.*;

public class ExpansionBlockTagsProvider extends BlockTagsProvider {

    public ExpansionBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {

        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(BATTERY_BOX_BLOCK.get().builtInRegistryHolder().key())
                .add(FRAME_BLOCK.get().builtInRegistryHolder().key());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(PROJECT_BENCH_BLOCK.get().builtInRegistryHolder().key())
                .add(CHARGING_BENCH_BLOCK.get().builtInRegistryHolder().key())
                .add(AUTO_CRAFTER_BLOCK.get().builtInRegistryHolder().key())
                .add(FIRE_STARTER_BLOCK.get().builtInRegistryHolder().key())
                .add(FRAME_MOTOR_BLOCK.get().builtInRegistryHolder().key())
                .add(FRAME_ACTUATOR_BLOCK.get().builtInRegistryHolder().key())
                .add(TRANSPOSER_BLOCK.get().builtInRegistryHolder().key())
                .add(BLOCK_BREAKER_BLOCK.get().builtInRegistryHolder().key())
                .add(DEPLOYER_BLOCK.get().builtInRegistryHolder().key());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(PROJECT_BENCH_BLOCK.get().builtInRegistryHolder().key())
                .add(CHARGING_BENCH_BLOCK.get().builtInRegistryHolder().key())
                .add(AUTO_CRAFTER_BLOCK.get().builtInRegistryHolder().key())
                .add(FIRE_STARTER_BLOCK.get().builtInRegistryHolder().key())
                .add(FRAME_MOTOR_BLOCK.get().builtInRegistryHolder().key())
                .add(FRAME_ACTUATOR_BLOCK.get().builtInRegistryHolder().key())
                .add(TRANSPOSER_BLOCK.get().builtInRegistryHolder().key())
                .add(BLOCK_BREAKER_BLOCK.get().builtInRegistryHolder().key())
                .add(DEPLOYER_BLOCK.get().builtInRegistryHolder().key());
    }
}
