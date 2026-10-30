package mrtjp.projectred.integration.item;

import mrtjp.projectred.core.init.RegistryIds;
import mrtjp.projectred.integration.GateType;
import net.minecraft.world.item.Item;

public class GatePartItem extends BaseGatePartItem {

    public GatePartItem(GateType gateType) {
        super(RegistryIds.itemProps(), gateType);
    }

    public GateType getGateType() {
        return gateType;
    }

}
