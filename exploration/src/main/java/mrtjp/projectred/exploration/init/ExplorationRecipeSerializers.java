package mrtjp.projectred.exploration.init;

import mrtjp.projectred.exploration.item.crafting.BackpackDyeRecipe;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

import static mrtjp.projectred.exploration.ProjectRedExploration.RECIPE_SERIALIZERS;

@SuppressWarnings("NotNullFieldNotInitialized")
public class ExplorationRecipeSerializers {

    public static final String ID_BACKPACK_DYE = "backpack_dye";

    public static Supplier<RecipeSerializer<BackpackDyeRecipe>> BACKPACK_DYE_RECIPE_SERIALIZER;

    public static void register() {

        BACKPACK_DYE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(ID_BACKPACK_DYE, () -> new RecipeSerializer<>(MapCodec.unit(BackpackDyeRecipe.INSTANCE), StreamCodec.unit(BackpackDyeRecipe.INSTANCE)));
    }
}
