package mrtjp.projectred.core.data;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import com.mojang.math.Quadrant;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base for Project Red's model providers on top of vanilla's {@link ModelProvider}. NeoForge's
 * {@code BlockStateProvider}/{@code ItemModelProvider} (and CCL's wrapper) were removed in 1.21.4; this keeps the
 * small vocabulary the module providers used ({@code generated}, {@code handheld}, {@code simpleItemBlock},
 * {@code simpleBlock}, {@code forAllStates}).
 */
public abstract class ProjectRedModelProvider extends ModelProvider {

    protected final String modId;
    protected BlockModelGenerators blockModels;
    protected ItemModelGenerators itemModels;

    protected ProjectRedModelProvider(PackOutput output, String modId) {
        super(output, modId);
        this.modId = modId;
    }

    @Override
    protected final void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        this.blockModels = blockModels;
        this.itemModels = itemModels;
        registerModels();
    }

    protected abstract void registerModels();

    // Modules have a block state and an item model provider for the same mod id: keep provider names unique.
    @Override
    public String getName() {
        return "Model Definitions - " + modId + " (" + getClass().getSimpleName() + ")";
    }

    // Modules share one mod namespace per module but not every block/item gets a JSON model here (parts and
    // gates render through their own renderers), so don't fail on the ones we leave out.
    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return Stream.empty();
    }

    //region Locations
    protected Identifier modLoc(String path) {
        return Identifier.fromNamespaceAndPath(modId, path);
    }

    protected static String name(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }

    protected static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    protected Identifier blockTexture(Block block) {
        Identifier key = BuiltInRegistries.BLOCK.getKey(block);
        return key.withPrefix("block/");
    }
    //endregion

    //region Items
    protected void generated(Supplier<? extends ItemLike> item) {
        generated(item.get());
    }

    protected void generated(ItemLike item) {
        itemModels.generateFlatItem(item.asItem(), ModelTemplates.FLAT_ITEM);
    }

    protected void generated(ItemLike item, Identifier texture) {
        flatItem(item, texture, ModelTemplates.FLAT_ITEM);
    }

    protected void handheld(Supplier<? extends ItemLike> item) {
        handheld(item.get());
    }

    protected void handheld(ItemLike item) {
        itemModels.generateFlatItem(item.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
    }

    protected void handheld(ItemLike item, Identifier texture) {
        flatItem(item, texture, ModelTemplates.FLAT_HANDHELD_ITEM);
    }

    private void flatItem(ItemLike item, Identifier texture, ModelTemplate template) {
        Identifier model = template.create(ModelLocationUtils.getModelLocation(item.asItem()), TextureMapping.layer0(new Material(texture)), itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item.asItem(), ItemModelUtils.plainModel(model));
    }

    /** Item model that just uses the block's model ({@code models/block/<name>}). */
    protected void simpleItemBlock(Block block) {
        itemModels.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(block)));
    }

    /**
     * Item drawn by a {@link mrtjp.projectred.core.client.ProjectRedItemRenderer} (CCL's old {@code clazz(item, Renderer.class)}),
     * with block display transforms.
     */
    protected void specialItem(ItemLike item, SpecialModelRenderer.Unbaked<?> renderer) {
        itemModels.itemModelOutput.accept(item.asItem(), ItemModelUtils.specialModel(Identifier.withDefaultNamespace("block/block"), renderer));
    }

    /** Item that uses an existing model (for example a hand-written one in {@code src/main/resources}). */
    protected void itemWithModel(ItemLike item, Identifier model) {
        itemModels.itemModelOutput.accept(item.asItem(), ItemModelUtils.plainModel(model));
    }
    //endregion

    //region Blocks
    /** Full cube using {@code block/<name>} on all sides. */
    protected void simpleBlock(Block block) {
        simpleBlock(block, cubeAll(block));
    }

    protected void simpleBlock(Block block, Identifier model) {
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(model)));
    }

    protected Identifier cubeAll(Block block) {
        return ModelTemplates.CUBE_ALL.create(block, TextureMapping.cube(new Material(blockTexture(block))), blockModels.modelOutput);
    }

    protected Identifier cubeBottomTop(String name, Identifier side, Identifier bottom, Identifier top) {
        return ModelTemplates.CUBE_BOTTOM_TOP.create(modLoc("block/" + name), new TextureMapping()
                .put(TextureSlot.SIDE, new Material(side))
                .put(TextureSlot.BOTTOM, new Material(bottom))
                .put(TextureSlot.TOP, new Material(top)), blockModels.modelOutput);
    }

    protected Identifier orientableWithBottom(String name, Identifier side, Identifier front, Identifier bottom, Identifier top) {
        return ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.create(modLoc("block/" + name), new TextureMapping()
                .put(TextureSlot.SIDE, new Material(side))
                .put(TextureSlot.FRONT, new Material(front))
                .put(TextureSlot.BOTTOM, new Material(bottom))
                .put(TextureSlot.TOP, new Material(top)), blockModels.modelOutput);
    }

    protected Identifier cube(String name, Identifier down, Identifier up, Identifier north, Identifier south, Identifier east, Identifier west) {
        return ModelTemplates.CUBE.create(modLoc("block/" + name), new TextureMapping()
                .put(TextureSlot.DOWN, new Material(down))
                .put(TextureSlot.UP, new Material(up))
                .put(TextureSlot.NORTH, new Material(north))
                .put(TextureSlot.SOUTH, new Material(south))
                .put(TextureSlot.EAST, new Material(east))
                .put(TextureSlot.WEST, new Material(west))
                .put(TextureSlot.PARTICLE, new Material(north)), blockModels.modelOutput);
    }

    protected static Variant rotateY(Variant variant, int degrees) {
        return degrees % 360 == 0 ? variant : variant.withYRot(Quadrant.parseJson(degrees % 360));
    }

    protected static Variant rotateX(Variant variant, int degrees) {
        return degrees % 360 == 0 ? variant : variant.withXRot(Quadrant.parseJson(degrees % 360));
    }

    /** One variant per block state, like the old {@code getVariantBuilder(block).forAllStates(...)}. */
    protected void forAllStates(Block block, Function<BlockState, Variant> variants) {
        Map<String, BlockStateModel.Unbaked> models = new LinkedHashMap<>();
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            String key = state.getValues()
                    .map(v -> v.property().getName() + "=" + v.valueName())
                    .collect(Collectors.joining(","));
            models.put(key, new MultiVariant(WeightedList.of(variants.apply(state))).toUnbaked());
        }
        blockModels.blockStateOutput.accept(new BlockModelDefinitionGenerator() {
            @Override
            public Block block() {
                return block;
            }

            @Override
            public BlockStateModelDispatcher create() {
                return new BlockStateModelDispatcher(Optional.of(new BlockStateModelDispatcher.SimpleModelSelectors(models)), Optional.empty());
            }
        });
    }
    //endregion
}
