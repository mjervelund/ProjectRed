package mrtjp.projectred.exploration.item;

import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.tags.BlockTags;

public class SickleItem extends Item {

    //TODO fix tag, or create our own sickle-able tag
    public SickleItem(ToolMaterial tier, float attackDamage, float attackSpeed, Properties properties) {
        super(properties.tool(tier, BlockTags.REPLACEABLE, attackDamage, attackSpeed, 0.0F));
    }
}
