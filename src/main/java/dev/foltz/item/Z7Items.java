package dev.foltz.item;

import dev.foltz.Z7Util;
import dev.foltz.Zombie7;
import dev.foltz.item.ammo.Z7AmmoBasicPistolItem;
import dev.foltz.item.ammo.Z7AmmoBasicShotgunItem;
import dev.foltz.item.ammo.Z7AmmoItem;
import dev.foltz.item.consumable.Z7AntibioticsItem;
import dev.foltz.item.consumable.Z7BandageItem;
import dev.foltz.item.consumable.Z7PainkillersItem;
import dev.foltz.item.consumable.Z7SplintItem;
import dev.foltz.item.grenade.Z7BowlingBallGrenadeItem;
import dev.foltz.item.grenade.Z7FragGrenadeItem;
import dev.foltz.item.grenade.Z7MolotovGrenadeItem;
import dev.foltz.item.grenade.Z7StickyGrenadeItem;
import dev.foltz.item.gun.pistol.Z7BasicPistolItem;
import dev.foltz.item.gun.pistol.Z7FlintlockPistolItem;
import dev.foltz.item.gun.rifle.Z7AkRifleItem;
import dev.foltz.item.gun.shotgun.Z7Aa12Shotgun;
import dev.foltz.item.gun.shotgun.Z7PumpShotgunItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static dev.foltz.item.grenade.Z7FragGrenadeItem.GLOBAL_STAGE_PRIMED;

public abstract class Z7Items {
    private static final Map<String, Item> ALL_ITEMS = new HashMap<>();
    private static final Map<String, Item> ALL_ITEMS_GENERAL = new HashMap<>();
    private static final Map<String, Item> ALL_ITEMS_MATERIALS = new HashMap<>();
    private static final Map<String, Item> ALL_ITEMS_CONSUMABLES = new HashMap<>();
    private static final Map<String, Item> ALL_ITEMS_GUNS = new HashMap<>();
    private static final Map<String, Item> ALL_ITEMS_GRENADES = new HashMap<>();
    private static final Map<String, Item> ALL_ITEMS_AMMO = new HashMap<>();
    private static final Map<String, Item> ALL_ITEMS_MISC_WEAPONS = new HashMap<>();



    // Materials
    public static final Item ITEM_PLANT_FIBER = registerMaterialItem("plant_fiber", new Item(new FabricItemSettings()));
    public static final Item ITEM_CLOTH_FRAGMENT = registerMaterialItem("cloth_fragment", new Item(new FabricItemSettings()));
    public static final Item ITEM_REPAIR_KIT = registerMaterialItem("repair_kit", new Item(new FabricItemSettings()));

    // Parts
    public static final Item ITEM_PART_STEEL_AXE = registerMaterialItem("part_axe_steel", new Item(new FabricItemSettings()));


    // Consumables
    public static final Item ITEM_BANDAGE = registerConsumableItem("bandage", new Z7BandageItem());
    public static final Item ITEM_ANTIBIOTICS = registerConsumableItem("antibiotics", new Z7AntibioticsItem());
    public static final Item ITEM_PAINKILLERS = registerConsumableItem("painkillers", new Z7PainkillersItem());
    public static final Item ITEM_SPLINT = registerConsumableItem("splint", new Z7SplintItem());

    public static final Item ITEM_COFFEE = registerConsumableItem("coffee", new Item(new FabricItemSettings().maxCount(10)));


    // Weapons
    public static final Item ITEM_FRAG_GRENADE = registerGrenadeItem("frag", new Z7FragGrenadeItem());
    public static final Item ITEM_MOLOTOV_GRENADE = registerGrenadeItem("molotov", new Z7MolotovGrenadeItem());
    public static final Item ITEM_STICKY_GRENADE = registerGrenadeItem("sticky", new Z7StickyGrenadeItem());
    public static final Item ITEM_BOWLING_BALL_GRENADE = registerGrenadeItem("bowling_ball", new Z7BowlingBallGrenadeItem());

