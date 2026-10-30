package mrtjp.projectred.exploration.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static mrtjp.projectred.exploration.ProjectRedExploration.MOD_ID;
import static mrtjp.projectred.exploration.init.ExplorationBlocks.*;
import static mrtjp.projectred.exploration.init.ExplorationTags.*;

public class ExplorationBlockTagsProvider extends BlockTagsProvider {

    public ExplorationBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        tag(BlockTags.WALLS).add(
                MARBLE_WALL.get(),
                MARBLE_BRICK_WALL.get(),
                BASALT_WALL.get(),
                BASALT_COBBLE_WALL.get(),
                BASALT_BRICK_WALL.get(),
                RUBY_BLOCK_WALL.get(),
                SAPPHIRE_BLOCK_WALL.get(),
                PERIDOT_BLOCK_WALL.get(),
                ELECTROTINE_BLOCK_WALL.get());

        tag(Tags.Blocks.ORES)
                .addTag(RUBY_ORES_BLOCK_TAG)
                .addTag(SAPPHIRE_ORES_BLOCK_TAG)
                .addTag(PERIDOT_ORES_BLOCK_TAG)
                .addTag(TIN_ORES_BLOCK_TAG)
                .addTag(SILVER_ORES_BLOCK_TAG)
                .addTag(ELECTROTINE_ORES_BLOCK_TAG);

        tag(Tags.Blocks.STORAGE_BLOCKS)
                .addTag(RUBY_STORAGE_BLOCK_TAG)
                .addTag(SAPPHIRE_STORAGE_BLOCK_TAG)
                .addTag(PERIDOT_STORAGE_BLOCK_TAG)
                .addTag(ELECTROTINE_STORAGE_BLOCK_TAG)
                .addTag(RAW_TIN_STORAGE_BLOCK_TAG)
                .addTag(TIN_STORAGE_BLOCK_TAG)
                .addTag(RAW_SILVER_STORAGE_BLOCK_TAG)
                .addTag(SILVER_STORAGE_BLOCK_TAG);

