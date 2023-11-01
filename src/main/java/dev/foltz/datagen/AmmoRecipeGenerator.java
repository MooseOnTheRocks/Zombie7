package dev.foltz.datagen;

import dev.foltz.item.Z7Items;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;

public class AmmoRecipeGenerator extends FabricRecipeProvider {
    public AmmoRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
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
    }
}
