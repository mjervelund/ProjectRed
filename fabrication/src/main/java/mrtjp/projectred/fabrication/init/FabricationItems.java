package mrtjp.projectred.fabrication.init;

import mrtjp.projectred.core.init.RegistryIds;
import mrtjp.projectred.fabrication.item.*;

import java.util.function.Supplier;

import static mrtjp.projectred.fabrication.ProjectRedFabrication.ITEMS;

@SuppressWarnings("NotNullFieldNotInitialized")
public class FabricationItems {

    public static final String ID_IC_BLUEPRINT = "ic_blueprint";
    public static final String ID_BLANK_PHOTOMASK = "blank_photomask";
    public static final String ID_PHOTOMASK_SET = "photomask_set";
    public static final String ID_ROUGH_SILICON_WAFER = "rough_silicon_wafer";
//    public static final String ID_ETCHED_SILICON_WAFER = "etched_silicon_wafer";
    public static final String ID_VALID_DIE = "valid_die";
    public static final String ID_INVALID_DIE = "invalid_die";

    public static Supplier<ICBlueprintItem> IC_BLUEPRINT_ITEM;
    public static Supplier<BlankPhotomaskItem> BLANK_PHOTOMASK_ITEM;
    public static Supplier<PhotomaskSetItem> PHOTOMASK_SET_ITEM;
    public static Supplier<RoughSiliconWaferItem> ROUGH_SILICON_WAFER_ITEM;
//    public static Supplier<EtchedSiliconWaferItem> ETCHED_SILICON_WAFER_ITEM;
    public static Supplier<ValidDieItem> VALID_DIE_ITEM;
    public static Supplier<InvalidDieItem> INVALID_DIE_ITEM;

    public static void register() {

        // Items
        IC_BLUEPRINT_ITEM = ITEMS.register(ID_IC_BLUEPRINT, RegistryIds.item(ICBlueprintItem::new));
        BLANK_PHOTOMASK_ITEM = ITEMS.register(ID_BLANK_PHOTOMASK, RegistryIds.item(BlankPhotomaskItem::new));
        PHOTOMASK_SET_ITEM = ITEMS.register(ID_PHOTOMASK_SET, RegistryIds.item(PhotomaskSetItem::new));
        ROUGH_SILICON_WAFER_ITEM = ITEMS.register(ID_ROUGH_SILICON_WAFER, RegistryIds.item(RoughSiliconWaferItem::new));
//        ETCHED_SILICON_WAFER_ITEM = ITEMS.register(ID_ETCHED_SILICON_WAFER, RegistryIds.item(EtchedSiliconWaferItem::new));
        VALID_DIE_ITEM = ITEMS.register(ID_VALID_DIE, RegistryIds.item(ValidDieItem::new));
        INVALID_DIE_ITEM = ITEMS.register(ID_INVALID_DIE, RegistryIds.item(InvalidDieItem::new));
    }
}