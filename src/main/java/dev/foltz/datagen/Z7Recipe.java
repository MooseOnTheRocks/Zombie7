package dev.foltz.datagen;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class Z7Recipe implements Recipe<SimpleInventory> {
    public final Identifier id;
    public final Ingredient ingredient1;
    public final Ingredient ingredient2;
    public final ItemStack result;

    public Z7Recipe(Identifier id, ItemStack result, Ingredient ingredient1, Ingredient ingredient2) {
        this.id = id;
        this.result = result;
        this.ingredient1 = ingredient1;
        this.ingredient2 = ingredient2;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if (inventory.size() < 2) {
            return false;
        }
        return ingredient1.test(inventory.getStack(0)) && ingredient2.test(inventory.getStack(1));
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return result;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Z7RecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    public static class Type implements RecipeType<Z7Recipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "z7_recipe_type";
        private Type() {}
    }
}
