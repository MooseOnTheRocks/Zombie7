package dev.foltz;

import dev.foltz.block.Z7Blocks;
import dev.foltz.entity.*;
import dev.foltz.item.Z7Items;
import dev.foltz.network.Z7Networking;
import dev.foltz.status.StatusEffects;
import net.fabricmc.api.ModInitializer;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.WorldPreset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Zombie7 implements ModInitializer {
    public static final String MODID = "zombie7";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static final RegistryKey<WorldPreset> ZOMBIE7_WORLD = RegistryKey.of(RegistryKeys.WORLD_PRESET, new Identifier(Zombie7.MODID, "zombie7_world"));
    public static final RegistryKey<DamageType> INFECTION_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Zombie7.MODID, "infection"));
    public static final RegistryKey<DamageType> BLEEDING_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Zombie7.MODID, "bleeding"));
    public static final RegistryKey<DamageType> BLEEDING_LONG_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Zombie7.MODID, "bleeding_long"));
    public static final RegistryKey<DamageType> BULLET_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Zombie7.MODID, "bullet"));


    @Override
	public void onInitialize() {
        System.out.println("Hello, Zombie7!");

        Z7Blocks.registerAllBlocks();
        Z7Items.registerAllItems();
        Z7Entities.registerAllEntities();
        StatusEffects.registerAllStatusEffects();
        Z7Networking.registerAllEvents();
	}
}
