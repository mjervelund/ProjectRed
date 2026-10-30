package mrtjp.projectred.expansion.gui.screen.inventory;

import net.minecraft.client.renderer.RenderPipelines;
import mrtjp.projectred.expansion.inventory.container.BatteryBoxMenu;
import mrtjp.projectred.lib.GuiLib;
import mrtjp.projectred.lib.Point;
import mrtjp.projectred.redui.RedUIContainerScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static mrtjp.projectred.expansion.ProjectRedExpansion.MOD_ID;

public class BatteryBoxScreen extends RedUIContainerScreen<BatteryBoxMenu> {

    public static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/battery_box.png");

    public BatteryBoxScreen(BatteryBoxMenu container, Inventory playerInventory, Component title) {
        super(176, 171, container, playerInventory, title);

        inventoryLabelX = 8;
        inventoryLabelY = 79;
    }

    @Override
    public void drawBack(GuiGraphicsExtractor graphics, Point mouse, float partialFrame) {

        int x = getFrame().x();
        int y = getFrame().y();

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, (float) (0), (float) (0), getFrame().width(), getFrame().height(), 256, 256);

        if (getMenu().canConductorWork())
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x + 57, y + 16, (float) (176), (float) (1), 7, 9, 256, 256);
        GuiLib.drawVerticalTank(graphics, BACKGROUND, x + 57, y + 26, 176, 10, 7, 48, getMenu().getChargeScaled(48));

        if (getMenu().isPowerStorageFull())
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x + 112, y + 16, (float) (184), (float) (1), 14, 9, 256, 256);
        GuiLib.drawVerticalTank(graphics, BACKGROUND, x + 112, y + 26, 184, 10, 14, 48, getMenu().getPowerStoredScaled(48));

        if (getMenu().isStorageCharging())
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x + 65, y + 52, (float) (199), (float) (18), 48, 18, 256, 256);
        else if (getMenu().isStorageDischarging())
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x + 65, y + 30, (float) (199), (float) (0), 48, 18, 256, 256);
    }
}
