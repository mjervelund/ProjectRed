package mrtjp.projectred.fabrication.gui;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.render.BlockRenderer;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import mrtjp.projectred.core.client.RecordingBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import org.joml.Matrix4f;


import static mrtjp.projectred.fabrication.ProjectRedFabrication.MOD_ID;

@SuppressWarnings("NotNullFieldNotInitialized")
public class ICRenderTypes {

    public static Identifier PERFBOARD_TEXTURE = Identifier.fromNamespaceAndPath(MOD_ID, "textures/block/workbench_ui/perfboard.png");
    public static Identifier PERFBOARD_EDGE_TEXTURE = Identifier.fromNamespaceAndPath(MOD_ID, "textures/block/workbench_ui/perfboard_edge.png");
    public static Identifier PERFBOARD_CORNER_TEXTURE = Identifier.fromNamespaceAndPath(MOD_ID, "textures/block/workbench_ui/perfboard_corner.png");

    // 26.x builds render types from pipelines; these are the closest vanilla ones to the old composite states
    // (cutout + translucent blend + lightmap; lightning blend for the selection; translucent lines).
    public static RenderType layersRenderType = RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS);

    public static RenderType selectionRenderType = RenderTypes.lightning();

    public static RenderType gridRenderType = RenderTypes.entityTranslucent(PERFBOARD_TEXTURE);

    public static RenderType gridEdgeRenderType = RenderTypes.entityTranslucent(PERFBOARD_EDGE_TEXTURE);

    public static RenderType gridCornerRenderType = RenderTypes.entityTranslucent(PERFBOARD_CORNER_TEXTURE);

    public static RenderType holoGridRenderType = RenderTypes.linesTranslucent();

    /** Line width is a vertex attribute now: bind with {@code RecordingBufferSource#bind(ccrs, type, stack, lineWidth)}. */
    public static RenderType interactionZoneLinesRenderType = RenderTypes.linesTranslucent();

    public static TextureAtlasSprite icSurfaceIcon;
    public static TextureAtlasSprite icSurfaceBorderIcon;
    public static TextureAtlasSprite icSurfaceCornerIcon;
    public static TextureAtlasSprite rotateIcon;
    public static TextureAtlasSprite reflectIcon;

    public static void renderICGrid(PoseStack renderStack, RecordingBufferSource getter, Cuboid6 bounds, CCRenderState ccrs) {

        // Main
        double ymin = -2/16D;
        Cuboid6 box = new Cuboid6(bounds.min.x, ymin, bounds.min.z, bounds.max.x, 0, bounds.max.z);
        getter.bind(ccrs, ICRenderTypes.gridRenderType, renderStack);
        BlockRenderer.renderCuboid(ccrs, box, 1);

        // Edges
        double h = 0.01D;
        getter.bind(ccrs, ICRenderTypes.gridEdgeRenderType, renderStack);
        // Top
        box.set(bounds.min.x,     ymin, bounds.min.z,     bounds.max.x,     h, bounds.min.z + 1);
        BlockRenderer.renderCuboid(ccrs, box, 1);
        // bottom
        box.set(bounds.min.x,     ymin, bounds.max.z - 1, bounds.max.x,     h, bounds.max.z);
        BlockRenderer.renderCuboid(ccrs, box, 1);
        // left
        box.set(bounds.min.x,     ymin, bounds.min.z,     bounds.min.x + 1, h, bounds.max.z);
        BlockRenderer.renderCuboid(ccrs, box, 1);
        // right
        box.set(bounds.max.x - 1, ymin, bounds.min.z,     bounds.max.x,     h, bounds.max.z);
        BlockRenderer.renderCuboid(ccrs, box, 1);

        // Corners
        h = 0.02D;
        getter.bind(ccrs, ICRenderTypes.gridCornerRenderType, renderStack);
        // Top left
        box.set(bounds.min.x, ymin, bounds.min.z, bounds.min.x + 1, h, bounds.min.z + 1);
        BlockRenderer.renderCuboid(ccrs, box, 1);
        // Top right
        box.set(bounds.max.x - 1, ymin, bounds.min.z, bounds.max.x, h, bounds.min.z + 1);
        BlockRenderer.renderCuboid(ccrs, box, 1);
        // bottom right
        box.set(bounds.max.x - 1, ymin, bounds.max.z - 1, bounds.max.x, h, bounds.max.z);
        BlockRenderer.renderCuboid(ccrs, box, 1);
        // bottom left
        box.set(bounds.min.x, ymin, bounds.max.z - 1, bounds.min.x + 1, h, bounds.max.z);
        BlockRenderer.renderCuboid(ccrs, box, 1);

    }

    public static void renderSelection(CCRenderState ccrs, Vector3 a, Vector3 b, double height, double th) {

        Cuboid6 ac = new Cuboid6(a.copy().floor(), a.copy().ceil());
        ac.enclose(b.copy().floor());
        ac.enclose(b.copy().ceil());
        ac.expand(0.002);

        Cuboid6 box = new Cuboid6();

        //Top
        box.min.x = ac.min.x;
        box.min.y = ac.min.y;
        box.min.z = ac.min.z;
        box.max.x = ac.max.x;
        box.max.y = ac.min.y + height;
        box.max.z = ac.min.z + th;
        BlockRenderer.renderCuboid(ccrs, box, 1);

        // Bottom
        box.min.x = ac.min.x;
        box.min.y = ac.min.y;
        box.min.z = ac.max.z - th;
        box.max.x = ac.max.x;
        box.max.y = ac.min.y + height;
        box.max.z = ac.max.z;
        BlockRenderer.renderCuboid(ccrs, box, 1);

        // Left
        box.min.x = ac.min.x;
        box.min.y = ac.min.y;
        box.min.z = ac.min.z + th;
        box.max.x = ac.min.x + th;
        box.max.y = ac.min.y + height;
        box.max.z = ac.max.z - th;
        BlockRenderer.renderCuboid(ccrs, box, 1 | 1<<2 | 1<<3);

        // Right
        box.min.x = ac.max.x - th;
        box.min.y = ac.min.y;
        box.min.z = ac.min.z + th;
        box.max.x = ac.max.x;
        box.max.y = ac.min.y + height;
        box.max.z = ac.max.z - th;
        BlockRenderer.renderCuboid(ccrs, box, 1 | 1<<2 | 1<<3);
    }

    public static void renderGridInBounds(PoseStack stack, RecordingBufferSource getter, Cuboid6 bounds, int amask) {

        VertexConsumer vertexConsumer = getter.getBuffer(holoGridRenderType);
        Matrix4f pose = stack.last().pose();

        float r = 0.7f;
        float g = 0.7f;
        float b = 0.7f;
        float alpha = 0.2f;

        // Render X axis lines
        if ((amask & 1) != 0) {
            for (int y = (int) bounds.min.y; y <= bounds.max.y; y++) {
                for (int z = (int) bounds.min.z; z <= bounds.max.z; z++) {
                    vertexConsumer.addVertex(pose, (float) bounds.min.x, y, z).setColor(r, g, b, alpha).setNormal(1, 0, 0);
                    vertexConsumer.addVertex(pose, (float) bounds.max.x, y, z).setColor(r, g, b, alpha).setNormal(1, 0, 0);
                }
            }
        }

        // Render Y axis lines
        if ((amask & 2) != 0) {
            for (int x = (int) bounds.min.x; x <= bounds.max.x; x++) {
                for (int z = (int) bounds.min.z; z <= bounds.max.z; z++) {
                    vertexConsumer.addVertex(pose, x, (float) bounds.min.y, z).setColor(r, g, b, alpha).setNormal(0, 1, 0);
                    vertexConsumer.addVertex(pose, x, (float) bounds.max.y, z).setColor(r, g, b, alpha).setNormal(0, 1, 0);
                }
            }
        }

        // Render Z axis lines
        if ((amask & 4) != 0) {
            for (int x = (int) bounds.min.x; x <= bounds.max.x; x++) {
                for (int y = (int) bounds.min.y; y <= bounds.max.y; y++) {
                    vertexConsumer.addVertex(pose, x, y, (float) bounds.min.z).setColor(r, g, b, alpha).setNormal(0, 0, 1);
                    vertexConsumer.addVertex(pose, x, y, (float) bounds.max.z).setColor(r, g, b, alpha).setNormal(0, 0, 1);
                }
            }
        }
    }

    public static void renderCenteredTextTopOfCuboid(Component text, Cuboid6 bounds, PoseStack stack, RecordingBufferSource getter) {
        double w = bounds.max.x - bounds.min.x;
        double h = bounds.max.z - bounds.min.z;
        double x = bounds.min.x + w / 2;
        double y = bounds.max.y + 0.002;
        double z = bounds.min.z + h / 2;

        renderConstrainedCenteredText(text, stack, getter, x, y, z, w, h);
    }

    public static void renderConstrainedText(Component text, PoseStack stack, RecordingBufferSource getter, double x, double y, double z, double maxW, double maxH) {
        Font fontRenderer = Minecraft.getInstance().font;
        float h = fontRenderer.lineHeight;
        float w = fontRenderer.width(text);
        float s = (float) Math.min(maxW/w, maxH/h);

        stack.pushPose();
        stack.translate(x, y, z);
        stack.scale(s, 1.0F, s);
        stack.rotate(Axis.XP.rotationDegrees(90.0F));

        // Render text
        getter.text(stack, 0, 0, text.getVisualOrderText(), EnumColour.WHITE.argb(), 0xF000F0);

        stack.popPose();
    }

    public static void renderConstrainedCenteredText(Component text, PoseStack stack, RecordingBufferSource getter, double x, double y, double z, double maxW, double maxH) {
        Font fontRenderer = Minecraft.getInstance().font;
        float h = fontRenderer.lineHeight;
        float w = fontRenderer.width(text);
        float s = (float) Math.min(maxW/w, maxH/h);

        stack.pushPose();
        stack.translate(x, y, z);
        stack.scale(s, 1.0F, s);
        stack.rotate(Axis.XP.rotationDegrees(90.0F));

        // Render text ceneterd at desired position
        getter.text(stack, (float) (-fontRenderer.width(text) / 2), (float) (-fontRenderer.lineHeight / 2), text.getVisualOrderText(), EnumColour.WHITE.argb(), 0xF000F0);

        stack.popPose();
    }

    public void sortComponents(Cuboid6 c) {

        if (c.min.x > c.max.x) {
            double tmp = c.max.x;
            c.max.x = c.min.x;
            c.min.x = tmp;
        }

        if (c.min.y > c.max.y) {
            double tmp = c.max.y;
            c.max.y = c.min.y;
            c.min.y = tmp;
        }

        if (c.min.z > c.max.z) {
            double tmp = c.max.z;
            c.max.z = c.min.z;
            c.min.z = tmp;
        }
    }

    public static void onTextureStitchEvent(TextureAtlasStitchedEvent event) {
        if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) return;

        icSurfaceIcon = event.getAtlas().getSprite(Identifier.fromNamespaceAndPath(MOD_ID, "block/workbench_ui/perfboard"));
        icSurfaceBorderIcon = event.getAtlas().getSprite(Identifier.fromNamespaceAndPath(MOD_ID, "block/workbench_ui/perfboard_edge"));
        icSurfaceCornerIcon = event.getAtlas().getSprite(Identifier.fromNamespaceAndPath(MOD_ID, "block/workbench_ui/perfboard_corner"));
        rotateIcon = event.getAtlas().getSprite(Identifier.fromNamespaceAndPath(MOD_ID, "block/workbench_ui/rotate"));
        reflectIcon = event.getAtlas().getSprite(Identifier.fromNamespaceAndPath(MOD_ID, "block/workbench_ui/reflect"));
    }
}
