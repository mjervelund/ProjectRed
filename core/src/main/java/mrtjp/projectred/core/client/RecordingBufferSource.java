package mrtjp.projectred.core.client;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import codechicken.lib.vec.Transformation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stand-in for the removed {@code MultiBufferSource}: renderers write vertices per {@link RenderType} as before, and
 * {@link #submit} hands each batch to a {@link SubmitNodeCollector}.
 * <p>
 * Vertices are recorded as written. Bind through {@link #bind(CCRenderState, RenderType, PoseStack)} (or wrap the
 * consumer yourself) so positions are already in the collector's pose space; batches are replayed with an identity
 * pose.
 */
public final class RecordingBufferSource {

    private final Map<RenderType, Recorder> batches = new LinkedHashMap<>();

    public VertexConsumer getBuffer(RenderType type) {
        return batches.computeIfAbsent(type, t -> new Recorder());
    }

    /**
     * Replacement for CCL's removed {@code ccrs.bind(RenderType, MultiBufferSource, PoseStack)}.
     */
    public void bind(CCRenderState ccrs, RenderType type, PoseStack stack) {
        ccrs.bind(new TransformingVertexConsumer(getBuffer(type), stack), type);
    }

    public void bind(CCRenderState ccrs, RenderType type, Transformation transform) {
        ccrs.bind(new TransformingVertexConsumer(getBuffer(type), transform), type);
    }

    public boolean isEmpty() {
        return batches.isEmpty();
    }

    /**
     * Submits everything recorded so far and clears this source.
     */
    public void submit(SubmitNodeCollector collector) {
        if (batches.isEmpty()) return;
        PoseStack identity = new PoseStack();
        for (var entry : batches.entrySet()) {
            Recorder recorder = entry.getValue();
            if (recorder.vertexCount() == 0) continue;
            collector.submitCustomGeometry(identity, entry.getKey(), (pose, consumer) -> recorder.replay(consumer));
        }
        batches.clear();
    }

    /**
     * Records vertex attributes. Unset attributes replay as defaults (white, no UVs, no overlay, full light, up normal, 1px lines).
     */
    private static final class Recorder implements VertexConsumer {

        private final FloatArrayList floats = new FloatArrayList(); // x y z u v u3 v3 nx ny nz lineWidth
        private final IntArrayList ints = new IntArrayList();       // color uv1 uv2
        private static final int F = 11;
        private static final int I = 3;
        private int count;

        int vertexCount() {
            return count;
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            floats.add(x);
            floats.add(y);
            floats.add(z);
            for (int i = 3; i < F; i++) floats.add(i == 8 || i == 10 ? 1 : 0); // up normal, 1px line width
            ints.add(-1);
            ints.add(OverlayTexture.NO_OVERLAY);
            ints.add(0xF000F0);
            count++;
            return this;
        }

        private void setF(int offset, float value) {
            floats.set((count - 1) * F + offset, value);
        }

        private void setI(int offset, int value) {
            ints.set((count - 1) * I + offset, value);
        }

        @Override
        public VertexConsumer setColor(int r, int g, int b, int a) {
            setI(0, (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF);
            return this;
        }

        @Override
        public VertexConsumer setColor(int color) {
            setI(0, color);
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            setF(3, u);
            setF(4, v);
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            setI(1, (v & 0xFFFF) << 16 | u & 0xFFFF);
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            setI(2, (v & 0xFFFF) << 16 | u & 0xFFFF);
            return this;
        }

        @Override
        public VertexConsumer setUv3(float u, float v) {
            setF(5, u);
            setF(6, v);
            return this;
        }

        @Override
        public VertexConsumer setNormal(float x, float y, float z) {
            setF(7, x);
            setF(8, y);
            setF(9, z);
            return this;
        }

        @Override
        public VertexConsumer setLineWidth(float width) {
            setF(10, width);
            return this;
        }

        void replay(VertexConsumer out) {
            for (int i = 0; i < count; i++) {
                int f = i * F;
                int n = i * I;
                int uv1 = ints.getInt(n + 1);
                int uv2 = ints.getInt(n + 2);
                out.addVertex(floats.getFloat(f), floats.getFloat(f + 1), floats.getFloat(f + 2))
                        .setColor(ints.getInt(n))
                        .setUv(floats.getFloat(f + 3), floats.getFloat(f + 4))
                        .setUv1(uv1 & 0xFFFF, uv1 >>> 16)
                        .setUv2(uv2 & 0xFFFF, uv2 >>> 16)
                        .setUv3(floats.getFloat(f + 5), floats.getFloat(f + 6))
                        .setNormal(floats.getFloat(f + 7), floats.getFloat(f + 8), floats.getFloat(f + 9))
                        .setLineWidth(floats.getFloat(f + 10));
            }
        }
    }
}
