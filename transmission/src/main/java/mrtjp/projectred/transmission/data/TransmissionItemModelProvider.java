package mrtjp.projectred.transmission.data;

import mrtjp.projectred.transmission.init.TransmissionClientInit;
import mrtjp.projectred.core.data.ProjectRedModelProvider;
import mrtjp.projectred.transmission.WireType;
import mrtjp.projectred.transmission.client.FramedWirePartItemRenderer;
import mrtjp.projectred.transmission.client.WirePartItemRenderer;
import net.minecraft.data.PackOutput;

import static mrtjp.projectred.transmission.ProjectRedTransmission.MOD_ID;

public class TransmissionItemModelProvider extends ProjectRedModelProvider {

    public TransmissionItemModelProvider(PackOutput output) {
        super(output, MOD_ID);
    }

    @Override
    protected void registerModels() {

        // Wires and framed wires
        for (WireType type : WireType.values()) {
            specialItem(type.getItem(), type.isCenterPart() ? TransmissionClientInit.FRAMED_WIRE_ITEM_RENDERER : TransmissionClientInit.WIRE_ITEM_RENDERER);
        }
    }
}
