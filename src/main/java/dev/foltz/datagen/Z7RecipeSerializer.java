package dev.foltz.datagen;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.foltz.Zombie7;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class Z7RecipeSerializer implements RecipeSerializer<Z7Recipe> {
    public static final Z7RecipeSerializer INSTANCE = new Z7RecipeSerializer();
    public static final Identifier ID = new Identifier(Zombie7.MODID + ":z7_recipe");

    private Z7RecipeSerializer() {}

    @Override
    public Z7Recipe read(Identifier id, JsonObject json) {
        Z7RecipeJsonFormat recipeJson = new Gson().fromJson(json, Z7RecipeJsonFormat.class);
        if (recipeJson.ingredient1 == null || recipeJson.ingredient2 == null || recipeJson.result == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }

        if (recipeJson.outputAmount == 0) {
            recipeJson.outputAmount = 1;
        }

        Ingredient ingredient1 = Ingredient.fromJson(recipeJson.ingredient1);
        Ingredient ingredient2 = Ingredient.fromJson(recipeJson.ingredient2);

        Item outputItem = Registries.ITEM.getOrEmpty(new Identifier(recipeJson.result))
                .orElseThrow(() -> new JsonSyntaxException("No such item " + recipeJson.outputAmount));
        ItemStack outputStack = new ItemStack(outputItem, recipeJson.outputAmount);
        return new Z7Recipe(id, outputStack, ingredient1, ingredient2);
    }

    @Override
    public Z7Recipe read(Identifier id, PacketByteBuf buf) {
        Ingredient ingredient1 = Ingredient.fromPacket(buf);
        Ingredient ingredient2 = Ingredient.fromPacket(buf);
        ItemStack result = buf.readItemStack();
        return new Z7Recipe(id, result, ingredient1, ingredient2);
    }

    @Override
    public void write(PacketByteBuf buf, Z7Recipe recipe) {
        recipe.ingredient1.write(buf);
        recipe.ingredient2.write(buf);
        buf.writeItemStack(recipe.result);
    }
}
