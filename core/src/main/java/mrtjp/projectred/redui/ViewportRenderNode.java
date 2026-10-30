package mrtjp.projectred.redui;

import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Vector3;
import com.mojang.blaze3d.vertex.PoseStack;
import mrtjp.projectred.lib.Point;
import mrtjp.projectred.lib.Rect;
import mrtjp.projectred.lib.Size;
import mrtjp.projectred.lib.Vec2;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.List;

/**
 * Renders a viewport within the node frame. Overrides provided to obtain the camera position fov, etc.
 */
public abstract class ViewportRenderNode extends AbstractGuiNode {

    private final PVMMatrix pvMatrix = new PVMMatrix();

    /**
     * Renders the viewport contents. {@code renderStack} maps world coordinates straight to the viewport's clip
     * space (projection and view are folded in), so submit geometry through {@code collector} with it.
     */
    protected abstract void renderInViewport(PoseStack renderStack, SubmitNodeCollector collector, Vec2 ndcMouse, float partialFrame, boolean isFirstHit);

    protected abstract List<Component> getToolTip(Point mousePosition, boolean isFirstHit);

    protected abstract double getTargetPlaneDistance();

    protected abstract double getVerticalFOV();

    protected abstract double getMaxRenderDist();

    protected abstract Vector3 getCameraPosition();

    protected Rect getGlFrame() {
        // Convert frame to screen space anchored at bottom-left instead of top-left
        Rect screenFrame = getRoot().getScreenFrame();
        Rect frame = convertParentRectToScreen(getFrame());

        Rect bottomLeftFrame = new Rect(
                new Point(frame.x(), screenFrame.height() - frame.y() - frame.height()),
                frame.size);

        // Convert from GUI screen space to GL11 screen space using the Minecraft Gui Scale value
        double glWScale = getRoot().getMinecraft().getWindow().getGuiScale();
        double glHScale = getRoot().getMinecraft().getWindow().getGuiScale();

        return new Rect(new Point((int) Math.round(bottomLeftFrame.x() * glWScale), (int) Math.round(bottomLeftFrame.y() * glHScale)),
                new Size((int) Math.round(bottomLeftFrame.width() * glWScale), (int) Math.round(bottomLeftFrame.height() * glHScale)));
    }

    protected Vector3 mouseToWorld(Point mouse) {
        return ndcMouseToWorld(getFrame().ndc(mouse));
    }

    protected Vector3 ndcMouseToWorld(Vec2 ndcMouse) {
        return pvMatrix.ndcToWorldCoordinates(ndcMouse.dx, ndcMouse.dy, getTargetPlaneDistance());
    }

    protected double distanceToEncloseRect(double width, double height) {
        double fovY = getVerticalFOV();
        double fovX = fovY * getFrame().width() / getFrame().height();

        double xDist = width / (2 * Math.tan(fovX / 2));
        double yDist = height / (2 * Math.tan(fovY / 2));

        return Math.max(xDist, yDist);
    }

    @Override
    public void drawBack(GuiGraphicsExtractor graphics, Point mouse, float partialFrame) {
        // Set up projection and view matrices (also used on the CPU for mouse picking)
        Rect glFrame = getGlFrame();
        pvMatrix.setProjection(getVerticalFOV(), glFrame.width(), glFrame.height(), 0.2F, getMaxRenderDist());
        pvMatrix.setView(getCameraPosition().x, getCameraPosition().y, getCameraPosition().z, 90 * MathHelper.torad, 0);

        // 26.x: GUI geometry is collected and drawn later, so 3D content goes through a picture-in-picture
        // renderer (an offscreen target blitted into the GUI at this node's frame).
        // The PIP blit isn't transformed by the GUI pose: use absolute screen coordinates.
        Rect screenFrame = convertParentRectToScreen(getFrame());
        int x0 = screenFrame.x();
        int y0 = screenFrame.y();
        graphics.submitPictureInPictureRenderState(new ViewportRenderState(this, getFrame().ndc(mouse), partialFrame, isFirstHit(mouse),
                x0, y0, x0 + screenFrame.width(), y0 + screenFrame.height(), graphics.peekScissorStack()));
    }

    private void renderToTexture(ViewportRenderState state, PoseStack poseStack, SubmitNodeCollector collector, int width, int height) {
        // The PIP target uses an orthographic projection; undo it and apply ours instead.
        Projection ortho = new Projection();
        ortho.setupOrtho(-1000.0F, 1000.0F, width, height, true);
        Projection perspective = new Projection();
        perspective.setupPerspective(0.2F, (float) getMaxRenderDist(), (float) Math.toDegrees(getVerticalFOV()), width, height);

        Matrix4f m = ortho.getMatrix(new Matrix4f()).invert()
                .mul(perspective.getMatrix(new Matrix4f()))
                .mul(pvMatrix.getViewMatrix().toMatrix4f());
        poseStack.last().pose().set(m);
        poseStack.last().normal().identity();

        renderInViewport(poseStack, collector, state.ndcMouse(), state.partialFrame(), state.isFirstHit());
    }

    @Override
    public void drawFront(GuiGraphicsExtractor graphics, Point mouse, float partialFrame) {

        List<Component> tooltip = getToolTip(mouse, isFirstHit(mouse));

        renderTooltip(graphics, mouse, tooltip);
    }

    public record ViewportRenderState(ViewportRenderNode node, Vec2 ndcMouse, float partialFrame, boolean isFirstHit,
                                      int x0, int y0, int x1, int y1, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState {

        public ViewportRenderState(ViewportRenderNode node, Vec2 ndcMouse, float partialFrame, boolean isFirstHit, int x0, int y0, int x1, int y1, @Nullable ScreenRectangle scissorArea) {
            this(node, ndcMouse, partialFrame, isFirstHit, x0, y0, x1, y1, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
        }

        @Override
        public float scale() {
            return 1.0F;
        }
    }

    /**
     * Registered with {@code RegisterPictureInPictureRenderersEvent}.
     */
    public static class Renderer extends PictureInPictureRenderer<ViewportRenderState> {

        private int guiScale = 1;

        @Override
        public Class<ViewportRenderState> getRenderStateClass() {
            return ViewportRenderState.class;
        }

        @Override
        public void prepare(ViewportRenderState renderState, net.minecraft.client.renderer.state.gui.GuiRenderState guiRenderState, net.minecraft.client.renderer.feature.FeatureRenderDispatcher featureRenderDispatcher, int guiScale) {
            this.guiScale = guiScale;
            super.prepare(renderState, guiRenderState, featureRenderDispatcher, guiScale);
        }

        @Override
        protected void renderToTexture(ViewportRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
            state.node().renderToTexture(state, poseStack, collector, (state.x1() - state.x0()) * guiScale, (state.y1() - state.y0()) * guiScale);
        }

        @Override
        protected String getTextureLabel() {
            return "projectred_viewport";
        }
    }
}
