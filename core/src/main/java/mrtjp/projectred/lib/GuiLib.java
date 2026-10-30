package mrtjp.projectred.lib;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;


import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

public class GuiLib {

    public static void drawVerticalTank(GuiGraphicsExtractor graphics, Identifier loc, int x, int y, int u, int v, int w, int h, int prog) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, loc, x, y + h - prog, (float) (u), (float) (v + h - prog), w, prog, 256, 256);
    }

    public static void drawLine(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int th, int argb) {

        // Two points to rect
        Vec2 p1 = new Vec2(x1, y1);
        Vec2 p2 = new Vec2(x2, y2);
        Vec2 v = p2.subtract(p1);
        Vec2 p = new Vec2(v.dy, -v.dx);
        Vec2 pnorm = p.normalize();
        Vec2 widthVec = pnorm.multiply(th / 2.0);

        Vec2 r1 = p1.subtract(widthVec);
        Vec2 r2 = p2.subtract(widthVec);
        Vec2 r3 = p2.add(widthVec);
        Vec2 r4 = p1.add(widthVec);

        float[] xy = {
                (float) r1.dx, (float) r1.dy,
                (float) r2.dx, (float) r2.dy,
                (float) r3.dx, (float) r3.dy,
                (float) r4.dx, (float) r4.dy
        };
        graphics.submitGuiElementRenderState(new QuadRenderState(new Matrix3x2f(graphics.pose()), xy, argb, graphics.peekScissorStack()));
    }

    /**
     * An arbitrary coloured quad in GUI space (vertices in order).
     */
    private record QuadRenderState(Matrix3x2fc pose, float[] xy, int argb, @Nullable ScreenRectangle scissorArea) implements GuiElementRenderState {

        @Override
        public void buildVertices(VertexConsumer consumer) {
            for (int i = 0; i < 4; i++) {
                consumer.addVertexWith2DPose(pose, xy[i * 2], xy[i * 2 + 1]).setColor(argb);
            }
        }

        @Override
        public RenderPipeline pipeline() {
            return RenderPipelines.GUI;
        }

        @Override
        public TextureSetup textureSetup() {
            return TextureSetup.noTexture();
        }

        @Override
        public @Nullable ScreenRectangle bounds() {
            float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
            for (int i = 0; i < 4; i++) {
                minX = Math.min(minX, xy[i * 2]);
                maxX = Math.max(maxX, xy[i * 2]);
                minY = Math.min(minY, xy[i * 2 + 1]);
                maxY = Math.max(maxY, xy[i * 2 + 1]);
            }
            ScreenRectangle bounds = new ScreenRectangle((int) Math.floor(minX), (int) Math.floor(minY),
                    (int) Math.ceil(maxX - minX), (int) Math.ceil(maxY - minY)).transformMaxBounds(pose);
            return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
        }
    }
}
