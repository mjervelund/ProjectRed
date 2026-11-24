package mrtjp.projectred.exploration.item;

import mrtjp.projectred.core.init.RegistryIds;
import mrtjp.projectred.core.inventory.container.ProjectRedMenuType;
import mrtjp.projectred.exploration.init.ExplorationDataComponents;
import mrtjp.projectred.exploration.init.ExplorationTags;
import mrtjp.projectred.exploration.inventory.container.BackpackMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class BackpackItem extends Item {

    private final int colour;

    public BackpackItem(int colour) {
        super(RegistryIds.itemProps()
                .stacksTo(1));

        this.colour = colour;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        if (!context.getLevel().isClientSide()) {
            ServerPlayer player = (ServerPlayer) context.getPlayer();
            if (player != null) {
                openGui(player, context.getHand());
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide()) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            openGui(serverPlayer, hand);
        }
        return InteractionResult.SUCCESS;
    }

    private void openGui(ServerPlayer player, InteractionHand hand) {
        // Bind the menu to the slot the backpack was used from (client gets it too, for the slot lock)
        int slot = hand == InteractionHand.OFF_HAND ? Inventory.SLOT_OFFHAND : player.getInventory().getSelectedSlot();
        ProjectRedMenuType.openMenu(player,
                new SimpleMenuProvider((windowId, playerInventory, playerEntity) -> new BackpackMenu(windowId, playerInventory, slot),
                        Component.translatable(this.getDescriptionId())),
                p -> p.writeVarInt(slot));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        stack.addToTooltip(ExplorationDataComponents.BACKPACK_DATA_COMPONENT, context, display, tooltip, flag);
    }

    public DyeColor getDyeColor() {
        return DyeColor.byId(colour);
    }

    public static boolean isBackpack(ItemStack stack) {
        return stack.getItem() instanceof BackpackItem;
    }

    public static boolean isItemAllowedInBackpack(ItemStack stack) {
        return !stack.is(ExplorationTags.BACKPACKS_DISALLOWED_TAG);
    }
}
