package dev.foltz.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;

public class Z7RecipeGenerator extends FabricRecipeProvider {
    public Z7RecipeGenerator(FabricDataOutput generator) {
        super(generator);
    }

    public static ItemStack OUTPUT = new ItemStack(Items.DIAMOND, 5);
    public static ItemStack INPUT = new ItemStack(Items.DIRT, 2);
    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, OUTPUT.getItem())
                .input(INPUT.getItem())
                .criterion(
                    FabricRecipeProvider.hasItem(OUTPUT.getItem()),
                    FabricRecipeProvider.conditionsFromItem(OUTPUT.getItem())
                )
                .criterion(
                    FabricRecipeProvider.hasItem(INPUT.getItem()),
                    FabricRecipeProvider.conditionsFromItem(INPUT.getItem())
                ).offerTo(exporter);
    }
}
