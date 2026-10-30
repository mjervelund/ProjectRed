package mrtjp.projectred.expansion.client;

import com.mojang.serialization.MapCodec;
import mrtjp.projectred.expansion.item.BatteryBoxStorageComponent;
import mrtjp.projectred.expansion.item.RecipePlanComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Item model properties (formerly {@code ItemProperties} overrides), registered in {@code ExpansionClientInit}.
 */
public final class ExpansionItemProperties {

    private ExpansionItemProperties() {
    }

    /** Stored power of a battery box item, as its render level (0-8). */
    public record ChargeLevel() implements RangeSelectItemModelProperty {

        public static final MapCodec<ChargeLevel> CODEC = MapCodec.unit(new ChargeLevel());

        @Override
        public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
            var component = BatteryBoxStorageComponent.getComponent(stack);
            return component != null ? component.storedPowerRenderLevel() : 0.0F;
        }

        @Override
        public MapCodec<ChargeLevel> type() {
            return CODEC;
        }
    }

    /** Whether a recipe plan holds a valid recipe. */
    public record WrittenRecipePlan() implements ConditionalItemModelProperty {

        public static final MapCodec<WrittenRecipePlan> CODEC = MapCodec.unit(new WrittenRecipePlan());

        @Override
        public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
            var recipeData = RecipePlanComponent.getComponent(stack);
            return recipeData != null && recipeData.isRecipeValid();
        }

        @Override
        public MapCodec<WrittenRecipePlan> type() {
            return CODEC;
        }
    }
}
