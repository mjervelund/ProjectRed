package mrtjp.projectred.core.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.jetbrains.annotations.Nullable;

public class CraftingDamageItem extends Item {

    public CraftingDamageItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable ItemStackTemplate getCraftingRemainder(ItemInstance instance) {
        ItemStack itemStack = switch (instance) {
            case ItemStack s -> s;
            case ItemStackTemplate t -> t.create();
            default -> null;
        };
        if (itemStack == null) return super.getCraftingRemainder(instance);
        if (itemStack.isDamageableItem()) {
            ItemStack ret = itemStack.copy();
            ret.setDamageValue(ret.getDamageValue() + 1);
            return ItemStackTemplate.fromNonEmptyStack(ret);
        }
        return ItemStackTemplate.fromNonEmptyStack(itemStack);
    }
}