    public static final Item ITEM_PISTOL_BASIC = registerGunItem("pistol_basic", new Z7BasicPistolItem());
    public static final Item ITEM_PISTOL_FLINTLOCK = registerGunItem("pistol_flintlock", new Z7FlintlockPistolItem());
    public static final Item ITEM_SHOTGUN_BASIC = registerGunItem("shotgun_basic", new Z7PumpShotgunItem());
    public static final Item ITEM_SHOTGUN_AA12 = registerGunItem("shotgun_aa12", new Z7Aa12Shotgun());
    public static final Item ITEM_RIFLE_AK = registerGunItem("rifle_ak", new Z7AkRifleItem());


    // Ammo
    public static final Z7AmmoItem ITEM_AMMO_PISTOL = registerAmmoItem("pistol_basic", new Z7AmmoBasicPistolItem());
    public static final Z7AmmoItem ITEM_AMMO_SHOTGUN = registerAmmoItem("shotgun_basic", new Z7AmmoBasicShotgunItem());

    public static final Item ITEM_BONK_STICK = registerItem("bonk_stick", new Z7BonkStick());


    // Item groups
    public static final ItemGroup GROUP_MATERIALS = registerItemGroup("materials", ALL_ITEMS_MATERIALS, () -> new ItemStack(ITEM_PLANT_FIBER));
    public static final ItemGroup GROUP_CONSUMABLES = registerItemGroup("consumables", ALL_ITEMS_CONSUMABLES, () -> new ItemStack(ITEM_BANDAGE));
    public static final ItemGroup GROUP_WEAPONS = registerItemGroup("weapons", List.of(ALL_ITEMS_GUNS, ALL_ITEMS_GRENADES, ALL_ITEMS_AMMO, ALL_ITEMS_MISC_WEAPONS), () -> new ItemStack(ITEM_SHOTGUN_BASIC));
    public static final ItemGroup GROUP_GENERAL = registerItemGroup("general", ALL_ITEMS_GENERAL, () -> new ItemStack(ITEM_BONK_STICK));


    private static <T extends Item> T  registerItem(String name, T item) {
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_GENERAL.put(name, item);
        return item;
    }

    private static <T extends Item> T registerMaterialItem(String name, T item) {
        name = "material/" + name;
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_MATERIALS.put(name, item);
        return item;
    }

    private static <T extends Item> T registerConsumableItem(String name, T item) {
        name = "consumable/" + name;
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_CONSUMABLES.put(name, item);
        return item;
    }

    private static <T extends Item> T registerWeaponItem(String name, T item) {
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_MISC_WEAPONS.put(name, item);
        return item;
    }

    private static <T extends Item> T registerGunItem(String name, T item) {
        name = "gun/" + name + "/default";
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_GUNS.put(name, item);
        return item;
    }

    private static <T extends Item> T registerAmmoItem(String name, T item) {
        name = "ammo/" + name;
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_AMMO.put(name, item);
        return item;
    }

    private static <T extends Item> T registerGrenadeItem(String name, T item) {
        name = "grenade/" + name + "/default";
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_GRENADES.put(name, item);
        return item;
    }

    public static BlockItem registerBlockItem(String name, Block block) {
        BlockItem item = new BlockItem(block, new FabricItemSettings());
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_GENERAL.put(name, item);
        return item;
    }

    protected static ItemGroup registerItemGroup(String name, Map<String, Item> group, Supplier<ItemStack> icon) {
        return registerItemGroup(name, List.of(group), icon);
    }

    protected static ItemGroup registerItemGroup(String name, List<Map<String, Item>> groups, Supplier<ItemStack> icon) {
        return FabricItemGroup
            .builder(new Identifier(Zombie7.MODID, name))
            .icon(icon)
            .entries((displayContext, entries) -> groups.forEach(group -> group.values().stream().map(ItemStack::new).forEach(entries::add)))
            .build();
    }

    public static void registerAllItems() {
        for (var entry : ALL_ITEMS.entrySet()) {
            var identifier = new Identifier(Zombie7.MODID, entry.getKey());
            Registry.register(Registries.ITEM, identifier, entry.getValue());
        }
    }
}
