package mrtjp.projectred.fabrication.init;

import mrtjp.projectred.core.inventory.container.ProjectRedMenuType;
import mrtjp.projectred.fabrication.inventory.container.LithographyTableMenu;
import mrtjp.projectred.fabrication.inventory.container.PackagingTableMenu;
import mrtjp.projectred.fabrication.inventory.container.PlottingTableMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

import static mrtjp.projectred.fabrication.ProjectRedFabrication.MENU_TYPES;
import static mrtjp.projectred.fabrication.init.FabricationBlocks.*;

@SuppressWarnings("NotNullFieldNotInitialized")
public class FabricationMenus {

    public static Supplier<MenuType<PlottingTableMenu>> PLOTTING_TABLE_MENU;
    public static Supplier<MenuType<LithographyTableMenu>> LITHOGRAPHY_TABLE_MENU;
    public static Supplier<MenuType<PackagingTableMenu>> PACKAGING_TABLE_MENU;

    public static void register() {

        PLOTTING_TABLE_MENU = MENU_TYPES.register(ID_PLOTTING_TABLE, () -> ProjectRedMenuType.create(PlottingTableMenu.FACTORY));
        LITHOGRAPHY_TABLE_MENU = MENU_TYPES.register(ID_LITHOGRAPHY_TABLE, () -> ProjectRedMenuType.create(LithographyTableMenu.FACTORY));
        PACKAGING_TABLE_MENU = MENU_TYPES.register(ID_PACKAGING_TABLE, () -> ProjectRedMenuType.create(PackagingTableMenu.FACTORY));
    }
}
