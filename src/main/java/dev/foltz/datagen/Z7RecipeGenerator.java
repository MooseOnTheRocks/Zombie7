package dev.foltz.datagen;

import dev.foltz.item.Z7Items;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;

public class Z7RecipeGenerator extends FabricRecipeProvider {
    public final GunRecipeGenerator gunRecipes;
    public final GrenadeRecipeGenerator grenadeRecipes;
    public final AmmoRecipeGenerator ammoRecipes;

    public Z7RecipeGenerator(FabricDataOutput generator) {
        super(generator);
        gunRecipes = new GunRecipeGenerator(generator);
        grenadeRecipes = new GrenadeRecipeGenerator(generator);
        ammoRecipes = new AmmoRecipeGenerator(generator);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        gunRecipes.generate(exporter);
        grenadeRecipes.generate(exporter);
        ammoRecipes.generate(exporter);

        // == Consumables
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, Z7Items.ITEM_ANTIBIOTICS)
            .input(Items.HONEY_BOTTLE)
            .input(Items.GLISTERING_MELON_SLICE)
            .input(Items.SUGAR)
            .input(Items.EGG)
            .criterion(hasItem(Items.HONEY_BOTTLE), conditionsFromItem(Items.HONEY_BOTTLE))
            .criterion(hasItem(Items.GLISTERING_MELON_SLICE), conditionsFromItem(Items.GLISTERING_MELON_SLICE))
            .criterion(hasItem(Items.SUGAR), conditionsFromItem(Items.SUGAR))
            .criterion(hasItem(Items.EGG), conditionsFromItem(Items.EGG))
            .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, Z7Items.ITEM_PAINKILLERS)
            .input(Items.POPPY)
            .input(Items.FERMENTED_SPIDER_EYE)
            .input(Items.SUGAR)
            .criterion(hasItem(Items.POPPY), conditionsFromItem(Items.POPPY))
            .criterion(hasItem(Items.FERMENTED_SPIDER_EYE), conditionsFromItem(Items.FERMENTED_SPIDER_EYE))
            .criterion(hasItem(Items.SUGAR), conditionsFromItem(Items.SUGAR))
            .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Z7Items.ITEM_SPLINT)
            .input('#', Items.STICK)
            .input('X', Items.WHITE_WOOL)
            .input('s', Items.STRING)
            .pattern("#X#")
            .pattern("#s#")
            .pattern("#X#")
            .criterion(hasItem(Items.STICK), conditionsFromItem(Items.WHITE_WOOL))
            .criterion(hasItem(Items.WHITE_WOOL), conditionsFromItem(Items.WHITE_WOOL))
            .criterion(hasItem(Items.STRING), conditionsFromItem(Items.STRING))
            .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Z7Items.ITEM_HEALING_BANDAGE)
            .input('#', Items.HONEYCOMB)
            .input('X', Items.WHITE_WOOL)
            .input('s', Items.STRING)
            .pattern("sXs")
            .pattern("X#X")
            .pattern("sXs")
            .criterion(hasItem(Items.HONEYCOMB), conditionsFromItem(Items.HONEYCOMB))
            .criterion(hasItem(Items.WHITE_WOOL), conditionsFromItem(Items.WHITE_WOOL))
            .criterion(hasItem(Items.STRING), conditionsFromItem(Items.STRING))
            .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Z7Items.ITEM_HEALING_BANDAGE)
            .input('X', Items.WHITE_WOOL)
            .input('s', Items.STRING)
            .pattern("sss")
            .pattern("sXs")
            .pattern("sss")
            .criterion(hasItem(Items.WHITE_WOOL), conditionsFromItem(Items.WHITE_WOOL))
            .criterion(hasItem(Items.STRING), conditionsFromItem(Items.STRING))
            .offerTo(exporter);

        // == Misc
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Z7Items.ITEM_BOWLING_BALL)
            .input('#', Items.LEATHER)
            .input('X', Items.IRON_BLOCK)
            .pattern("###")
            .pattern("#X#")
            .pattern("###")
            .criterion(hasItem(Items.LEATHER), conditionsFromItem(Items.LEATHER))
            .criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
            .offerTo(exporter);
    }
}
