package mrtjp.projectred.expansion.data;

import java.util.List;
import java.util.ArrayList;
import net.minecraft.resources.Identifier;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.ItemModelUtils;
import mrtjp.projectred.expansion.init.ExpansionClientInit;
import mrtjp.projectred.expansion.client.ExpansionItemProperties;
import mrtjp.projectred.core.data.ProjectRedModelProvider;
import mrtjp.projectred.expansion.TubeType;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static mrtjp.projectred.expansion.ProjectRedExpansion.MOD_ID;
import static mrtjp.projectred.expansion.init.ExpansionBlocks.*;
import static mrtjp.projectred.expansion.init.ExpansionItems.*;

public class ExpansionItemModelProvider extends ProjectRedModelProvider {

    public ExpansionItemModelProvider(PackOutput output) {
        super(output, MOD_ID);
    }

    @Override
    protected void registerModels() {

        simpleItemBlock(PROJECT_BENCH_BLOCK.get());
        simpleItemBlock(BATTERY_BOX_BLOCK.get());
        simpleItemBlock(AUTO_CRAFTER_BLOCK.get());
        simpleItemBlock(CHARGING_BENCH_BLOCK.get());
        simpleItemBlock(FIRE_STARTER_BLOCK.get());
        simpleItemBlock(FRAME_ACTUATOR_BLOCK.get());
        simpleItemBlock(TRANSPOSER_BLOCK.get());
        simpleItemBlock(BLOCK_BREAKER_BLOCK.get());
        simpleItemBlock(DEPLOYER_BLOCK.get());

        simpleItemBlock(FRAME_BLOCK.get());
        simpleItemBlock(FRAME_MOTOR_BLOCK.get());

        for (TubeType type : TubeType.values()) {
            specialItem(type.getItem(), ExpansionClientInit.TUBE_ITEM_RENDERER);
        }

        generated(EMPTY_BATTERY_ITEM);
        generated(BATTERY_ITEM);

        handheld(ELECTRIC_SCREWDRIVER_ITEM);

        writablePlanItem(RECIPE_PLAN_ITEM.get());

        chargeableItemBlock(BATTERY_BOX_BLOCK.get(), 8);
    }

    // Block model per charge level, selected by the charge level property
    private void chargeableItemBlock(Block block, int chargeLevels) {
        List<RangeSelectItemModel.Entry> entries = new ArrayList<>();
        for (int i = 1; i <= chargeLevels; i++) {
            entries.add(ItemModelUtils.override(ItemModelUtils.plainModel(modLoc("block/" + name(block) + "_charge" + i)), i));
        }
        itemModels.itemModelOutput.accept(block.asItem(), ItemModelUtils.rangeSelect(new ExpansionItemProperties.ChargeLevel(),
                ItemModelUtils.plainModel(modLoc("block/" + name(block))), entries));
    }

    // Blank and written textures, selected by the written property
    private void writablePlanItem(Item item) {
        Identifier blank = ModelTemplates.FLAT_ITEM.create(modLoc("item/" + name(item)), TextureMapping.layer0(new Material(modLoc("item/" + name(item)))), itemModels.modelOutput);
        Identifier written = ModelTemplates.FLAT_ITEM.create(modLoc("item/" + name(item) + "_written"), TextureMapping.layer0(new Material(modLoc("item/" + name(item) + "_written"))), itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item, ItemModelUtils.conditional(new ExpansionItemProperties.WrittenRecipePlan(),
                ItemModelUtils.plainModel(written), ItemModelUtils.plainModel(blank)));
    }
}
