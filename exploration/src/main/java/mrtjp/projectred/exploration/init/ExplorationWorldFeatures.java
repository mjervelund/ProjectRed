package mrtjp.projectred.exploration.init;

import net.minecraft.world.level.levelgen.feature.BlockReplacement;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import mrtjp.projectred.core.Configurator;
import mrtjp.projectred.exploration.world.gen.ConfigFileControlledAddFeaturesBiomeModifier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

import static mrtjp.projectred.exploration.ProjectRedExploration.*;
import static mrtjp.projectred.exploration.init.ExplorationBlocks.*;

@SuppressWarnings("NotNullFieldNotInitialized")
public class ExplorationWorldFeatures {

    public static final String ID_MARBLE_CAVE_CARVER = "marble_cave";

    /* Static registry entries */

    // Biome Modifier Codecs
    public static Supplier<MapCodec<ConfigFileControlledAddFeaturesBiomeModifier>> ADD_FEATURES_BIOME_MODIFIER_CODEC;

    /* Dynamic registry entries */
    // Marble. 26.x carvers only mark what to carve and can't line caves with marble any more, so marble generates as
    // large blobs (like vanilla diorite) under the old marble cave config key.
    public static ResourceKey<Feature> MARBLE_CONFIGURED_FEATURE = createFeatureKey("marble");
    public static ResourceKey<PlacedFeature> MARBLE_PLACED_FEATURE = createPlacedFeatureKey("marble");

    // Configured features
    public static ResourceKey<Feature> RUBY_ORE_CONFIGURED_FEATURE = createFeatureKey("ruby_ore");
    public static ResourceKey<Feature> SAPPHIRE_ORE_CONFIGURED_FEATURE = createFeatureKey("sapphire_ore");
    public static ResourceKey<Feature> PERIDOT_ORE_CONFIGURED_FEATURE = createFeatureKey("peridot_ore");
    public static ResourceKey<Feature> ELECTROTINE_ORE_CONFIGURED_FEATURE = createFeatureKey("electrotine_ore");
    public static ResourceKey<Feature> TIN_ORE_CONFIGURED_FEATURE = createFeatureKey("tin_ore");
    public static ResourceKey<Feature> SILVER_ORE_CONFIGURED_FEATURE = createFeatureKey("silver_ore");

    // Placed features
    public static ResourceKey<PlacedFeature> RUBY_ORE_PLACED_FEATURE = createPlacedFeatureKey("ruby_ore");
    public static ResourceKey<PlacedFeature> SAPPHIRE_ORE_PLACED_FEATURE = createPlacedFeatureKey("sapphire_ore");
    public static ResourceKey<PlacedFeature> PERIDOT_ORE_PLACED_FEATURE = createPlacedFeatureKey("peridot_ore");
    public static ResourceKey<PlacedFeature> ELECTROTINE_ORE_PLACED_FEATURE = createPlacedFeatureKey("electrotine_ore");
    public static ResourceKey<PlacedFeature> TIN_ORE_PLACED_FEATURE = createPlacedFeatureKey("tin_ore");
    public static ResourceKey<PlacedFeature> SILVER_ORE_PLACED_FEATURE = createPlacedFeatureKey("silver_ore");

    public static void register() {


        // Features
        // None yet. For custom ores, we register a configured variant of Vanilla Feature.ORE

        // Codecs
        ADD_FEATURES_BIOME_MODIFIER_CODEC = BIOME_MODIFIER_SERIALIZERS.register("add_features", ConfigFileControlledAddFeaturesBiomeModifier::createCodec);
    }

