package mrtjp.projectred.expansion.client;

import java.util.WeakHashMap;
import java.util.Set;
import java.util.Collections;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.Minecraft;
import mrtjp.projectred.core.client.RecordingBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import codechicken.lib.vec.*;
import com.mojang.blaze3d.vertex.*;
import mrtjp.projectred.core.client.particle.BaseActionParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;

import java.util.LinkedList;
import java.util.List;

import static mrtjp.projectred.expansion.ProjectRedExpansion.MOD_ID;

@SuppressWarnings("NotNullFieldNotInitialized")
public class PneumaticSmokeParticle extends BaseActionParticle {

    public static Identifier SMOKE_PARTICLE_LOCATION = Identifier.fromNamespaceAndPath(MOD_ID, "textures/particle/smoke.png");

    public static TextureAtlasSprite SMOKE_PARTICLE_SPRITE;

    // 26.x particles only render through vanilla's particle groups: smoke trails are drawn by onSubmitCustomGeometry.
    private static final Set<PneumaticSmokeParticle> ACTIVE = Collections.newSetFromMap(new WeakHashMap<>());

    private final List<Vector3> points = new LinkedList<>();

    public PneumaticSmokeParticle(ClientLevel pLevel, List<Vector3> points) {
        super(pLevel, points.get(0).x, points.get(0).y, points.get(0).z);

        // Set points
        this.points.addAll(points);

        // Set properties
        setParticleSpeed(0, 0, 0);
        setColor(EnumColour.LIGHT_GRAY.rF(), EnumColour.LIGHT_GRAY.gF(), EnumColour.LIGHT_GRAY.bF());
        setAlpha(1);
        setLifetime(20 * 5); // 5 seconds
        gravity = 0;

        // Set bounds
        Cuboid6 bounds = new Cuboid6(x, y, z, x, y, z);
        for (Vector3 point : points) {
            bounds.enclose(point);
        }
        setBoundingBox(bounds.aabb());

        ACTIVE.add(this);
    }


    public static void onSubmitCustomGeometry(SubmitCustomGeometryEvent event) {
        ACTIVE.removeIf(p -> !p.isAlive());
        if (ACTIVE.isEmpty()) return;

        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        Vec3 cam = event.getLevelRenderState().cameraRenderState.pos;
        RecordingBufferSource buffers = new RecordingBufferSource();
        VertexConsumer buffer = buffers.getBuffer(RenderTypes.entityTranslucent(SMOKE_PARTICLE_LOCATION));
        PoseStack stack = event.getPoseStack();
        stack.pushPose();
        VertexConsumer posed = new TransformingVertexConsumer(buffer, stack);
        for (PneumaticSmokeParticle particle : ACTIVE) {
            particle.render(posed, cam, partialTicks);
        }
        stack.popPose();
        buffers.submit(event.getSubmitNodeCollector());
    }

    private void render(VertexConsumer pBuffer, Vec3 cam, float pPartialTicks) {

        runActions(pPartialTicks);

        var t = new Translation(-cam.x, -cam.y, -cam.z);
        VertexConsumer vc = new TransformingVertexConsumer(pBuffer, t);

        // Draw lines from point to point
        Vector3 lastPoint = null;

        for (Vector3 point : points) {
            if (lastPoint != null) {
                drawSmokeLine(vc, lastPoint, point, pPartialTicks);
            }
            lastPoint = point;
        }
    }

    private void drawSmokeLine(VertexConsumer buffer, Vector3 p1, Vector3 p2, float f) {

        // Vector from start to end point
        Vector3 dp = p2.copy().subtract(p1);

        double width = 4/16D;
        double length = dp.mag();

        // Virtual point at which the quad is created at
        Vector3 quadPoint = new Vector3(0, length, 0);

        // Angle/axis transforms to change quadPoint to dp
        double angle = quadPoint.angle(dp);
        Vector3 axis = quadPoint.copy().crossProduct(dp).normalize();
        Transformation t = new Rotation(angle, axis);

        float slideProg = (age + f) / lifetime * 8;

        float u1 = 0.0f;
        float v1 = slideProg;
        float u2 = 1.0f;
        float v2 = v1 + (float) length;

        int j = getLightCoords(f);

        // 90-degree rotated on Y axis for crossed quad render
        Transformation[] transforms = {
                new TransformationList(t, p1.translation()),
                new TransformationList(Rotation.quarterRotations[1], t, p1.translation()),
        };

        for (var tf : transforms) {
            VertexConsumer bc = new TransformingVertexConsumer(buffer, tf);
            bc.addVertex((float) -width,              0, 0).setUv(u1, v2).setColor(rCol, gCol, bCol, alpha).setLight(j);
            bc.addVertex((float) -width, (float) length, 0).setUv(u1, v1).setColor(rCol, gCol, bCol, alpha).setLight(j);
            bc.addVertex((float)  width, (float) length, 0).setUv(u2, v1).setColor(rCol, gCol, bCol, alpha).setLight(j);
            bc.addVertex((float)  width,              0, 0).setUv(u2, v2).setColor(rCol, gCol, bCol, alpha).setLight(j);
        }
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }

    public static void onTextureStitchEvent(TextureAtlasStitchedEvent event) {
        if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) return;
        SMOKE_PARTICLE_SPRITE = event.getAtlas().getSprite(Identifier.fromNamespaceAndPath(MOD_ID, "particle/smoke"));
    }
}
