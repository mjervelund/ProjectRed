package mrtjp.projectred.expansion.gui.screen.inventory;

import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.blaze3d.platform.InputConstants;
import codechicken.lib.colour.EnumColour;
import com.mojang.blaze3d.systems.RenderSystem;
import mrtjp.projectred.expansion.ProjectRedExpansion;
import mrtjp.projectred.expansion.inventory.container.ProjectBenchMenu;
import mrtjp.projectred.expansion.item.RecipePlanComponent;
import mrtjp.projectred.expansion.item.RecipePlanItem;
import mrtjp.projectred.expansion.tile.ProjectBenchBlockEntity;
import mrtjp.projectred.lib.Point;
import mrtjp.projectred.redui.AbstractButtonNode;
import mrtjp.projectred.redui.RedUIContainerScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ProjectBenchScreen extends RedUIContainerScreen<ProjectBenchMenu> {

    public static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(ProjectRedExpansion.MOD_ID, "textures/gui/project_bench.png");

    private boolean isShiftDown = false;

    public ProjectBenchScreen(ProjectBenchMenu container, Inventory playerInventory, Component title) {
        super(176, 208, container, playerInventory, title);

        inventoryLabelX = 8;
        inventoryLabelY = 116;

        initSubNodes();
    }

    private void initSubNodes() {

        AbstractButtonNode writePlanButton = new AbstractButtonNode() {
            @Override
            protected void onButtonClicked() {
                getMenu().getProjectBenchTile().sendWriteButtonPressed();
            }

            @Override
            protected void drawButtonBody(GuiGraphicsExtractor graphics, boolean mouseover) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, getPosition().x, getPosition().y, (float) (176), (float) (0), 14, 14, 256, 256);
            }
        };
        writePlanButton.setPosition(18, 56);
        writePlanButton.setSize(14, 14);
        addChild(writePlanButton);

        AbstractButtonNode clearGridButton = new AbstractButtonNode() {
            @Override
            protected void onButtonClicked() {
                getMenu().getProjectBenchTile().sendGridClearButtonPressed();
            }

            @Override
            protected void drawButtonBody(GuiGraphicsExtractor graphics, boolean mouseover) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, getPosition().x, getPosition().y, (float) (176), (float) (15), 8, 8, 256, 256);
            }
        };
        clearGridButton.setPosition(37, 17);
        clearGridButton.setSize(8, 8);
        addChild(clearGridButton);
    }

    @Override
    public void drawBack(GuiGraphicsExtractor graphics, Point mouse, float partialFrame) {
        int x = getFrame().x();
        int y = getFrame().y();

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, (float) (0), (float) (0), getFrame().width(), getFrame().height(), 256, 256);

        ProjectBenchBlockEntity tile = getMenu().getProjectBenchTile();
        if (tile.isPlanRecipe()) {
            int missingMask = tile.getCraftingHelper().getMissingIngredientMask();
            Container inputs = tile.getCraftingHelper().getCraftingInventory();
            drawPlanIngredientsOverlay(graphics, inputs, missingMask, x + 48, y + 18);
        }
    }

    @Override
    public void drawFront(GuiGraphicsExtractor graphics, Point mouse, float partialFrame) {

        if (isShiftDown) {
            drawPlanOutputsOverlay(graphics, getFrame().x(), getFrame().y());
        }
    }

    private void drawPlanIngredientsOverlay(GuiGraphicsExtractor graphics, Container ingredients, int missingMask, int xPos, int yPos) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int drawPosX = xPos + (x * 18);
                int drawPosY = yPos + (y * 18);

                int index = (y * 3) + x;
                ItemStack ingredient = ingredients.getItem(index);
                int colour = (missingMask & 1 << index) != 0 ? EnumColour.RED.argb(0x77) : EnumColour.GRAY.argb(0x77);

                if (!ingredient.isEmpty()) {
                    graphics.fillGradient(drawPosX, drawPosY, drawPosX + 16, drawPosY + 16, colour, colour);
                    graphics.item(ingredient, drawPosX, drawPosY);
                }
            }
        }
    }

    private void drawPlanOutputsOverlay(GuiGraphicsExtractor graphics, int xPos, int yPos) {

        for (Slot slot : getMenu().slots) {
            ItemStack stack = slot.getItem();
            var recipeData = RecipePlanComponent.getComponent(stack);
            if (recipeData != null && recipeData.isRecipeValid()) {

                ItemStack output = recipeData.getOutput();
                int colour = EnumColour.LIGHT_BLUE.argb(0xCC);
                graphics.fillGradient(xPos + slot.x, yPos + slot.y, xPos + slot.x + 16, yPos + slot.y + 16, colour, colour);

                // Render item at Z +200
                graphics.item(output, xPos + slot.x, yPos + slot.y);
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