        /* Attach blocks to tags */

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(RUBY_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(DEEPSLATE_RUBY_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(SAPPHIRE_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(DEEPSLATE_SAPPHIRE_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(PERIDOT_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(DEEPSLATE_PERIDOT_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(ELECTROTINE_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(DEEPSLATE_ELECTROTINE_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(TIN_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(DEEPSLATE_TIN_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(SILVER_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(DEEPSLATE_SILVER_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(MARBLE_BLOCK.get().builtInRegistryHolder().key())
                .add(MARBLE_BRICK_BLOCK.get().builtInRegistryHolder().key())
                .add(BASALT_BLOCK.get().builtInRegistryHolder().key())
                .add(BASALT_COBBLE_BLOCK.get().builtInRegistryHolder().key())
                .add(RUBY_BLOCK.get().builtInRegistryHolder().key())
                .add(SAPPHIRE_BLOCK.get().builtInRegistryHolder().key())
                .add(PERIDOT_BLOCK.get().builtInRegistryHolder().key())
                .add(ELECTROTINE_BLOCK.get().builtInRegistryHolder().key())
                .add(RAW_TIN_BLOCK.get().builtInRegistryHolder().key())
                .add(TIN_BLOCK.get().builtInRegistryHolder().key())
                .add(RAW_SILVER_BLOCK.get().builtInRegistryHolder().key())
                .add(SILVER_BLOCK.get().builtInRegistryHolder().key())
                .add(MARBLE_WALL.get().builtInRegistryHolder().key())
                .add(MARBLE_BRICK_WALL.get().builtInRegistryHolder().key())
                .add(BASALT_WALL.get().builtInRegistryHolder().key())
                .add(BASALT_COBBLE_WALL.get().builtInRegistryHolder().key())
                .add(BASALT_BRICK_WALL.get().builtInRegistryHolder().key())
                .add(RUBY_BLOCK_WALL.get().builtInRegistryHolder().key())
                .add(SAPPHIRE_BLOCK_WALL.get().builtInRegistryHolder().key())
                .add(PERIDOT_BLOCK_WALL.get().builtInRegistryHolder().key())
                .add(ELECTROTINE_BLOCK_WALL.get().builtInRegistryHolder().key());

        tag(BlockTags.NEEDS_STONE_TOOL)
                // Ores
                .add(TIN_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(DEEPSLATE_TIN_ORE_BLOCK.get().builtInRegistryHolder().key())
                // Decorative blocks
                .add(MARBLE_BLOCK.get().builtInRegistryHolder().key())
                .add(MARBLE_BRICK_BLOCK.get().builtInRegistryHolder().key())
                .add(BASALT_BLOCK.get().builtInRegistryHolder().key())
                .add(BASALT_COBBLE_BLOCK.get().builtInRegistryHolder().key())
                .add(BASALT_BRICK_BLOCK.get().builtInRegistryHolder().key())
                .add(ELECTROTINE_BLOCK.get().builtInRegistryHolder().key())
                .add(RAW_TIN_BLOCK.get().builtInRegistryHolder().key())
                .add(TIN_BLOCK.get().builtInRegistryHolder().key())
                // Walls
                .add(MARBLE_WALL.get().builtInRegistryHolder().key())
                .add(MARBLE_BRICK_WALL.get().builtInRegistryHolder().key())
                .add(BASALT_WALL.get().builtInRegistryHolder().key())
                .add(BASALT_COBBLE_WALL.get().builtInRegistryHolder().key())
                .add(BASALT_BRICK_WALL.get().builtInRegistryHolder().key())
                .add(ELECTROTINE_BLOCK_WALL.get().builtInRegistryHolder().key());

        tag(BlockTags.NEEDS_IRON_TOOL)
                // Ores
                .add(RUBY_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(DEEPSLATE_RUBY_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(SAPPHIRE_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(DEEPSLATE_SAPPHIRE_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(PERIDOT_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(DEEPSLATE_PERIDOT_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(ELECTROTINE_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(DEEPSLATE_ELECTROTINE_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(SILVER_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(DEEPSLATE_SILVER_ORE_BLOCK.get().builtInRegistryHolder().key())
                // Decorative blocks
                .add(RUBY_BLOCK.get().builtInRegistryHolder().key())
                .add(SAPPHIRE_BLOCK.get().builtInRegistryHolder().key())
                .add(PERIDOT_BLOCK.get().builtInRegistryHolder().key())
                .add(RAW_SILVER_BLOCK.get().builtInRegistryHolder().key())
                .add(SILVER_BLOCK.get().builtInRegistryHolder().key())
                // Walls
                .add(RUBY_BLOCK_WALL.get().builtInRegistryHolder().key())
                .add(SAPPHIRE_BLOCK_WALL.get().builtInRegistryHolder().key())
                .add(PERIDOT_BLOCK_WALL.get().builtInRegistryHolder().key());

        tag(RUBY_ORES_BLOCK_TAG).add(RUBY_ORE_BLOCK.get().builtInRegistryHolder().key()).add(DEEPSLATE_RUBY_ORE_BLOCK.get().builtInRegistryHolder().key());
        tag(SAPPHIRE_ORES_BLOCK_TAG).add(SAPPHIRE_ORE_BLOCK.get().builtInRegistryHolder().key()).add(DEEPSLATE_SAPPHIRE_ORE_BLOCK.get().builtInRegistryHolder().key());
        tag(PERIDOT_ORES_BLOCK_TAG).add(PERIDOT_ORE_BLOCK.get().builtInRegistryHolder().key()).add(DEEPSLATE_PERIDOT_ORE_BLOCK.get().builtInRegistryHolder().key());
        tag(TIN_ORES_BLOCK_TAG).add(TIN_ORE_BLOCK.get().builtInRegistryHolder().key()).add(DEEPSLATE_TIN_ORE_BLOCK.get().builtInRegistryHolder().key());
        tag(SILVER_ORES_BLOCK_TAG).add(SILVER_ORE_BLOCK.get().builtInRegistryHolder().key()).add(DEEPSLATE_SILVER_ORE_BLOCK.get().builtInRegistryHolder().key());
        tag(ELECTROTINE_ORES_BLOCK_TAG).add(ELECTROTINE_ORE_BLOCK.get().builtInRegistryHolder().key()).add(DEEPSLATE_ELECTROTINE_ORE_BLOCK.get().builtInRegistryHolder().key());

        tag(Tags.Blocks.ORES_IN_GROUND_STONE)
                .add(RUBY_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(SAPPHIRE_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(PERIDOT_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(TIN_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(SILVER_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(ELECTROTINE_ORE_BLOCK.get().builtInRegistryHolder().key());

        tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE)
                .add(DEEPSLATE_RUBY_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(DEEPSLATE_SAPPHIRE_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(DEEPSLATE_PERIDOT_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(DEEPSLATE_TIN_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(DEEPSLATE_SILVER_ORE_BLOCK.get().builtInRegistryHolder().key())
                .add(DEEPSLATE_ELECTROTINE_ORE_BLOCK.get().builtInRegistryHolder().key());

        tag(MARBLE_BLOCK_TAG).add(MARBLE_BLOCK.get().builtInRegistryHolder().key());
        tag(BASALT_BLOCK_TAG).add(BASALT_BLOCK.get().builtInRegistryHolder().key());
        tag(RUBY_STORAGE_BLOCK_TAG).add(RUBY_BLOCK.get().builtInRegistryHolder().key());
        tag(SAPPHIRE_STORAGE_BLOCK_TAG).add(SAPPHIRE_BLOCK.get().builtInRegistryHolder().key());
        tag(PERIDOT_STORAGE_BLOCK_TAG).add(PERIDOT_BLOCK.get().builtInRegistryHolder().key());
        tag(ELECTROTINE_STORAGE_BLOCK_TAG).add(ELECTROTINE_BLOCK.get().builtInRegistryHolder().key());
        tag(RAW_TIN_STORAGE_BLOCK_TAG).add(RAW_TIN_BLOCK.get().builtInRegistryHolder().key());
        tag(TIN_STORAGE_BLOCK_TAG).add(TIN_BLOCK.get().builtInRegistryHolder().key());
        tag(RAW_SILVER_STORAGE_BLOCK_TAG).add(RAW_SILVER_BLOCK.get().builtInRegistryHolder().key());
        tag(SILVER_STORAGE_BLOCK_TAG).add(SILVER_BLOCK.get().builtInRegistryHolder().key());
    }
}
