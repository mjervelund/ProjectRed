package mrtjp.projectred.expansion.client;

import codechicken.lib.colour.EnumColour;
import codechicken.lib.render.BlockRenderer;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mrtjp.projectred.core.client.RecordingBufferSource;
import mrtjp.projectred.expansion.GraphDebugManager;
import mrtjp.projectred.expansion.graphs.ClientSideLinkCache;
import mrtjp.projectred.expansion.part.PneumaticTubePart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Draws the pneumatic link debug overlay of {@link GraphDebugManager} (client only).
 */
public final class GraphDebugRenderer {

    private GraphDebugRenderer() {
    }

    public static void onSubmitCustomGeometry(SubmitCustomGeometryEvent event) {
        // Return if debug rendering is disabled. This can be toggled via command
        if (!GraphDebugManager.isRenderingEnabled()) return;

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        GraphDebugManager renderer = GraphDebugManager.getInstance(level);

        Map<BlockPos, ClientSideLinkCache> links = new HashMap<>();
        for (var container : renderer.getContainers()) {
            // TODO create some kind of interface here
            if (!(container instanceof PneumaticTubePart ptp)) continue;
            if (!ptp.linkCache.isActive()) continue;
            links.put(ptp.pos(), ptp.linkCache);
        }

        if (links.isEmpty()) return;
        // Translate pose to camera position
        Vec3 cam = event.getLevelRenderState().cameraRenderState.pos;
        PoseStack stack = event.getPoseStack();
        stack.pushPose();
        stack.translate(-cam.x, -cam.y, -cam.z);

        RecordingBufferSource buffers = new RecordingBufferSource();
        renderLinks(stack, buffers, links);
        buffers.submit(event.getSubmitNodeCollector());
    }

    private static void renderLinks(PoseStack stack, RecordingBufferSource buffers, Map<BlockPos, ClientSideLinkCache> links) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();

        // Render node cube outlines
        buffers.bind(ccrs, RenderTypes.lines(), stack);
        ccrs.baseColour = EnumColour.ORANGE.rgba();
        for (var entry : links.entrySet()) {
            RenderUtils.bufferCuboidOutline(ccrs.getConsumer(), Cuboid6.full.copy().expand(-2/16D).add(entry.getKey()), 1, 1, 1, 1);
        }

        // Render link lines
        buffers.bind(ccrs, RenderTypes.lines(), stack);
        ccrs.baseColour = EnumColour.LIGHT_BLUE.rgba();
        for (var entry : links.entrySet()) {
            for (var link : entry.getValue().links) {
                BlockPos segStart = entry.getKey();
                LinkedList<Vector3> segmentPoints = link.getPointListFor(segStart);
                if (segmentPoints.isEmpty()) continue;

                Iterator<Vector3> pointsIt = segmentPoints.iterator();
                Vector3 p1 = pointsIt.next();

                while (pointsIt.hasNext()) {
                    Vector3 p2 = pointsIt.next();
                    bufferLinePair(ccrs.getConsumer(),
                            p1.x, p1.y, p1.z,
                            p2.x, p2.y, p2.z,
                            1, 1, 1, 1);
                    p1 = p2;
                }

                // Link start box
                double s = 4;
                RenderUtils.bufferCuboidOutline(ccrs.getConsumer(), new Cuboid6(-s/16, -s/16, -s/16, s/16, s/16, s/16).add(segmentPoints.getFirst()), 0, 0, 1, 1);
                // Link end box
                s = 5;
                RenderUtils.bufferCuboidOutline(ccrs.getConsumer(), new Cuboid6(-s/16, -s/16, -s/16, s/16, s/16, s/16).add(segmentPoints.getLast()), 1, 0, 0, 1);
            }
        }

        // Render cubes at ends of segments
        buffers.bind(ccrs, RenderTypes.debugQuads(), stack);
        ccrs.baseColour = EnumColour.LIGHT_BLUE.rgba(128);
        double b = 0.5/16D;
        Cuboid6 box = new Cuboid6(-b, -b, -b, b, b, b);

        for (var entry : links.entrySet()) {
            for (var link : entry.getValue().links) {
                var points = link.getPointListFor(entry.getKey());
                for (var p : points) {
                    ccrs.setPipeline(new Translation(p));
                    BlockRenderer.renderCuboid(ccrs, box, 0);
                }
            }
        }

        stack.popPose();
    }

    private static void bufferLinePair(VertexConsumer builder, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a) {
        Vector3 v1 = new Vector3(x1, y1, z1).subtract(x2, y2, z2);
        double d = v1.mag();
        v1.divide(d);
        builder.addVertex((float) x1, (float) y1, (float) z1).setColor(r, g, b, a).setNormal((float) v1.x, (float) v1.y, (float) v1.z).setLineWidth(1.5F);
        builder.addVertex((float) x2, (float) y2, (float) z2).setColor(r, g, b, a).setNormal((float) v1.x, (float) v1.y, (float) v1.z).setLineWidth(1.5F);
    }
}
