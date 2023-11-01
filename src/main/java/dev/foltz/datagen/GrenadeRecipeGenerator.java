package dev.foltz.datagen;

import dev.foltz.item.Z7Items;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;

import java.util.function.Consumer;

public class GrenadeRecipeGenerator extends FabricRecipeProvider {
    public GrenadeRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        // == Grenades
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Z7Items.ITEM_FRAG_GRENADE)
            .input('#', Items.FLINT)
            .input('.', Items.IRON_NUGGET)
            .input('X', Items.TNT)
            .pattern(".#.")
            .pattern("#X#")
            .pattern(".#.")
            .criterion(hasItem(Items.TNT), conditionsFromItem(Items.TNT))
            .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, Z7Items.ITEM_STICKY_GRENADE)
            .input(Z7Items.ITEM_FRAG_GRENADE)
            .input(Items.SLIME_BALL)
            .criterion(hasItem(Z7Items.ITEM_FRAG_GRENADE), conditionsFromItem(Z7Items.ITEM_FRAG_GRENADE))
            .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Z7Items.ITEM_CONTACT_GRENADE)
            .input('_', Items.HEAVY_WEIGHTED_PRESSURE_PLATE)
            .input('.', Items.IRON_NUGGET)
            .input('X', Items.TNT)
            .pattern("._.")
            .pattern("_X_")
            .pattern("._.")
            .criterion(hasItem(Items.TNT), conditionsFromItem(Items.TNT))
            .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, Z7Items.ITEM_MOLOTOV_GRENADE)
            .input(ItemTags.WOOL)
            .input(Items.GLASS_BOTTLE)
            .input(Items.STRING)
            .criterion(hasItem(Items.GLASS_BOTTLE), conditionsFromItem(Items.GLASS_BOTTLE))
            .offerTo(exporter);
    }
}
