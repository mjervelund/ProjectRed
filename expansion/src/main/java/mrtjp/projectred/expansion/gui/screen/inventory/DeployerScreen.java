package mrtjp.projectred.expansion.gui.screen.inventory;

import net.minecraft.client.renderer.RenderPipelines;
import mrtjp.projectred.expansion.ProjectRedExpansion;
import mrtjp.projectred.expansion.inventory.container.DeployerMenu;
import mrtjp.projectred.lib.Point;
import mrtjp.projectred.redui.RedUIContainerScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class DeployerScreen extends RedUIContainerScreen<DeployerMenu> {

    public static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(ProjectRedExpansion.MOD_ID, "textures/gui/deployer.png");

    public DeployerScreen(DeployerMenu menu, Inventory playerInventory, Component title) {
        super(176, 168, menu, playerInventory, title);

        inventoryLabelX = 8;
        inventoryLabelY = 75;

        initSubNodes();
    }

    private void initSubNodes() {
    }

    @Override
    public void drawBack(GuiGraphicsExtractor graphics, Point mouse, float partialFrame) {
        int x = getFrame().x();
        int y = getFrame().y();

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, (float) (0), (float) (0), getFrame().width(), getFrame().height(), 256, 256);
    }
}
