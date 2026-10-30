package mrtjp.projectred.redui;

import com.mojang.blaze3d.systems.RenderSystem;
import mrtjp.projectred.lib.Point;
import mrtjp.projectred.lib.Rect;
import mrtjp.projectred.lib.Size;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.LinkedList;
import java.util.List;

public class RedUIContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements RedUIRootNode {

    private final List<RedUINode> children = new LinkedList<>();

    // Frame of the GUI window
    private Rect frame;
    // Frame of the full Minecraft window (positioned at 0, 0)
    private Rect screenFrame;
    // Absolute z position of this root node
    private double zPos = 0;

    private long lastClickTime = 0;

    public RedUIContainerScreen(int backgroundWidth, int backgroundHeight, T container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.imageWidth = backgroundWidth;
        this.imageHeight = backgroundHeight;

        // These frames are fully set during init() call, when the bounds of the entire screen are known
        this.frame = new Rect(Point.ZERO, new Size(backgroundWidth, backgroundHeight));
        this.screenFrame = Rect.ZERO;
    }

    @Override
    protected void init() {
        super.init();

        screenFrame = new Rect(Point.ZERO, new Size(width, height));

        Point framePos = new Point((screenFrame.width() - frame.width()) / 2, (screenFrame.height() - frame.height()) / 2);
        frame = new Rect(framePos, frame.size);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialFrame) {
        // Call frame update function on all nodes
        Point mousePoint = new Point(mouseX, mouseY);
        operateOnSubtree(mousePoint, (n, p, c) -> {
            n.frameUpdate(p, partialFrame);
            return false;
        }, false);

        // Super draws the dimmed background and calls extractBackground() (RedUI back layer), then slots, labels,
        // the carried item and tooltips (tooltips are deferred to the end of the frame).
        super.extractRenderState(graphics, mouseX, mouseY, partialFrame);

        // Render foreground
        drawFrontForSubtree(graphics, mousePoint, partialFrame);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialFrame) {
        super.extractBackground(graphics, mouseX, mouseY, partialFrame);
        // Draw back of all nodes in RedUI tree (this class is expected to provide the main background via RedUI's render call)
        drawBackForSubtree(graphics, new Point(mouseX, mouseY), partialFrame);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        getSubTree(n -> true).forEach(RedUINode::update);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double x = event.x(), y = event.y();
        int glfwMouseButton = event.button();
        lastClickTime = System.currentTimeMillis();
//        boolean consumed = super.mouseClicked(event, doubleClick); //TODO Check consumption somehow. This always returns true
        super.mouseClicked(x, y, glfwMouseButton);
        boolean consumed = false;
        return operateOnZOrderedSubtree(new Point((int) x, (int) y), (n, p, c) -> n.mouseClicked(p, glfwMouseButton, c), consumed);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        double x = event.x(), y = event.y();
        int button = event.button();
        boolean consumed = super.mouseReleased(event);
        long timeSinceLastClick = System.currentTimeMillis() - lastClickTime;
        return operateOnZOrderedSubtree(new Point((int) x, (int) y), (n, p, c) -> n.mouseReleased(p, button, timeSinceLastClick, c), consumed);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        double x = event.x(), y = event.y();
        int button = event.button();
        boolean consumed = super.mouseDragged(event, dragX, dragY);
        long timeSinceLastClick = System.currentTimeMillis() - lastClickTime;
        return operateOnZOrderedSubtree(new Point((int) x, (int) y), (n, p, c) -> n.mouseDragged(p, button, timeSinceLastClick, c), consumed);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        boolean consumed = super.mouseScrolled(x, y, scrollX, scrollY);
        return operateOnZOrderedSubtree(new Point((int) x, (int) y), (n, p, c) -> n.mouseScrolled(p, scrollX, scrollY, c), consumed);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        char keyPressed = (char) event.codepoint();
        int glfwFlags = 0;
        boolean consumed = super.charTyped(event);
        return operateOnZOrderedSubtree(Point.ZERO, (n, p, c) -> n.onCharTyped(keyPressed, glfwFlags, c), consumed);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int glfwKeyCode = event.key(), glfwScanCode = event.keycode(), glfwFlags = event.modifiers();
        boolean consumed = super.keyPressed(event);
        return operateOnZOrderedSubtree(Point.ZERO, (n, p, c) -> n.onKeyPressed(glfwKeyCode, glfwScanCode, glfwFlags, c), consumed);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        int glfwKeyCode = event.key(), glfwScanCode = event.keycode(), glfwFlags = event.modifiers();
        boolean consumed = super.keyReleased(event);
        return operateOnZOrderedSubtree(Point.ZERO, (n, p, c) -> n.onKeyReleased(glfwKeyCode, glfwScanCode, glfwFlags, c), consumed);
    }

    //region RedUINode overrides

    @Override
    public Rect getScreenFrame() {
        return screenFrame;
    }

    @Override
    public List<RedUINode> getOurChildren() {
        return children;
    }

    @Override
    public Rect getFrame() {
        return frame;
    }

    @Override
    public Point getPosition() {
        return frame.origin;
    }

    @Override
    public double getZPosition() {
        return zPos;
    }

    @Override
    public void setZPosition(double zPosition) {
        this.zPos = zPosition;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public Font getFontRenderer() {
        return font;
    }

    //endregion
}
