package mrtjp.projectred.transmission.client;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import java.util.ArrayList;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.microblock.api.MicroMaterial;
import codechicken.microblock.api.MicroMaterialClient;
import codechicken.microblock.util.MaskedCuboid;
import net.minecraft.client.renderer.rendertype.RenderType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FramedJacketedWireModel {

    private final CCModel wireModel;
    private final MaskedCuboid[] wireBoxes;
    private final MaskedCuboid[] highlightBoxes;

    private final List<MaskedCuboid> wireBoxList;
    private final List<MaskedCuboid> highlightBoxList;

    public FramedJacketedWireModel(CCModel wireModel, MaskedCuboid[] wireBoxes, MaskedCuboid[] highlightBoxes) {
        this.wireModel = wireModel;
        this.wireBoxes = wireBoxes;
        this.highlightBoxes = highlightBoxes;

        wireBoxList = Arrays.asList(wireBoxes);
        highlightBoxList = Arrays.asList(highlightBoxes);
    }

    public void renderWire(CCRenderState ccrs, IVertexOperation... ops) {
        wireModel.render(ccrs, ops);
    }

    public void renderMaterial(CCRenderState ccrs, MicroMaterial material, boolean inventory) {
        renderCuboids(ccrs, material, wireBoxList);
    }

    public void renderHighlight(CCRenderState ccrs, MicroMaterial material, boolean inventory) {
        renderCuboids(ccrs, material, highlightBoxList);
    }

    // CB Multipart 3.6 materials only provide baked model parts; write their quads to the bound consumer.
    // Tints of the material (grass, leaves) are not applied here.
    private static void renderCuboids(CCRenderState ccrs, MicroMaterial material, List<MaskedCuboid> boxes) {
        MicroMaterialClient client = Objects.requireNonNull(MicroMaterialClient.get(material));
        VertexConsumer consumer = ccrs.r;
        if (consumer == null) return;

        List<BlockStateModelPart> parts = new ArrayList<>();
        client.collectParts(null, null, ImmutableSet.copyOf(boxes), parts);

        QuadInstance instance = new QuadInstance();
        instance.setLightCoords(ccrs.brightness);
        instance.setOverlayCoords(ccrs.overlay);
        if (ccrs.alphaOverride >= 0) {
            instance.setColor(ARGB.color(ccrs.alphaOverride, 0xFFFFFF));
        }
        PoseStack.Pose pose = new PoseStack().last();
        for (BlockStateModelPart part : parts) {
            for (Direction side : SIDES_AND_NULL) {
                for (BakedQuad quad : part.getQuads(side)) {
                    consumer.putBakedQuad(pose, quad, instance);
                }
            }
        }
    }

    private static final Direction[] SIDES_AND_NULL = { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, null };

    public FramedJacketedWireModel copy() {
        return new FramedJacketedWireModel(wireModel.copy(), wireBoxes, highlightBoxes);
    }
}
