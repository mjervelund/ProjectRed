package mrtjp.projectred.core.client;

import codechicken.lib.render.BlockRenderer;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import codechicken.lib.vec.*;
import com.mojang.blaze3d.vertex.PoseStack;
import mrtjp.projectred.api.MovingBlockEntityRenderCallback;
import mrtjp.projectred.api.ProjectRedAPI;
import mrtjp.projectred.core.Configurator;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Light halos for illumar lamps and lights.
 * <p>
 * 26.3 port: halos are additive glow geometry submitted through {@link SubmitCustomGeometryEvent}. The Fabulous-only
 * bloom post chain ({@code fabulousLights}) is not ported yet: its render types and post chain were built on the
 * removed {@code RenderStateShard}/{@code PostChain} APIs. Entity/item bloom requests are accepted and dropped.
 */
public class HaloRenderer {

    // Position + colour, additive blending, no depth write: the same look as the old unfabulous halo type.
    private static final RenderType HALO_GLOW_RENDER_TYPE = RenderTypes.lightning();

    // Ring buffer of all pending halos to render. Capped by config
    private static final LinkedList<HaloRenderData> levelHalos = new LinkedList<>();
    // Bloom requests from item/entity renderers; only consumed by the (unported) Fabulous bloom pass.
    private static final LinkedList<HaloRenderData> entityHalos = new LinkedList<>();

    private static final Vector3 offset = Vector3.ZERO.copy();

    //region Init and event handlers
    public static void init() {
        // Register callback for moving block entities
        if (ProjectRedAPI.expansionAPI != null) {
            ProjectRedAPI.expansionAPI.registerBlockEntityRenderCallback(new MovingBlockEntityRenderCallback() {
                @Override
                public void onMovingPreRender(double offsetX, double offsetY, double offsetZ) {
                    offset.set(offsetX, offsetY, offsetZ);
                }

                @Override
                public void onMovingPostRender() {
                    offset.set(0, 0, 0);
                }
            });
        }
    }

