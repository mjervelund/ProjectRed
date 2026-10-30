package mrtjp.projectred.expansion.data;

import mrtjp.projectred.expansion.ProjectRedExpansion;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static mrtjp.projectred.expansion.init.ExpansionBlocks.*;

public class ExpansionLootTableProvider extends LootTableProvider {

    /** Registered through {@code GatherDataRegistryEntriesEvent} (loot tables are a registry bootstrap in 26.x). */
    public ExpansionLootTableProvider() {
        super(Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(BlockLootTable::new, LootContextParamSets.BLOCK)
        );
    }

    private static final class BlockLootTable extends BlockLootSubProvider {
        BlockLootTable(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags();
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ProjectRedExpansion.BLOCKS.getEntries()
                    .stream()
                    .map(Holder::value)
                    .toList();
        }

        @Override
        protected void generate() {
            dropSelf(PROJECT_BENCH_BLOCK.get());
            dropSelf(BATTERY_BOX_BLOCK.get());
            dropSelf(AUTO_CRAFTER_BLOCK.get());
            dropSelf(CHARGING_BENCH_BLOCK.get());
            dropSelf(FIRE_STARTER_BLOCK.get());
            dropSelf(FRAME_BLOCK.get());
            dropSelf(FRAME_MOTOR_BLOCK.get());
            dropSelf(FRAME_ACTUATOR_BLOCK.get());
            dropSelf(TRANSPOSER_BLOCK.get());
            dropSelf(BLOCK_BREAKER_BLOCK.get());
            dropSelf(DEPLOYER_BLOCK.get());
        }
    }
}
