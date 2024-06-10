package net.agent59.recipe;

import com.google.gson.JsonObject;
import net.agent59.block.entity.WandMakerBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class WandMakerRecipe implements Recipe<Inventory> {
    public static final int WOOD_RECIPE_INDEX = 0;
    public static final int CORE_RECIPE_INDEX = 1;
    private final Identifier id;
    private final ItemStack output;
    private final DefaultedList<Ingredient> recipeItems;
    private final int craftingTime;

    public WandMakerRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems, int crafting_time) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.craftingTime = crafting_time;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        boolean bl1 = recipeItems.get(WOOD_RECIPE_INDEX).test(inventory.getStack(WandMakerBlockEntity.WOOD_SLOT));
        boolean bl2 = recipeItems.get(CORE_RECIPE_INDEX).test(inventory.getStack(WandMakerBlockEntity.CORE_SLOT));
        return bl1 && bl2;
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager dynamicRegistryManager) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return recipeItems;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager dynamicRegistryManager) {
        return output.copy();
    }

    public int getCraftingTime() {
        return craftingTime;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<WandMakerRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "wand_maker";
    }

    public static class Serializer implements RecipeSerializer<WandMakerRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "wand_maker";

        @Override
        public WandMakerRecipe read(Identifier id, JsonObject json) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));

            JsonObject ingredients = JsonHelper.getObject(json, "ingredients");
            JsonObject wood = JsonHelper.getObject(ingredients, "wood");
            JsonObject core = JsonHelper.getObject(ingredients, "core");

            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(2, Ingredient.EMPTY);
            inputs.set(WOOD_RECIPE_INDEX, Ingredient.fromJson(wood));
            inputs.set(CORE_RECIPE_INDEX, Ingredient.fromJson(core));

            int crafting_time = JsonHelper.getInt(json, "crafting_time");

            return new WandMakerRecipe(id, output, inputs, crafting_time);
        }

        @Override
        public WandMakerRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(2, Ingredient.EMPTY);
            inputs.set(WOOD_RECIPE_INDEX, Ingredient.fromPacket(buf));
            inputs.set(CORE_RECIPE_INDEX, Ingredient.fromPacket(buf));

            ItemStack output = buf.readItemStack();

            int craftingTime = buf.readInt();
            return new WandMakerRecipe(id, output, inputs, craftingTime);
        }

        @Override
        public void write(PacketByteBuf buf, WandMakerRecipe recipe) {
            recipe.getIngredients().get(WOOD_RECIPE_INDEX).write(buf);
            recipe.getIngredients().get(CORE_RECIPE_INDEX).write(buf);

            buf.writeItemStack(recipe.output);

            buf.writeInt(recipe.craftingTime);
        }
    }
}
