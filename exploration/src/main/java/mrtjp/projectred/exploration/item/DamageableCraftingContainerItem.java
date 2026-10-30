package mrtjp.projectred.exploration.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.jetbrains.annotations.Nullable;

public class DamageableCraftingContainerItem extends Item {

    public DamageableCraftingContainerItem(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable ItemStackTemplate getCraftingRemainder(ItemInstance instance) {
        ItemStack stack = switch (instance) {
            case ItemStack s -> s;
            case ItemStackTemplate t -> t.create();
            default -> null;
        };
        if (stack == null) return super.getCraftingRemainder(instance);
        if (stack.isDamageableItem()) {
            if (stack.getDamageValue() + 1 >= stack.getMaxDamage()) {
                return null;
            }
            ItemStack newStack = stack.copy();
            newStack.setDamageValue(stack.getDamageValue() + 1);
            return ItemStackTemplate.fromNonEmptyStack(newStack);
        }
        return ItemStackTemplate.fromNonEmptyStack(stack);
    }
}
