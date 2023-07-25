package dev.foltz.datagen;

import dev.foltz.item.Z7Items;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;

public class Z7RecipeGenerator extends FabricRecipeProvider {
    public final GunRecipeGenerator gunRecipes;
    public final GrenadeRecipeGenerator grenadeRecipes;

    public Z7RecipeGenerator(FabricDataOutput generator) {
        super(generator);
        gunRecipes = new GunRecipeGenerator(generator);
        grenadeRecipes = new GrenadeRecipeGenerator(generator);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        // == Guns
        gunRecipes.generate(exporter);

        // == Grenades
        grenadeRecipes.generate(exporter);

        // == Ammo
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Z7Items.ITEM_AMMO_PISTOL, 4)
            .input('^', Items.COPPER_INGOT)
            .input('o', Items.GUNPOWDER)
            .input('|', Items.IRON_NUGGET)
            .input('#', Items.IRON_INGOT)
            .pattern("|^|")
            .pattern("|o|")
            .pattern("|#|")
            .criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT))
            .criterion(hasItem(Items.GUNPOWDER), conditionsFromItem(Items.GUNPOWDER))
            .criterion(hasItem(Items.IRON_NUGGET), conditionsFromItem(Items.IRON_NUGGET))
            .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
            .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Z7Items.ITEM_AMMO_SHOTGUN, 2)
            .input('^', Items.FLINT)
            .input('o', Items.GUNPOWDER)
            .input('|', Items.IRON_NUGGET)
            .input('-', Items.GOLD_INGOT)
            .pattern("|^|")
            .pattern("|o|")
            .pattern("|-|")
            .criterion(hasItem(Items.FLINT), conditionsFromItem(Items.FLINT))
            .criterion(hasItem(Items.GUNPOWDER), conditionsFromItem(Items.GUNPOWDER))
            .criterion(hasItem(Items.IRON_NUGGET), conditionsFromItem(Items.IRON_NUGGET))
            .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
            .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Z7Items.ITEM_AMMO_MAGNUM, 4)
            .input('^', Items.COPPER_INGOT)
            .input('o', Items.GUNPOWDER)
            .input('#', Items.IRON_INGOT)
            .input('|', Items.IRON_NUGGET)
            .pattern("|^|")
            .pattern("|o|")
            .pattern("###")
            .criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT))
            .criterion(hasItem(Items.GUNPOWDER), conditionsFromItem(Items.GUNPOWDER))
            .criterion(hasItem(Items.IRON_NUGGET), conditionsFromItem(Items.IRON_NUGGET))
            .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
            .offerTo(exporter);

        // == Misc
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Z7Items.ITEM_BOWLING_BALL)
            .input('#', Items.LEATHER)
            .input('X', Items.IRON_BLOCK)
            .pattern(" # ")
            .pattern("#X#")
            .pattern(" # ")
            .criterion(hasItem(Items.LEATHER), conditionsFromItem(Items.LEATHER))
            .criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
            .offerTo(exporter);
    }
}
