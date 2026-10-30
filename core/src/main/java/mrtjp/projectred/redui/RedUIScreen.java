package mrtjp.projectred.redui;

import com.mojang.blaze3d.systems.RenderSystem;
import mrtjp.projectred.lib.Point;
import mrtjp.projectred.lib.Rect;
import mrtjp.projectred.lib.Size;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Base Screen class that implements the UI Node system. This is for UIs that do not have containers.
 */
public class RedUIScreen extends Screen implements RedUIRootNode {

    private final List<RedUINode> children = new LinkedList<>();

    // Frame of the GUI window
    private Rect frame;
    // Frame of the full Minecraft window (positioned at 0, 0)
    private Rect screenFrame;
    // Absolute z position of this root node
    private double zPos = 0;

    private long lastClickTime = 0;

    public RedUIScreen(int backgroundWidth, int backgroundHeight, Component title) {
        super(title);

        // These frames are fully set during init() call, when the bounds of the entire screen are known
        this.frame = new Rect(Point.ZERO, new Size(backgroundWidth, backgroundHeight));
        this.screenFrame = Rect.ZERO;

        this.onAddedToParent(); // No actual parent, so this is called once on construction
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
        super.extractRenderState(graphics, mouseX, mouseY, partialFrame);

        // Render semi-transparent grey background
        int x = getScreenFrame().x();
        int y = getScreenFrame().y();
        graphics.fillGradient(x, y, x + getScreenFrame().width(), y + getScreenFrame().height(), -1072689136, -804253680);

        // Call frame update function on all nodes
        Point mousePoint = new Point(mouseX, mouseY);
        operateOnSubtree(mousePoint, (n, p, c) -> {
            n.frameUpdate(p, partialFrame);
            return false;
        }, false);

        // Draw the UI
        drawBackForSubtree(graphics, mousePoint, partialFrame);
        drawFrontForSubtree(graphics, mousePoint, partialFrame);
    }

    @Override
    public void tick() {
        super.tick();
        getSubTree(n -> true).forEach(RedUINode::update);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    //region INestedGuiEventHandler overrides
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double x = event.x(), y = event.y();
        int glfwMouseButton = event.button();
        lastClickTime = System.currentTimeMillis();
        boolean consumed = super.mouseClicked(event, doubleClick);
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
    //endregion

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