    public static void onSubmitCustomGeometry(final SubmitCustomGeometryEvent event) {
        entityHalos.clear();
        if (levelHalos.isEmpty()) return;

        List<HaloRenderData> lightList = pollHalos(levelHalos);
        Vec3 cam = event.getLevelRenderState().cameraRenderState.pos;
        PoseStack stack = event.getPoseStack();
        stack.pushPose();
        stack.translate(-cam.x, -cam.y, -cam.z);
        event.getSubmitNodeCollector().submitCustomGeometry(stack, HALO_GLOW_RENDER_TYPE, (pose, consumer) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(new TransformingVertexConsumer(consumer, new Matrix4(pose.pose())), HALO_GLOW_RENDER_TYPE);
            for (var light : lightList) {
                renderToCCRS(ccrs, light.box, light.t, light.levelGlowRgba);
            }
        });
        stack.popPose();
    }
    //endregion

    //region Level rendering
    public static void addLight(BlockPos pos, Cuboid6 box, int colourIndex) {
        addLight(new Translation(pos), box, colourIndex);
    }

    public static void addLight(Transformation t, Cuboid6 box, int colourIndex) {
        Transformation t2 = new TransformationList(t, new Translation(offset));
        addHalo(levelHalos, new HaloRenderData(box, t2).setColourIndex(colourIndex));
    }

    public static void addMultiLight(BlockPos pos, Cuboid6 box, byte[] alphas) {
        addMultiLight(new Translation(pos), box, alphas);
    }

    public static void addMultiLight(Transformation t, Cuboid6 box, byte[] alphas) {
        Transformation t2 = new TransformationList(t, new Translation(offset));
        addHalo(levelHalos, new HaloRenderData(box, t2).setMultiColourAlphas(alphas));
    }

    private static void addHalo(LinkedList<HaloRenderData> list, HaloRenderData data) {
        list.add(data);
        if (Configurator.CLIENT.lightHaloMax.get() > -1 && list.size() > Configurator.CLIENT.lightHaloMax.get()) {
            list.poll();
        }
    }

    private static List<HaloRenderData> pollHalos(LinkedList<HaloRenderData> src) {
        List<HaloRenderData> dest = new LinkedList<>();
        HaloRenderData l;
        while ((l = src.poll()) != null) {
            dest.add(l);
        }
        return dest;
    }
    //endregion

    //region Render functions
    private static void renderToCCRS(CCRenderState ccrs, Cuboid6 cuboid, Transformation t, int rgba) {
        ccrs.setPipeline(t);
        ccrs.baseColour = rgba;
        BlockRenderer.renderCuboid(ccrs, cuboid, 0);
    }

    public static void renderInventoryHalo(PoseStack mStack, SubmitNodeCollector collector, Cuboid6 cuboid, Vector3 pos, int colourIndex) {
        int rgba = getBaseColour(colourIndex, HaloContext.ITEM_GLOW);
        renderInventoryHaloRgba(mStack, collector, cuboid, pos, rgba);
    }

    public static void renderInventoryMultiHalo(PoseStack mStack, SubmitNodeCollector collector, Cuboid6 cuboid, Vector3 pos, byte[] alphas) {
        int rgba = getBlendedColour(alphas, HaloContext.ITEM_GLOW);
        renderInventoryHaloRgba(mStack, collector, cuboid, pos, rgba);
    }

    private static void renderInventoryHaloRgba(PoseStack mStack, SubmitNodeCollector collector, Cuboid6 cuboid, Vector3 pos, int rgba) {
        collector.submitCustomGeometry(mStack, HALO_GLOW_RENDER_TYPE, (pose, consumer) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(new TransformingVertexConsumer(consumer, new Matrix4(pose.pose())), HALO_GLOW_RENDER_TYPE);
            renderToCCRS(ccrs, cuboid, pos.translation(), rgba);
        });
    }

    // Same as above, for renderers drawing through a RecordingBufferSource (the old MultiBufferSource call shape).
    public static void renderInventoryHalo(CCRenderState ccrs, PoseStack mStack, RecordingBufferSource buffers, Cuboid6 cuboid, Vector3 pos, int colourIndex) {
        renderInventoryHaloRgba(ccrs, mStack, buffers, cuboid, pos, getBaseColour(colourIndex, HaloContext.ITEM_GLOW));
    }

    public static void renderInventoryMultiHalo(CCRenderState ccrs, PoseStack mStack, RecordingBufferSource buffers, Cuboid6 cuboid, Vector3 pos, byte[] alphas) {
        renderInventoryHaloRgba(ccrs, mStack, buffers, cuboid, pos, getBlendedColour(alphas, HaloContext.ITEM_GLOW));
    }

    private static void renderInventoryHaloRgba(CCRenderState ccrs, PoseStack mStack, RecordingBufferSource buffers, Cuboid6 cuboid, Vector3 pos, int rgba) {
        ccrs.reset();
        buffers.bind(ccrs, HALO_GLOW_RENDER_TYPE, mStack);
        renderToCCRS(ccrs, cuboid, pos.translation(), rgba);
    }

    public static void addItemRendererBloom(ItemDisplayContext transformType, PoseStack stack, Vector3 pos, Cuboid6 box, int colourIndex) {
        if (isEntityItemRenderType(transformType)) {
            addItemRendererBloom(stack, pos, box, colourIndex);
        }
    }

    public static void addItemRendererBloom(PoseStack stack, Vector3 pos, Cuboid6 box, int colourIndex) {
        Transformation t = new Matrix4(stack.last().pose()).with(pos.translation());
        addHalo(entityHalos, new HaloRenderData(box, t).setColourIndex(colourIndex));
    }

    public static void addItemRendererMultiBloom(ItemDisplayContext transformType, PoseStack stack, Vector3 pos, Cuboid6 box, byte[] alphas) {
        if (isEntityItemRenderType(transformType)) {
            addItemRendererMultiBloom(stack, pos, box, alphas);
        }
    }

    public static void addItemRendererMultiBloom(PoseStack stack, Vector3 pos, Cuboid6 box, byte[] alphas) {
        Transformation t = new Matrix4(stack.last().pose()).with(pos.translation());
        addHalo(entityHalos, new HaloRenderData(box, t).setMultiColourAlphas(alphas));
    }

    private static boolean isEntityItemRenderType(ItemDisplayContext transformType) {
        return switch (transformType) {
            case FIXED, GROUND, THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> true;
            default -> false;
        };
    }
    //endregion

    //region Colour calculations
    private static int getBaseColour(int colorIndex, HaloContext context) {
        return LightColours.byIndex(colorIndex).rgbaByContext(context);
    }

    /**
     * Mix all 16 colours given input array of alpha values. Colours are additively blended
     * using same calculations as GL_SRC_ALPHA/GL_ONE_MINUS_SRC_ALPHA blending.
     *
     * @param alphas 16-element array of alpha values
     * @return Blended colour (rgba)
     */
    private static int getBlendedColour(byte[] alphas, HaloContext context) {
        // Find the total alpha
        int aTotal = 0;
        int aMax = 0;
        for (int i = 0; i < 16; i++) {
            aTotal += alphas[i] & 0xFF;
            aMax = Math.max(aMax, alphas[i] & 0xFF);
        }

        if (aTotal == 0) {
            return 0;
        }

        // Normalize alpha values
        float[] aNorm = new float[16];
        for (int i = 0; i < 16; i++) {
            aNorm[i] = (alphas[i] & 0xFF) / (float) aTotal * aMax / 255f;
        }

        float r = 0, g = 0, b = 0, a = 0;
        for (int i = 0; i < 16; i++) {
            if (alphas[i] == 0) continue;

            int colour = getBaseColour(i, context);
            float rsrc = ((colour >> 24) & 0xFF) / 255f;
            float gsrc = ((colour >> 16) & 0xFF) / 255f;
            float bsrc = ((colour >> 8) & 0xFF) / 255f;
            float asrc = aNorm[i];

            r = rsrc * asrc + r * (1 - asrc);
            g = gsrc * asrc + g * (1 - asrc);
            b = bsrc * asrc + b * (1 - asrc);
            a = asrc * asrc + a * (1 - asrc);
        }

        // Note: Below alpha controls how halo renderer blends it into the render target, not alpha used to blend colours together
        return (int) (r * 255) << 24 | (int) (g * 255) << 16 | (int) (b * 255) << 8 | 0xA0;
    }

    private enum HaloContext {
        ITEM_GLOW,
        LEVEL_GLOW,
        BLOOM
    }

    /**
     * Colours used for Halo glow and bloom effects. They are traditional Minecraft
     * wool colours with scaled brightness components to reduce variation in
     * perceived brightness.
     */
    private enum LightColours {

        /*
        Default colour HSB values. Obvious problem is with the white colour, which
        ends up looking substantially brighter than the others.

        WHITE        rgba: ffffffffa0, hsb: [0.0,          0.0,         1.0        ]
        ORANGE       rgba: c06300ffa0, hsb: [0.0859375,    1.0,         0.7529412  ]
        MAGENTA      rgba: b51ab5ffa0, hsb: [0.8333333,    0.8563536,   0.70980394 ]
        LIGHT_BLUE   rgba: 6f84f1ffa0, hsb: [0.63974357,   0.5394191,   0.94509804 ]
        YELLOW       rgba: bfbf00ffa0, hsb: [0.16666667,   1.0,         0.7490196  ]
        LIME         rgba: 6bf100ffa0, hsb: [0.2593361,    1.0,         0.94509804 ]
        PINK         rgba: f14675ffa0, hsb: [0.954191,     0.7095436,   0.94509804 ]
        GRAY         rgba: 535353ffa0, hsb: [0.0,          0.0,         0.3254902  ]
        LIGHT_GRAY   rgba: 939393ffa0, hsb: [0.0,          0.0,         0.5764706  ]
        CYAN         rgba: 008787ffa0, hsb: [0.5,          1.0,         0.5294118  ]
        PURPLE       rgba: 5e00c0ffa0, hsb: [0.7482639,    1.0,         0.7529412  ]
        BLUE         rgba: 1313c0ffa0, hsb: [0.6666667,    0.9010417,   0.7529412  ]
        BROWN        rgba: 4f2700ffa0, hsb: [0.08227848,   1.0,         0.30980393 ]
        GREEN        rgba: 088700ffa0, hsb: [0.3234568,    1.0,         0.5294118  ]
        RED          rgba: a20f06ffa0, hsb: [0.009615381,  0.962963,    0.63529414 ]
        BLACK        rgba: 1f1f1fffa0, hsb: [0.0,          0.0,         0.12156863 ]
        */

        //@formatter:off
        WHITE     (0xFFFFFFA0, 0.9F, 0.8F, 0.8F),
        ORANGE    (0xC06300A0, 0.8F, 0.8F, 0.8F),
        MAGENTA   (0xB51AB5A0, 0.8F, 0.8F, 0.8F),
        LIGHT_BLUE(0x6F84F1A0, 0.9F, 0.9F, 0.9F),
        YELLOW    (0xBFBF00A0, 0.8F, 0.8F, 0.8F),
        LIME      (0x6BF100A0, 0.9F, 0.9F, 0.9F),
        PINK      (0xF14675A0, 0.9F, 0.9F, 0.9F),
        GRAY      (0x535353A0, 0.5F, 0.5F, 0.5F),
        LIGHT_GRAY(0x939393A0, 0.6F, 0.6F, 0.6F),
        CYAN      (0x008787A0, 0.8F, 0.8F, 0.8F),
        PURPLE    (0x5E00C0A0, 0.8F, 0.8F, 0.8F),
        BLUE      (0x1313C0A0, 0.8F, 0.8F, 0.8F),
        BROWN     (0x4F2700A0, 0.5F, 0.5F, 0.5F),
        GREEN     (0x088700A0, 0.7F, 0.7F, 0.7F),
        RED       (0xA20F06A0, 0.8F, 0.8F, 0.8F),
        BLACK     (0x1F1F1FA0, 0.4F, 0.4F, 0.4F);
        //@formatter:on

        //noinspection FieldCanBeLocal
        public final int blockGlowRgba;
        public final int itemGlowRgba;
        public final int bloomRgba;

        LightColours(int rgba, float glowBrightness, float itemGlowBrightness, float bloomBrightness) {
            this.blockGlowRgba = scaleBrightness(rgba, glowBrightness);
            this.itemGlowRgba = scaleBrightness(rgba, itemGlowBrightness);
            this.bloomRgba = scaleBrightness(rgba, bloomBrightness);
        }

        public int rgbaByContext(HaloContext context) {
            return switch (context) {
                case ITEM_GLOW -> itemGlowRgba;
                case LEVEL_GLOW -> blockGlowRgba;
                case BLOOM -> bloomRgba;
            };
        }

        private static int scaleBrightness(int baseRgba, float brightness) {
            int r = (baseRgba >> 24) & 0xFF;
            int g = (baseRgba >> 16) & 0xFF;
            int b = (baseRgba >> 8) & 0xFF;
            int a = baseRgba & 0xFF;

            float[] hsb = new float[3];
            Color.RGBtoHSB(r, g, b, hsb);

            return Color.HSBtoRGB(hsb[0], hsb[1], brightness) << 8 | a;
        }

        public static LightColours byIndex(int index) {
            return values()[index];
        }
    }
    //endregion

    private static class HaloRenderData {
        public final Cuboid6 box;
        public final Transformation t;
        public int levelGlowRgba;
        public int inventoryGlowRgba;
        public int bloomRgba;

        public HaloRenderData(Cuboid6 box, Transformation t) {
            this.box = box;
            this.t = t;
        }

        public HaloRenderData setColourIndex(int colourIndex) {
            levelGlowRgba = getBaseColour(colourIndex, HaloContext.LEVEL_GLOW);
            inventoryGlowRgba = getBaseColour(colourIndex, HaloContext.ITEM_GLOW);
            bloomRgba = getBaseColour(colourIndex, HaloContext.BLOOM);
            return this;
        }

        public HaloRenderData setMultiColourAlphas(byte[] alphas) {
            levelGlowRgba = getBlendedColour(alphas, HaloContext.LEVEL_GLOW);
            inventoryGlowRgba = getBlendedColour(alphas, HaloContext.ITEM_GLOW);
            bloomRgba = getBlendedColour(alphas, HaloContext.BLOOM);
            return this;
        }
    }
}
