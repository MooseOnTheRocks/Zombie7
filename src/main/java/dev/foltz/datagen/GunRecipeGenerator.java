package dev.foltz.datagen;

import dev.foltz.item.Z7Items;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;

import java.util.function.Consumer;

public class GunRecipeGenerator extends FabricRecipeProvider {
    public GunRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        // == Guns
        // -- Pistols
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Z7Items.ITEM_PISTOL_FLINTLOCK)
            .input('X', Items.FLINT_AND_STEEL)
            .input('#', Items.IRON_INGOT)
            .input('.', Items.IRON_NUGGET)
            .input('|', Items.STICK)
            .pattern("   ")
            .pattern("##X")
            .pattern(" .|")
            .criterion(hasItem(Items.FLINT_AND_STEEL), conditionsFromItem(Items.FLINT_AND_STEEL))
            .criterion(hasItem(Items.IRON_NUGGET), conditionsFromItem(Items.IRON_NUGGET))
            .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
            .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Z7Items.ITEM_PISTOL_EOKA)
                .input('B', Items.IRON_INGOT)
                .input('H', ItemTags.LOGS)
                .input('I', Items.IRON_NUGGET)
                .pattern("  I")
                .pattern("BBH")
                .pattern(" HH")
                .criterion(hasItem(Items.IRON_NUGGET), conditionsFromItem(Items.IRON_NUGGET))
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter);

        // -- Magnums
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Z7Items.ITEM_PISTOL_DEAGLE)
            .input('#', Items.IRON_INGOT)
            .input('.', Items.IRON_NUGGET)
            .input('|', Items.STICK)
            .pattern("  #")
            .pattern("###")
            .pattern(" .|")
            .criterion(hasItem(Items.IRON_NUGGET), conditionsFromItem(Items.IRON_NUGGET))
            .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
            .offerTo(exporter);

        // -- Shotguns
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Z7Items.ITEM_SHOTGUN_BASIC)
            .input('L', Items.LEATHER)
            .input('#', Items.IRON_INGOT)
            .input('X', Items.IRON_BLOCK)
            .input('|', Items.STICK)
            .pattern("   ")
            .pattern("#X#")
            .pattern("||L")
            .criterion(hasItem(Items.LEATHER), conditionsFromItem(Items.LEATHER))
            .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
            .criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
            .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Z7Items.ITEM_SHOTGUN_AA12)
            .input('#', Items.IRON_INGOT)
            .input('X', Items.IRON_BLOCK)
            .input('.', Items.IRON_NUGGET)
            .input('|', Items.STICK)
            .pattern("   ")
            .pattern("##X")
            .pattern("X.|")
            .criterion(hasItem(Items.IRON_NUGGET), conditionsFromItem(Items.IRON_NUGGET))
            .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
            .criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
            .offerTo(exporter);

        // -- Rifles
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Z7Items.ITEM_RIFLE_AK)
            .input('#', Items.IRON_INGOT)
            .input('X', Items.IRON_BLOCK)
            .input('.', Items.IRON_NUGGET)
            .input('|', Items.STICK)
            .pattern(". .")
            .pattern("##X")
            .pattern("#.|")
            .criterion(hasItem(Items.IRON_NUGGET), conditionsFromItem(Items.IRON_NUGGET))
            .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
            .criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
            .offerTo(exporter);
    }
}