    public static ResourceKey<Feature> createFeatureKey(String name) {
        return ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(MOD_ID, name));
    }

    public static ResourceKey<PlacedFeature> createPlacedFeatureKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(MOD_ID, name));
    }

    public static ResourceKey<BiomeModifier> createBiomeModifierKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Identifier.fromNamespaceAndPath(MOD_ID, name));
    }

    public static void bootstrapFeatures(BootstrapContext<Feature> context) {
        context.register(MARBLE_CONFIGURED_FEATURE, new OreFeature(new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD), MARBLE_BLOCK.get().defaultBlockState(), 64));

        registerOreConfiguration(context, RUBY_ORE_CONFIGURED_FEATURE,        RUBY_ORE_BLOCK, DEEPSLATE_RUBY_ORE_BLOCK,         8);
        registerOreConfiguration(context, SAPPHIRE_ORE_CONFIGURED_FEATURE,    SAPPHIRE_ORE_BLOCK, DEEPSLATE_SAPPHIRE_ORE_BLOCK, 8);
        registerOreConfiguration(context, PERIDOT_ORE_CONFIGURED_FEATURE,     PERIDOT_ORE_BLOCK, DEEPSLATE_PERIDOT_ORE_BLOCK,   10);
        registerOreConfiguration(context, ELECTROTINE_ORE_CONFIGURED_FEATURE, ELECTROTINE_ORE_BLOCK, DEEPSLATE_ELECTROTINE_ORE_BLOCK, 8);
        registerOreConfiguration(context, TIN_ORE_CONFIGURED_FEATURE,         TIN_ORE_BLOCK, DEEPSLATE_TIN_ORE_BLOCK,           8);
        registerOreConfiguration(context, SILVER_ORE_CONFIGURED_FEATURE,      SILVER_ORE_BLOCK, DEEPSLATE_SILVER_ORE_BLOCK,     9);
    }

    public static void bootstrapPlacements(BootstrapContext<PlacedFeature> context) {

        HolderGetter<Feature> features = context.lookup(Registries.FEATURE);
        registerOrePlacement(context, MARBLE_PLACED_FEATURE, features.getOrThrow(MARBLE_CONFIGURED_FEATURE), -48, 48, 2);

        registerOrePlacement(context, RUBY_ORE_PLACED_FEATURE,        features.getOrThrow(RUBY_ORE_CONFIGURED_FEATURE),        -80, 80, 1);
        registerOrePlacement(context, SAPPHIRE_ORE_PLACED_FEATURE,    features.getOrThrow(SAPPHIRE_ORE_CONFIGURED_FEATURE),    -80, 80, 1);
        registerOrePlacement(context, PERIDOT_ORE_PLACED_FEATURE,     features.getOrThrow(PERIDOT_ORE_CONFIGURED_FEATURE),     -80, 80, 1);
        registerOrePlacement(context, ELECTROTINE_ORE_PLACED_FEATURE, features.getOrThrow(ELECTROTINE_ORE_CONFIGURED_FEATURE), -32, 32, 4);
        registerOrePlacement(context, TIN_ORE_PLACED_FEATURE,         features.getOrThrow(TIN_ORE_CONFIGURED_FEATURE),         -24, 56, 8);
        registerOrePlacement(context, SILVER_ORE_PLACED_FEATURE,      features.getOrThrow(SILVER_ORE_CONFIGURED_FEATURE),      -64, 32, 6);
    }

    public static void bootstrapBiomeModifiers(BootstrapContext<BiomeModifier> context) {

        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        HolderSet<Biome> overworldBiomes = biomes.getOrThrow(BiomeTags.IS_OVERWORLD);

        // Add overworld ores
        context.register(createBiomeModifierKey("add_ruby_ore_to_overworld"), new ConfigFileControlledAddFeaturesBiomeModifier(overworldBiomes, HolderSet.direct(placedFeatures.getOrThrow(RUBY_ORE_PLACED_FEATURE)), GenerationStep.Decoration.UNDERGROUND_ORES, Configurator.Server.RUBY_ORE_KEY));
        context.register(createBiomeModifierKey("add_sapphire_ore_to_overworld"), new ConfigFileControlledAddFeaturesBiomeModifier(overworldBiomes, HolderSet.direct(placedFeatures.getOrThrow(SAPPHIRE_ORE_PLACED_FEATURE)), GenerationStep.Decoration.UNDERGROUND_ORES, Configurator.Server.SAPPHIRE_ORE_KEY));
        context.register(createBiomeModifierKey("add_peridot_ore_to_overworld"), new ConfigFileControlledAddFeaturesBiomeModifier(overworldBiomes, HolderSet.direct(placedFeatures.getOrThrow(PERIDOT_ORE_PLACED_FEATURE)), GenerationStep.Decoration.UNDERGROUND_ORES, Configurator.Server.PERIDOT_ORE_KEY));
        context.register(createBiomeModifierKey("add_electrotine_ore_to_overworld"), new ConfigFileControlledAddFeaturesBiomeModifier(overworldBiomes, HolderSet.direct(placedFeatures.getOrThrow(ELECTROTINE_ORE_PLACED_FEATURE)), GenerationStep.Decoration.UNDERGROUND_ORES, Configurator.Server.ELECTROTINE_ORE_KEY));
        context.register(createBiomeModifierKey("add_tin_ore_to_overworld"), new ConfigFileControlledAddFeaturesBiomeModifier(overworldBiomes, HolderSet.direct(placedFeatures.getOrThrow(TIN_ORE_PLACED_FEATURE)), GenerationStep.Decoration.UNDERGROUND_ORES, Configurator.Server.TIN_ORE_KEY));
        context.register(createBiomeModifierKey("add_silver_ore_to_overworld"), new ConfigFileControlledAddFeaturesBiomeModifier(overworldBiomes, HolderSet.direct(placedFeatures.getOrThrow(SILVER_ORE_PLACED_FEATURE)), GenerationStep.Decoration.UNDERGROUND_ORES, Configurator.Server.SILVER_ORE_KEY));

        // Marble (formerly marble caves)
        context.register(createBiomeModifierKey("add_marble_to_overworld"), new ConfigFileControlledAddFeaturesBiomeModifier(overworldBiomes, HolderSet.direct(placedFeatures.getOrThrow(MARBLE_PLACED_FEATURE)), GenerationStep.Decoration.UNDERGROUND_ORES, Configurator.Server.MARBLE_CAVE_KEY));
    }

    // Registers the actual ore feature. This describes a single cluster of this specific ore type
    private static void registerOreConfiguration(BootstrapContext<Feature> context, ResourceKey<Feature> key, Supplier<Block> standard,  Supplier<Block> deepslate, int veinSize) {
        context.register(key, new OreFeature(ImmutableList.of(
                BlockReplacement.replace(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), standard.get().defaultBlockState()),
                BlockReplacement.replace(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), deepslate.get().defaultBlockState())), veinSize));
    }

    // Registers a placement for the given feature. This controls how many of said features spawn and where
    private static void registerOrePlacement(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<Feature> configuredFeature, int minY, int maxY, int count) {
        List<PlacementModifier> modifiers = ImmutableList.of(
                CountPlacement.of(count),
                InSquarePlacement.spread(),
                HeightRangePlacement.triangle(VerticalAnchor.absolute(minY), VerticalAnchor.absolute(maxY)));

        context.register(key, new PlacedFeature(configuredFeature, modifiers));
    }
}
