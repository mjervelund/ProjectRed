package mrtjp.projectred.redui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;

/**
 * Draws 3D models (gate/wire icons and the like) into a GUI rectangle.
 * <p>
 * 26.x GUIs collect 2D draw calls and render them later, so 3D geometry can't be drawn into the GUI pose directly any
 * more; it goes through a picture-in-picture renderer instead (an offscreen target blitted at the icon's rectangle).
 * The model callback gets a pose in icon-local GUI pixels: origin at the icon's top-left, y down, like the GUI pose
 * used to be. Submit geometry through the collector with that pose (e.g. via
 * {@link mrtjp.projectred.core.client.RecordingBufferSource}).
 */
public final class GuiModelIcon {

    @FunctionalInterface
    public interface Model {
        void render(PoseStack iconPose, SubmitNodeCollector collector);
    }

    private GuiModelIcon() {
    }

    /**
     * Queues {@code model} for the {@code width} x {@code height} rectangle at {@code (x, y)} in the current GUI pose.
     */
    public static void submit(GuiGraphicsExtractor graphics, int x, int y, int width, int height, Model model) {
        // The PIP blit isn't transformed by the GUI pose: convert to absolute screen coordinates.
        Vector2f origin = graphics.pose().transformPosition(new Vector2f(x, y));
        int x0 = Math.round(origin.x);
        int y0 = Math.round(origin.y);
        graphics.submitPictureInPictureRenderState(new State(model, x0, y0, x0 + width, y0 + height, graphics.peekScissorStack()));
    }

    public record State(Model model, int x0, int y0, int x1, int y1, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState {

        public State(Model model, int x0, int y0, int x1, int y1, @Nullable ScreenRectangle scissorArea) {
            this(model, x0, y0, x1, y1, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
        }

        @Override
        public float scale() {
            return 1.0F;
        }
    }

    /**
     * Registered with {@code RegisterPictureInPictureRenderersEvent}.
     */
    public static class Renderer extends PictureInPictureRenderer<State> {

        private int guiScale = 1;

        @Override
        public Class<State> getRenderStateClass() {
            return State.class;
        }

        @Override
        public void prepare(State renderState, net.minecraft.client.renderer.state.gui.GuiRenderState guiRenderState, net.minecraft.client.renderer.feature.FeatureRenderDispatcher featureRenderDispatcher, int guiScale) {
            this.guiScale = guiScale;
            super.prepare(renderState, guiRenderState, featureRenderDispatcher, guiScale);
        }

        @Override
        protected void renderToTexture(State state, PoseStack poseStack, SubmitNodeCollector collector) {
            int width = state.x1() - state.x0();
            int height = state.y1() - state.y0();

            // Replace the PIP target's projection (as ViewportRenderNode does) with the GUI's own orthographic one
            // for an icon-sized screen, so the model sees icon-local GUI pixels.
            Projection target = new Projection();
            target.setupOrtho(-1000.0F, 1000.0F, width * guiScale, height * guiScale, true);
            Projection icon = new Projection();
            icon.setupOrtho(-1000.0F, 1000.0F, width, height, true);

            poseStack.last().pose().set(target.getMatrix(new Matrix4f()).invert().mul(icon.getMatrix(new Matrix4f())));
            poseStack.last().normal().identity();

            state.model().render(poseStack, collector);
        }

        @Override
        protected String getTextureLabel() {
            return "projectred_model_icon";
        }
    }
}
