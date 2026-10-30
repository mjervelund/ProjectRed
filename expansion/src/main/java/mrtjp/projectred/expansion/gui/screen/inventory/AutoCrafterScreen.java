package mrtjp.projectred.expansion.gui.screen.inventory;

import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.blaze3d.platform.InputConstants;
import codechicken.lib.colour.EnumColour;
import mrtjp.projectred.expansion.inventory.container.AutoCrafterMenu;
import mrtjp.projectred.expansion.item.RecipePlanComponent;
import mrtjp.projectred.expansion.item.RecipePlanItem;
import mrtjp.projectred.lib.GuiLib;
import mrtjp.projectred.lib.Point;
import mrtjp.projectred.redui.AbstractButtonNode;
import mrtjp.projectred.redui.RedUIContainerScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import static mrtjp.projectred.expansion.ProjectRedExpansion.MOD_ID;

public class AutoCrafterScreen extends RedUIContainerScreen<AutoCrafterMenu> {

    public static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/auto_crafter.png");

    private boolean isShiftDown = false;

    public AutoCrafterScreen(AutoCrafterMenu container, Inventory playerInventory, Component title) {
        super(176, 212, container, playerInventory, title);

        inventoryLabelX = 8;
        inventoryLabelY = 120;

        initSubNodes();
    }

    private void initSubNodes() {
        AbstractButtonNode cyclePlanButton = new AbstractButtonNode() {
            @Override
            protected void onButtonClicked() {
                getMenu().getAutoCrafterTile().sendCyclePlan();
            }

            @Override
            protected void drawButtonBody(GuiGraphicsExtractor graphics, boolean mouseover) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, getPosition().x, getPosition().y, (float) (176), (float) (0), 14, 14, 256, 256);
            }
        };
        cyclePlanButton.setPosition(126, 23);
        cyclePlanButton.setSize(14, 14);
        addChild(cyclePlanButton);
    }

    @Override
    public void drawBack(GuiGraphicsExtractor graphics, Point mouse, float partialFrame) {
        super.drawBack(graphics, mouse, partialFrame);

        int x = getFrame().x();
        int y = getFrame().y();

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, (float) (0), (float) (0), getFrame().width(), getFrame().height(), 256, 256);

        if (getMenu().canConductorWork())
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x + 16, y + 16, (float) (177), (float) (18), 7, 9, 256, 256);

        GuiLib.drawVerticalTank(graphics, BACKGROUND, x + 16, y + 26, 177, 27, 7, 48, getMenu().getChargeScaled(48));

        if (getMenu().isFlowFull())
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x + 27, y + 16, (float) (185), (float) (18), 7, 9, 256, 256);

        GuiLib.drawVerticalTank(graphics, BACKGROUND, x + 27, y + 26, 185, 27, 7, 48, getMenu().getFlowScaled(48));

        // Draw progress bar
        int s = getMenu().getProgressScaled(22);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x + 100, y + 41, (float) (193), (float) (23), s + 1, 14, 256, 256);

        // Draw plan output stack
        ItemStack output = getMenu().getPlanOutput();
        if (!output.isEmpty()) {
            graphics.item(output, x + 125, y + 40);
        }
    }

    @Override
    public void drawFront(GuiGraphicsExtractor graphics, Point mouse, float partialFrame) {
        // Plan output overlays
        if (isShiftDown) {
            drawPlanOutputsOverlay(graphics, getFrame().x(), getFrame().y());
        }

        // Selected plan slot
        drawPlanSlotSelection(graphics, getFrame().x() + 44, getFrame().y() + 22);
    }

    private void drawPlanSlotSelection(GuiGraphicsExtractor graphics, int xPos, int yPos) {
        int s = getMenu().getPlanSlot();
        int ix = s % 3;
        int iy = s / 3;
        int x = xPos + 18 * ix - 3;
        int y = yPos + 18 * iy - 3;
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, (float) (193), (float) (0), 22, 22, 256, 256);
    }

    private void drawPlanOutputsOverlay(GuiGraphicsExtractor graphics, int xPos, int yPos) {

        for (Slot slot : getMenu().slots) {
            var recipeData = RecipePlanComponent.getComponent(slot.getItem());
            if (recipeData != null && recipeData.isRecipeValid()) {

                ItemStack output = recipeData.getOutput();
                int colour = EnumColour.LIGHT_BLUE.argb(0xCC);
                graphics.fillGradient(xPos + slot.x, yPos + slot.y, xPos + slot.x + 16, yPos + slot.y + 16, colour, colour);

                // Render item at Z +200
                graphics.item(output, xPos + slot.x, yPos + slot.y, 0, 200);
            }
        }
    }

    @Override
    public boolean onKeyPressed(int glfwKeyCode, int glfwScanCode, int glfwFlags, boolean consumed) {
        if (!consumed && glfwKeyCode == InputConstants.KEY_LSHIFT) {
            isShiftDown = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyReleased(int glfwKeyCode, int glfwScanCode, int glfwFlags, boolean consumed) {
        if (glfwKeyCode == InputConstants.KEY_LSHIFT) {
            isShiftDown = false;
            return true;
        }
        return false;
    }
}
