package mrtjp.projectred.expansion.item;

import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import mrtjp.projectred.core.init.RegistryIds;
import mrtjp.projectred.expansion.init.ExpansionDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class RecipePlanItem extends Item {

    public RecipePlanItem() {
        super(RegistryIds.itemProps());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        var component = RecipePlanComponent.getComponent(stack);
        if (component != null) component.addToTooltip(context, tooltip, flag, stack);
    }
}
