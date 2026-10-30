package mrtjp.projectred.expansion.gui.screen.inventory;

import net.minecraft.client.renderer.RenderPipelines;
import mrtjp.projectred.expansion.inventory.container.ChargingBenchMenu;
import mrtjp.projectred.lib.GuiLib;
import mrtjp.projectred.lib.Point;
import mrtjp.projectred.redui.RedUIContainerScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static mrtjp.projectred.expansion.ProjectRedExpansion.MOD_ID;

public class ChargingBenchScreen extends RedUIContainerScreen<ChargingBenchMenu> {

    public static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/charging_bench.png");

    public ChargingBenchScreen(ChargingBenchMenu container, Inventory playerInventory, Component title) {
        super(176, 183, container, playerInventory, title);

        inventoryLabelX = 8;
        inventoryLabelY = 91;
    }

    @Override
    public void drawBack(GuiGraphicsExtractor graphics, Point mouse, float partialFrame) {
        int x = getFrame().x();
        int y = getFrame().y();

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, (float) (0), (float) (0), getFrame().width(), getFrame().height(), 256, 256);

        if (getMenu().canConductorWork())
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x + 14, y + 17, (float) (176), (float) (1), 7, 9, 256, 256);
        GuiLib.drawVerticalTank(graphics, BACKGROUND, x + 14, y + 27, 176, 10, 7, 48, getMenu().getChargeScaled(48));

        if (getMenu().isPowerStorageFull())
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x + 41, y + 17, (float) (184), (float) (1), 14, 9, 256, 256);
        GuiLib.drawVerticalTank(graphics, BACKGROUND, x + 41, y + 27, 184, 10, 14, 48, getMenu().getPowerStoredScaled(48));

        if (getMenu().isStorageCharging())
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x + 26, y + 48, (float) (199), (float) (0), 10, 8, 256, 256);

        if (getMenu().areItemsCharging())
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x + 63, y + 29, (float) (210), (float) (0), 17, 10, 256, 256);
    }
}
