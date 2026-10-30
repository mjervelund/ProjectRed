package mrtjp.projectred.core.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

import static mrtjp.projectred.core.ProjectRedCore.MOD_ID;
import static mrtjp.projectred.core.init.CoreItems.*;
import static mrtjp.projectred.core.init.CoreTags.*;

public class CoreItemTagsProvider extends ItemTagsProvider {

    public CoreItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        tag(Tags.Items.INGOTS)
                .addTag(RED_ALLOY_INGOT_TAG)
                .addTag(ELECTROTINE_ALLOY_INGOT_TAG);

        tag(Tags.Items.GEMS)
                .addTag(RUBY_GEM_TAG)
                .addTag(SAPPHIRE_GEM_TAG)
                .addTag(PERIDOT_GEM_TAG);

        tag(Tags.Items.DUSTS)
                .addTag(ELECTROTINE_DUST_TAG);

        tag(RED_ALLOY_INGOT_TAG).add(RED_ALLOY_INGOT_ITEM.get().builtInRegistryHolder().key());
        tag(ELECTROTINE_ALLOY_INGOT_TAG).add(ELECTROTINE_ALLOY_INGOT_ITEM.get().builtInRegistryHolder().key());
        tag(RUBY_GEM_TAG).add(RUBY_ITEM.get().builtInRegistryHolder().key());
        tag(SAPPHIRE_GEM_TAG).add(SAPPHIRE_ITEM.get().builtInRegistryHolder().key());
        tag(PERIDOT_GEM_TAG).add(PERIDOT_ITEM.get().builtInRegistryHolder().key());
        tag(ELECTROTINE_DUST_TAG).add(ELECTROTINE_DUST_ITEM.get().builtInRegistryHolder().key());
        tag(ILLUMAR_TAG)
                .add(WHITE_ILLUMAR_ITEM.get().builtInRegistryHolder().key())
                .add(ORANGE_ILLUMAR_ITEM.get().builtInRegistryHolder().key())
                .add(MAGENTA_ILLUMAR_ITEM.get().builtInRegistryHolder().key())
                .add(LIGHT_BLUE_ILLUMAR_ITEM.get().builtInRegistryHolder().key())
                .add(YELLOW_ILLUMAR_ITEM.get().builtInRegistryHolder().key())
                .add(LIME_ILLUMAR_ITEM.get().builtInRegistryHolder().key())
                .add(PINK_ILLUMAR_ITEM.get().builtInRegistryHolder().key())
                .add(GRAY_ILLUMAR_ITEM.get().builtInRegistryHolder().key())
                .add(LIGHT_GRAY_ILLUMAR_ITEM.get().builtInRegistryHolder().key())
                .add(CYAN_ILLUMAR_ITEM.get().builtInRegistryHolder().key())
                .add(PURPLE_ILLUMAR_ITEM.get().builtInRegistryHolder().key())
                .add(BLUE_ILLUMAR_ITEM.get().builtInRegistryHolder().key())
                .add(BROWN_ILLUMAR_ITEM.get().builtInRegistryHolder().key())
                .add(GREEN_ILLUMAR_ITEM.get().builtInRegistryHolder().key())
                .add(RED_ILLUMAR_ITEM.get().builtInRegistryHolder().key())
                .add(BLACK_ILLUMAR_ITEM.get().builtInRegistryHolder().key());
    }
}
