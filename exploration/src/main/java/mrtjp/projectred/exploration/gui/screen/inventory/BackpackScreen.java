package mrtjp.projectred.exploration.gui.screen.inventory;

import net.minecraft.client.renderer.RenderPipelines;
import mrtjp.projectred.exploration.inventory.container.BackpackMenu;
import mrtjp.projectred.lib.Point;
import mrtjp.projectred.redui.RedUIContainerScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static mrtjp.projectred.exploration.ProjectRedExploration.MOD_ID;

public class BackpackScreen extends RedUIContainerScreen<BackpackMenu> {

    public static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/backpack.png");

    public BackpackScreen(BackpackMenu container, Inventory playerInventory, Component title) {
        super(176, 168, container, playerInventory, title);

        inventoryLabelX = 8;
        inventoryLabelY = 75;
    }

    @Override
    public void drawBack(GuiGraphicsExtractor graphics, Point mouse, float partialFrame) {
        super.drawBack(graphics, mouse, partialFrame);

        int x = getFrame().x();
        int y = getFrame().y();

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, (float) (0), (float) (0), getFrame().width(), getFrame().height(), 256, 256);
    }

    @Override
    public boolean onKeyPressed(int glfwKeyCode, int glfwScanCode, int glfwFlags, boolean consumed) {
        return super.onKeyPressed(glfwKeyCode, glfwScanCode, glfwFlags, consumed);
    }
}
