package dev.foltz;

import dev.foltz.block.Z7Blocks;
import dev.foltz.entity.Z7BulletEntity;
import dev.foltz.entity.Z7GrenadeEntity;
import dev.foltz.item.Z7Items;
import dev.foltz.network.Z7Networking;
import dev.foltz.status.Z7StatusEffects;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.mixin.item.RecipeMixin;
import net.minecraft.block.AnvilBlock;
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RepairItemRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.WorldPreset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ItemEvent;

public class Zombie7 implements ModInitializer {
    public static final String MODID = "zombie7";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static final RegistryKey<WorldPreset> ZOMBIE7_WORLD = RegistryKey.of(RegistryKeys.WORLD_PRESET, new Identifier(Zombie7.MODID, "zombie7_world"));
    public static final RegistryKey<DamageType> INFECTION_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Zombie7.MODID, "infection"));
    public static final RegistryKey<DamageType> BLEEDING_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Zombie7.MODID, "bleeding"));
    public static final RegistryKey<DamageType> BLEEDING_LONG_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Zombie7.MODID, "bleeding_long"));

    public static final TagKey<Item> AMMO_TYPE_PISTOL = TagKey.of(RegistryKeys.ITEM, new Identifier(MODID, "ammo_type_pistol"));
    public static final TagKey<Item> AMMO_TYPE_SHOTGUN = TagKey.of(RegistryKeys.ITEM, new Identifier(MODID, "ammo_type_shotgun"));

    public static final EntityType<Z7GrenadeEntity> GRENADE_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MODID, "grenade"),
            FabricEntityTypeBuilder.create().entityFactory(Z7GrenadeEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build()
    );

    public static final EntityType<Z7BulletEntity> BULLET_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MODID, "bullet"),
            FabricEntityTypeBuilder.create().entityFactory(Z7BulletEntity::new).dimensions(EntityDimensions.fixed(0.15f, 0.15f)).build()
    );

    @Override
	public void onInitialize() {
        System.out.println("Hello, Zombie7!");
        Z7Blocks.registerAllBlocks();
        Z7Items.registerAllItems();
        Z7StatusEffects.registerAllStatusEffects();
        Z7Networking.registerAllEvents();
	}
}
