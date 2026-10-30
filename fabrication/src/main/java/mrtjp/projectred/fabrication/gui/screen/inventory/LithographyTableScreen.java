package mrtjp.projectred.fabrication.gui.screen.inventory;

import net.minecraft.client.renderer.RenderPipelines;
import mrtjp.projectred.fabrication.ProjectRedFabrication;
import mrtjp.projectred.fabrication.inventory.container.LithographyTableMenu;
import mrtjp.projectred.lib.GuiLib;
import mrtjp.projectred.lib.Point;
import mrtjp.projectred.redui.RedUIContainerScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class LithographyTableScreen extends RedUIContainerScreen<LithographyTableMenu> {

    public static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(ProjectRedFabrication.MOD_ID, "textures/gui/lithography_table.png");

    public LithographyTableScreen(LithographyTableMenu container, Inventory playerInventory, Component title) {
        super(176, 171, container, playerInventory, title);

        inventoryLabelX = 8;
        inventoryLabelY = 79;
    }

    @Override
    public void drawBack(GuiGraphicsExtractor graphics, Point mouse, float partialFrame) {
        super.drawBack(graphics, mouse, partialFrame);

        int x = getFrame().x();
        int y = getFrame().y();

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, (float) (0), (float) (0), getFrame().width(), getFrame().height(), 256, 256);

        int s = getMenu().getProgressScaled(24);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x + 80, y + 40, (float) (176), (float) (0), s + 1, 16, 256, 256);

        if (getMenu().canConductorWork())
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x + 16, y + 16, (float) (177), (float) (18), 7, 9, 256, 256);

        GuiLib.drawVerticalTank(graphics, BACKGROUND, x + 16, y + 26, 177, 27, 7, 48, getMenu().getChargeScaled(48));

        if (getMenu().isFlowFull())
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x + 27, y + 16, (float) (185), (float) (18), 7, 9, 256, 256);

        GuiLib.drawVerticalTank(graphics, BACKGROUND, x + 27, y + 26, 185, 27, 7, 48, getMenu().getFlowScaled(48));
    }
}
