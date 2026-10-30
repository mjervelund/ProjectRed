package mrtjp.projectred.fabrication.item;

import mrtjp.projectred.core.init.RegistryIds;
import mrtjp.projectred.fabrication.init.FabricationDataComponents;
import mrtjp.projectred.integration.GateType;
import mrtjp.projectred.integration.item.BaseGatePartItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class FabricatedGatePartItem extends BaseGatePartItem {

    public FabricatedGatePartItem(GateType gateType) {
        super(RegistryIds.itemProps(), gateType); // No creative tab
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, TooltipDisplay display, Consumer<Component> tooltipList, TooltipFlag flag) {
        stack.addToTooltip(FabricationDataComponents.IC_DATA_COMPONENT_TYPE, tooltipContext, display, tooltipList, flag);
    }
}
