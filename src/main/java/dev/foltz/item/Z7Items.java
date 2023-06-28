package dev.foltz.item;

import dev.foltz.Zombie7;
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
import java.util.Map;
import java.util.function.Supplier;

public class Z7Items {
    private static final Map<String, Item> ALL_ITEMS = new HashMap<>();
    private static final Map<String, Item> ALL_ITEMS_GENERAL = new HashMap<>();
    private static final Map<String, Item> ALL_ITEMS_PARTS = new HashMap<>();
    private static final Map<String, Item> ALL_ITEMS_WEAPONS = new HashMap<>();


    // Materials
    public static final Item ITEM_PLANT_FIBER = registerItem("plant_fiber", new Item(new FabricItemSettings()));
    public static final Item ITEM_CLOTH_FRAGMENT = registerItem("cloth_fragment", new Item(new FabricItemSettings()));
    public static final Item ITEM_REPAIR_KIT = registerItem("repair_kit", new Item(new FabricItemSettings()));

    // Healing
    public static final Item ITEM_BANDAGE = registerItem("bandage", new Z7BandageItem());
    public static final Item ITEM_ANTIBIOTICS = registerItem("antibiotics", new Z7AntibioticsItem());
    public static final Item ITEM_PAINKILLERS = registerItem("painkillers", new Z7PainkillersItem());
    public static final Item ITEM_SPLINT = registerItem("splint", new Z7SplintItem());

    // Consumables
    public static final Item ITEM_COFFEE = registerItem("coffee", new Item(new FabricItemSettings().maxCount(5)));

    // Parts
    public static final Item ITEM_PART_STEEL_AXE = registerPartItem("part_axe_steel", new Z7PartItem());

    // Weapons
    public static final Item ITEM_GRENADE = registerWeaponItem("grenade", new Z7GrenadeItem());
    public static final Item ITEM_PISTOL_BASIC = registerWeaponItem("pistol_basic", new Z7PistolItem());
    public static final Item ITEM_PISTOL_FLINTLOCK = registerWeaponItem("pistol_flintlock", new Z7FlintlockPistolItem());
    public static final Item ITEM_SHOTGUN_BASIC = registerWeaponItem("shotgun_basic", new Z7ShotgunItem());

    // Ammo
    public static final Z7AmmoItem ITEM_AMMO_PISTOL = registerAmmoItem("ammo_pistol", new Z7AmmoBasicPistolItem());
    public static final Z7AmmoItem ITEM_AMMO_SHOTGUN = registerAmmoItem("ammo_shotgun", new Z7AmmoBasicShotgunItem());

    public static final Item ITEM_BONK_STICK = registerItem("bonk_stick", new Z7BonkStick());

    // Item groups
    public static final ItemGroup GROUP_GENERAL = registerItemGroup("general", ALL_ITEMS_GENERAL, () -> new ItemStack(ITEM_BANDAGE));
    public static final ItemGroup GROUP_PARTS = registerItemGroup("parts", ALL_ITEMS_PARTS, () -> new ItemStack(ITEM_PART_STEEL_AXE));
    public static final ItemGroup GROUP_WEAPONS = registerItemGroup("weapons", ALL_ITEMS_WEAPONS, () -> new ItemStack(ITEM_PISTOL_BASIC));


    private static <T extends Item> T  registerItem(String name, T item) {
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_GENERAL.put(name, item);
        return item;
    }

    private static <T extends Item> T  registerPartItem(String name, T item) {
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_PARTS.put(name, item);
        return item;
    }

    private static <T extends Item> T  registerWeaponItem(String name, T item) {
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_WEAPONS.put(name, item);
        return item;
    }

    private static <T extends Item> T registerAmmoItem(String name, T item) {
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_WEAPONS.put(name, item);
        return item;
    }

    public static BlockItem registerBlockItem(String name, Block block) {
        BlockItem item = new BlockItem(block, new FabricItemSettings());
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_GENERAL.put(name, item);
        return item;
    }

    protected static ItemGroup registerItemGroup(String name, Map<String, Item> group, Supplier<ItemStack> icon) {
        return FabricItemGroup
            .builder(new Identifier(Zombie7.MODID, name))
            .icon(icon)
            .entries((displayContext, entries) -> group.values().stream().map(ItemStack::new).forEach(entries::add))
            .build();
    }

    public static void registerAllItems() {
        for (var entry : ALL_ITEMS.entrySet()) {
            var identifier = new Identifier(Zombie7.MODID, entry.getKey());
            Registry.register(Registries.ITEM, identifier, entry.getValue());
        }
    }
}
