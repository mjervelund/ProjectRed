package mrtjp.projectred.exploration.item;

import mrtjp.projectred.core.init.RegistryIds;
import net.minecraft.world.item.Item;

public class WoolGinItem extends DamageableCraftingContainerItem {

    public WoolGinItem() {
        super(RegistryIds.itemProps()
                .durability(128));
    }
}
