package dev.foltz.item;

import dev.foltz.Zombie7;
import dev.foltz.item.ammo.*;
import dev.foltz.item.ammo.type.MagnumBasicAmmoType;
import dev.foltz.item.ammo.type.PistolBasicAmmoType;
import dev.foltz.item.ammo.type.ShotgunBasicAmmoType;
import dev.foltz.item.ammo.type.ShotgunRubberAmmoType;
import dev.foltz.item.consumable.*;
import dev.foltz.item.grenade.*;
import dev.foltz.item.gun.ChargerItem;
import dev.foltz.item.gun.HandCannonItem;
import dev.foltz.item.misc.FireLance;
import dev.foltz.item.gun.pistol.*;
import dev.foltz.item.gun.rifle.AkRifleItem;
import dev.foltz.item.gun.rifle.MusketRifleItem;
import dev.foltz.item.gun.shotgun.Aa12ShotgunItem;
import dev.foltz.item.gun.shotgun.PumpShotgunItem;
import dev.foltz.item.gun.shotgun.dblbrlShotgunItem;
import dev.foltz.item.misc.BonkStick;
import dev.foltz.item.misc.BowlingBallItem;
import dev.foltz.item.misc.CannonBallItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class Z7Items {
    private static final Map<String, Item> ALL_ITEMS = new HashMap<>();
    private static final Map<String, Item> ALL_ITEMS_GENERAL = new HashMap<>();
    private static final Map<String, Item> ALL_ITEMS_MATERIALS = new HashMap<>();
    private static final Map<String, Item> ALL_ITEMS_CONSUMABLES = new HashMap<>();
    private static final Map<String, Item> ALL_ITEMS_GUNS = new HashMap<>();
    private static final Map<String, Item> ALL_ITEMS_GRENADES = new HashMap<>();
    private static final Map<String, Item> ALL_ITEMS_AMMO = new HashMap<>();
    private static final Map<String, Item> ALL_ITEMS_MISC_WEAPONS = new HashMap<>();

    private static final Map<String, ItemGroup> ALL_ITEM_GROUPS = new HashMap<>();



    // Materials
    public static final Item ITEM_PLANT_FIBER = registerMaterialItem("plant_fiber", new Item(new FabricItemSettings()));
    public static final Item ITEM_CLOTH_FRAGMENT = registerMaterialItem("cloth_fragment", new Item(new FabricItemSettings()));
    public static final Item ITEM_REPAIR_KIT = registerMaterialItem("repair_kit", new Item(new FabricItemSettings()));

    // Parts
    public static final Item ITEM_PART_STEEL_AXE = registerMaterialItem("part_axe_steel", new Item(new FabricItemSettings()));


    // Consumables
    public static final Item ITEM_BANDAGE = registerConsumableItem("bandage", new BandageItem());
    public static final Item ITEM_HEALING_BANDAGE = registerConsumableItem("healing_bandage", new HealingBandageItem());
    public static final Item ITEM_ANTIBIOTICS = registerConsumableItem("antibiotics", new AntibioticsItem());
    public static final Item ITEM_PAINKILLERS = registerConsumableItem("painkillers", new PainkillersItem());
    public static final Item ITEM_SPLINT = registerConsumableItem("splint", new SplintItem());

    public static final Item ITEM_COFFEE = registerConsumableItem("coffee", new Item(new FabricItemSettings().maxCount(10)));


    // Weapons
    // Grenades
    public static final Item ITEM_FRAG_GRENADE = registerGrenadeItem("frag", new FragGrenadeItem());
    public static final Item ITEM_CONTACT_GRENADE = registerGrenadeItem("contact", new ContactGrenadeItem());
    public static final Item ITEM_MOLOTOV_GRENADE = registerGrenadeItem("molotov", new MolotovGrenadeItem());
    public static final Item ITEM_STICKY_GRENADE = registerGrenadeItem("sticky", new StickyGrenadeItem());

    // Guns
    public static final Item ITEM_PISTOL_EOKA = registerGunItem("pistol_eoka", new EokaPistol());
    public static final Item ITEM_PISTOL_FLINTLOCK = registerGunItem("pistol_flintlock", new FlintlockPistolItem());
    public static final Item ITEM_PISTOL_BASIC = registerGunItem("pistol_basic", new BasicPistolItem());
    public static final Item ITEM_PISTOL_GLOCK = registerGunItem("pistol_glock", new GlockPistolItem());
    public static final Item ITEM_PISTOL_DEAGLE = registerGunItem("pistol_deagle", new DeaglePistolItem());
    public static final Item ITEM_SHOTGUN_BASIC = registerGunItem("shotgun_basic", new PumpShotgunItem());
    public static final Item ITEM_SHOTGUN_AA12 = registerGunItem("shotgun_aa12", new Aa12ShotgunItem());
    public static final Item ITEM_SHOTGUN_DBLBRL = registerGunItem("shotgun_dblbrl", new dblbrlShotgunItem());
    public static final Item ITEM_RIFLE_AK = registerGunItem("rifle_ak", new AkRifleItem());
    public static final Item ITEM_RIFLE_MUSKET = registerGunItem("rifle_musket", new MusketRifleItem());
    public static final Item ITEM_HAND_CANNON = registerGunItem("hand_cannon", new HandCannonItem());
    public static final Item ITEM_CHARGER = registerGunItem("charger", new ChargerItem());

    // Misc
    public static final Item ITEM_FIRE_LANCE = registerMiscWeaponItem("fire_lance", new FireLance());
    public static final Item ITEM_BOWLING_BALL = registerMiscWeaponItem("bowling_ball", new BowlingBallItem());
    public static final Item ITEM_CANNON_BALL = registerMiscWeaponItem("cannon_ball", new CannonBallItem());


    // Ammo
    public static final Item ITEM_AMMO_PISTOL = registerAmmoItem("pistol_basic", new AmmoPistolBasicItem());
    public static final Item ITEM_AMMO_MAGNUM = registerAmmoItem("magnum_basic", new AmmoMagnumBasicItem());
    public static final Item ITEM_AMMO_SHOTGUN = registerAmmoItem("shotgun_basic", new AmmoShotgunBasicItem());
    public static final Item ITEM_AMMO_SHOTGUN_RUBBER = registerAmmoItem("shotgun_rubber", new AmmoShotgunRubberItem());
    public static final Item ITEM_AMMO_GRAPESHOT = registerAmmoItem("grapeshot", new AmmoGrapeshotItem());

    // Debug
    public static final Item ITEM_BONK_STICK = registerItem("bonk_stick", new BonkStick());
    public static final Item ITEM_BONK_STICK_16 = registerItem("bonk_stick16", new BonkStick());


    // Item groups
    public static final ItemGroup GROUP_MATERIALS = registerItemGroup("materials", ALL_ITEMS_MATERIALS, () -> new ItemStack(ITEM_PLANT_FIBER));
    public static final ItemGroup GROUP_CONSUMABLES = registerItemGroup("consumables", ALL_ITEMS_CONSUMABLES, () -> new ItemStack(ITEM_HEALING_BANDAGE));
    public static final ItemGroup GROUP_WEAPONS = registerItemGroup("weapons", List.of(ALL_ITEMS_GUNS, ALL_ITEMS_GRENADES, ALL_ITEMS_AMMO, ALL_ITEMS_MISC_WEAPONS), () -> new ItemStack(ITEM_SHOTGUN_BASIC));
    public static final ItemGroup GROUP_GENERAL = registerItemGroup("general", ALL_ITEMS_GENERAL, () -> new ItemStack(ITEM_BONK_STICK));


    private static <T extends Item> String properName(String name, T item) {
        return name + (item instanceof CompositeResourceItem ? "/default" : "");
    }

    private static <T extends Item> T registerItem(String name, T item) {
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_GENERAL.put(name, item);
        return item;
    }

    private static <T extends Item> T registerMaterialItem(String name, T item) {
        name = "material/" + properName(name, item);
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_MATERIALS.put(name, item);
        return item;
    }

    private static <T extends Item> T registerConsumableItem(String name, T item) {
        name = "consumable/" + properName(name, item);
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_CONSUMABLES.put(name, item);
        return item;
    }

    private static <T extends Item> T registerMiscWeaponItem(String name, T item) {
        name = "misc/" + properName(name, item);
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_MISC_WEAPONS.put(name, item);
        return item;
    }

    private static <T extends Item> T registerGunItem(String name, T item) {
        name = "gun/" + properName(name, item);
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_GUNS.put(name, item);
        return item;
    }

    private static <T extends Item> T registerAmmoItem(String name, T item) {
        name = "ammo/" + properName(name, item);
        ALL_ITEMS.put(name, item);
        ALL_ITEMS_AMMO.put(name, item);
        return item;
    }

    private static <T extends Item> T registerGrenadeItem(String name, T item) {
        name = "grenade/" + properName(name, item);
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
        var itemGroup = FabricItemGroup
            .builder()
            .icon(icon)
            .entries((displayContext, entries) -> groups.forEach(group -> group.values().stream().map(ItemStack::new).forEach(entries::add)))
            .displayName(Text.translatable("itemGroup.zombie7." + name))
            .build();
        ALL_ITEM_GROUPS.put(name, itemGroup);
        return itemGroup;
    }

    public static void registerAllItems() {
        for (var entry : ALL_ITEMS.entrySet()) {
            var identifier = new Identifier(Zombie7.MODID, entry.getKey());
            Registry.register(Registries.ITEM, identifier, entry.getValue());
        }
    }

    public static void registerAllItemGroups() {
        for (var entry : ALL_ITEM_GROUPS.entrySet()) {
            var identifier = new Identifier(Zombie7.MODID, entry.getKey());
            Registry.register(Registries.ITEM_GROUP, identifier, entry.getValue());
        }
    }
}
