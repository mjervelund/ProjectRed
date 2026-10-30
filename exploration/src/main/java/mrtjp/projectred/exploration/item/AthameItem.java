package mrtjp.projectred.exploration.item;

import net.minecraft.world.item.ToolMaterial;
import mrtjp.projectred.core.init.RegistryIds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AthameItem extends Item {

    public AthameItem(ToolMaterial itemTier, int attackDamage, float attackSpeed) {
        super(RegistryIds.itemProps().sword(itemTier, attackDamage, attackSpeed));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity player) {
        return super.hurtEnemy(stack, target, player);
    }
}
