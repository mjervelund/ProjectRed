package mrtjp.projectred.core.item;

import mrtjp.projectred.core.init.RegistryIds;
import net.minecraft.world.item.Item;

public class DrawPlateItem extends CraftingDamageItem {

    public DrawPlateItem() {
        super(RegistryIds.itemProps().durability(512));
    }
}
