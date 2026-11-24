package mrtjp.projectred.exploration.inventory.container;

import mrtjp.projectred.core.inventory.container.ProjectRedMenuType;
import mrtjp.projectred.exploration.init.ExplorationMenus;
import mrtjp.projectred.exploration.inventory.BackpackInventory;
import mrtjp.projectred.exploration.item.BackpackItem;
import mrtjp.projectred.exploration.item.component.BackpackDataComponent;
import mrtjp.projectred.lib.InventoryLib;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;


public class BackpackMenu extends AbstractContainerMenu {

    public static final ProjectRedMenuType<BackpackMenu> FACTORY = (windowId, playerInv, packet) ->
            new BackpackMenu(windowId, playerInv, packet != null ? packet.readVarInt() : playerInv.getSelectedSlot());

    private final BackpackInventory inventory;// = new BackpackInventory(27);

    private final Inventory playerInventory;

    // The open backpack: the player inventory slot it was opened from and that exact stack. The menu stays bound to
    // them, not to "whatever is in the main hand", so changing the selected hotbar slot while it is open (scroll wheel,
    // number keys, lag between the right-click and the GUI) can no longer save into another item (MrTJP/ProjectRed#1925).
    private final int backpackSlot;
    private final ItemStack backpack;

    public BackpackMenu(int windowId, Inventory playerInventory) {
        this(windowId, playerInventory, playerInventory.getSelectedSlot());
    }

    public BackpackMenu(int windowId, Inventory playerInventory, int backpackSlot) {
        super(ExplorationMenus.BACKPACK_MENU.get(), windowId);

        this.playerInventory = playerInventory;
        this.backpackSlot = backpackSlot;
        this.backpack = backpackSlot >= 0 && backpackSlot < playerInventory.getContainerSize() ? playerInventory.getItem(backpackSlot) : ItemStack.EMPTY;
        this.inventory = this.loadInventory();

        inventory.addListener(this::onBackpackInventoryChanged);

        InventoryLib.addPlayerInventory(playerInventory, 8, 86, (inv, index, x, y) -> new BackpackPlayerSlot(inv, index, x, y, backpackSlot), this::addSlot);
        InventoryLib.addInventory(inventory, 0, 8, 18, 9, 3, BackpackSlot::new, this::addSlot);
    }

    private boolean hasBackpack() {
        return !backpack.isEmpty() && BackpackItem.isBackpack(backpack);
    }

    private BackpackInventory loadInventory() {
        if (!hasBackpack()) {
            return new BackpackInventory(27);
        }

        // Get or create the backpack data component
        var component = BackpackDataComponent.getOrCreateComponent(backpack, 27, true);

        // Save it in opened state. Previously, we also emptied it here to prevent duplication.
        BackpackDataComponent.setComponent(backpack, component);

        return component.createInventory();
    }

    private void saveInventory(boolean isOpened) {
        // Saved into the bound stack wherever it is now (even if it has left its slot and the menu is closing for it)
        if (!hasBackpack()) {
            return;
        }

        // Convert inventory to a Backpack component
        var component = BackpackDataComponent.fromInventory(inventory, isOpened);

        // Save it
        BackpackDataComponent.setComponent(backpack, component);
    }

    @Override
    public void clicked(int slotId, int dragType, ContainerInput clickType, Player player) {
        // Required because vanilla's shyte handling of number hotbar quick swap.
        // Apparently it only asks the non-hotbar slot if it can accept the item, but it does not ask the
        // hotbar slot (corresponding to number pressed) if it can take the stack (which would be denied by the custom slot class's canTakeStack implemented above).
        // Additionally, if the non-hotbar slot is empty, it calls that empty slot's canTakeStack. This is almost certainly
        // a bug. canTakeStack should be called on BOTH slots, and isItemValid should be called on BOTH slots with the other slots contents.
        //
        // Update 1.18.2: Still, only the non-hotbar slot is checked for mayPickup/mayPlace results. Empty non-hotbar slot is no longer asked for
        //                canTakeStack (instead its correctly asked mayPlace) but the hotbar slot is still not asked for canTakeStack.

        if (isHotbar(slotId)) {
            int hotbarIndex = slotId - 27;
            if (hotbarIndex == backpackSlot) {
                return;
            }
        }

        if (clickType == ContainerInput.SWAP) {
            // dragType is the hotbar slot (0-8) or the offhand (40) to swap with
            if (dragType == backpackSlot) {
                return;
            }
        }

        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        saveInventory(false);
    }

    @Override
    public boolean stillValid(Player player) {
        // Only while the backpack it was opened from is still in its slot
        return hasBackpack() && playerInventory.getItem(backpackSlot) == backpack;
    }

    protected void onBackpackInventoryChanged(Container backpackInventory) {
        // Keep the backpack's own data current (server only), so a copy of the stack made while it is open (dropped,
        // moved by another mod) carries what the menu holds instead of the contents at open time.
        if (!playerInventory.player.level().isClientSide()) {
            saveInventory(true);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = slots.get(slotIndex);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack originalStack = stack.copy();

        if (isBag(slotIndex)) {
            if (!moveToEntireInventory(stack, true)) return ItemStack.EMPTY;
        } else {
            if (!moveToBag(stack, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == originalStack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);
        return originalStack;
    }

    //@formatter:off
    private boolean isPlayerInventory(int slotIndex) {
        return slotIndex >= 0 && slotIndex < 27;
    }
    private boolean isHotbar(int slotIndex) {
        return slotIndex >= 27 && slotIndex < 36;
    }
    private boolean isBag(int slotIndex) {
        return slotIndex >= 36 && slotIndex < 63;
    }

    private boolean moveToPlayerInventory(ItemStack stack, boolean reverse) {
        return moveItemStackTo(stack, 0, 27, reverse);
    }
    private boolean moveToHotbar(ItemStack stack, boolean reverse) {
        return moveItemStackTo(stack, 27, 36, reverse);
    }
    private boolean moveToEntireInventory(ItemStack stack, boolean reverse) {
        return moveItemStackTo(stack, 0, 36, reverse);
    }
    private boolean moveToBag(ItemStack stack, boolean reverse) {
        return moveItemStackTo(stack, 36, 63, reverse);
    }
    //@formatter:on

    /**
     * Slot for the actual player inventory. Disables picking up backpack from hotbar.
     */
    private static class BackpackPlayerSlot extends Slot {
        private final int backpackSlot;

        public BackpackPlayerSlot(Container inventory, int slotIndex, int x, int y, int backpackSlot) {
            super(inventory, slotIndex, x, y);
            this.backpackSlot = backpackSlot;
        }
        @Override
        public boolean mayPickup(Player player) {
            // Dont allow pickup if this is the slot containing the open backpack
            return getSlotIndex() != backpackSlot;
        }
    }

    /**
     * Slot for the backpack contents inventory. Enforces the backpack blacklist.
     */
    private static class BackpackSlot extends Slot {
        public BackpackSlot(Container inventory, int slotIndex, int x, int y) {
            super(inventory, slotIndex, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return BackpackItem.isItemAllowedInBackpack(stack);
        }
    }
}
