package mrtjp.projectred.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Base for item renderers, replacing CodeChicken Lib's removed {@code IItemRenderer}. Items use it through a
 * {@code minecraft:special} item model (see {@code ProjectRedModelProvider#specialItem}); register the
 * {@link Unbaked}'s codec with {@code RegisterSpecialModelRendererEvent}.
 * <p>
 * Display transforms come from the item model's base model. The display context isn't passed to special renderers
 * in 26.x, so {@link #renderItem} gets {@link ItemDisplayContext#NONE}.
 */
public abstract class ProjectRedItemRenderer implements SpecialModelRenderer<ItemStack> {

    @Override
    public @Nullable ItemStack extractArgument(ItemStack stack) {
        return stack;
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(0, 0, 0));
        output.accept(new Vector3f(1, 1, 1));
    }

    @Override
    public void submit(@Nullable ItemStack stack, PoseStack poseStack, SubmitNodeCollector collector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
        if (stack == null) return;
        RecordingBufferSource buffers = new RecordingBufferSource();
        renderItem(stack, ItemDisplayContext.NONE, poseStack, buffers, packedLight, packedOverlay);
        buffers.submit(collector);
    }

    public abstract void renderItem(ItemStack stack, ItemDisplayContext transformType, PoseStack mStack, RecordingBufferSource getter, int packedLight, int packedOverlay);

    /**
     * Stateless unbaked form: one instance per renderer type, serialized as just its type id.
     */
    public static final class Unbaked implements SpecialModelRenderer.Unbaked<ItemStack> {

        private final Supplier<? extends SpecialModelRenderer<ItemStack>> factory;
        private final MapCodec<Unbaked> codec;

        public Unbaked(Supplier<? extends SpecialModelRenderer<ItemStack>> factory) {
            this.factory = factory;
            this.codec = MapCodec.unit(this);
        }

        @Override
        public SpecialModelRenderer<ItemStack> bake(BakingContext context) {
            return factory.get();
        }

        @Override
        public MapCodec<Unbaked> type() {
            return codec;
        }
    }
}
