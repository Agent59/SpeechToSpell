package net.agent59.recipe;

import net.agent59.Main;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {

    public static final RecipeType<WandMakerRecipe> WAND_MAKER_RECIPE_TYPE = registerRecipeType(WandMakerRecipe.Type.ID, WandMakerRecipe.Type.INSTANCE);
    public static final RecipeSerializer<WandMakerRecipe> WAND_MAKER_RECIPE_RECIPE_SERIALIZER = registerRecipeSerializer(WandMakerRecipe.Serializer.ID, WandMakerRecipe.Serializer.INSTANCE);

    private static <S extends RecipeType<T>, T extends Recipe<?>> RecipeType<T> registerRecipeType(String id, S type) {
        return Registry.register(Registries.RECIPE_TYPE, new Identifier(Main.MOD_ID, id), type);
    }

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerRecipeSerializer(String id, S serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(Main.MOD_ID, id), serializer);
    }

    public static void registerModRecipes() {
        Main.LOGGER.info("Registering Mod Recipes for " + Main.MOD_ID);
    }

}
