package dev.foltz;

import com.mojang.datafixers.util.Function4;
import dev.foltz.item.grenade.MolotovGrenadeItem;
import dev.foltz.item.grenade.StickyGrenadeItem;
import dev.foltz.item.gun.GunStagedItem;
import dev.foltz.item.grenade.FragGrenadeItem;
import dev.foltz.item.Z7Items;
import dev.foltz.item.gun.pistol.DeaglePistolItem;
import dev.foltz.item.gun.pistol.EokaPistol;
import dev.foltz.item.gun.pistol.FlintlockPistolItem;
import dev.foltz.item.gun.rifle.AkRifleItem;
import dev.foltz.item.gun.shotgun.Aa12ShotgunItem;
import dev.foltz.item.gun.shotgun.PumpShotgunItem;
import dev.foltz.item.gun.shotgun.dblbrlShotgunItem;
import dev.foltz.item.stage.StagedItem;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class Z7ModelPredicates {
    private static final List<Integer> IN_HAND_ORDINALS = Stream.of(
            ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
            ModelTransformationMode.FIRST_PERSON_RIGHT_HAND,
            ModelTransformationMode.THIRD_PERSON_LEFT_HAND,
            ModelTransformationMode.THIRD_PERSON_RIGHT_HAND)
        .map(Enum::ordinal).toList();
    public static final Consumer<Item> STACK_COUNT = makePredicate("stack_count", (item, stack, livingEntity, world) -> stack.getCount() / (float) stack.getMaxCount());

    public static final Consumer<GunStagedItem> USAGE_TICKS = makePredicate("usage_stage", (gun, stack, livingEntity, world) -> {
        int maxUse = gun.getMaxStageTicks(stack);
        return maxUse == 0 ? 0.0f : gun.getStageTicks(stack) / (float) maxUse;
    });

    public static final Consumer<? extends Item> IN_GUI = item -> ModelPredicateProviderRegistry.register(item, new Identifier("in_gui"), (stack, world, entity, seed) -> entity != null && IN_HAND_ORDINALS.contains(seed - entity.getId()) ? 0 : 1);

    public static <T extends Item> Consumer<T> makePredicate(String name, Function4<T, ItemStack, LivingEntity, World, Float> predicate) {
        return item -> ModelPredicateProviderRegistry.register(item, new Identifier(name), (stack, world, entity, seed) -> entity == null ? 0.0f : predicate.apply(item, stack, entity, world));
    }

    public static <T extends Item> Consumer<T> booleanPredicate(String name, Function4<T, ItemStack, LivingEntity, World, Boolean> predicate) {
        return makePredicate(name, (item, stack, entity, world) -> predicate.apply(item, stack, entity, world) ? 1.0f : 0.0f);
    }

    public static <T extends StagedItem<?>> Consumer<T> stagePredicate(String predName, String... stageName) {
        return booleanPredicate(predName, (item, stack, entity, world) -> List.of(stageName).contains(item.getStageName(stack)));
    }

    public static void registerItemsWithPredicates(List<Item> items, List<Consumer<? extends Item>> predicates) {
        items.forEach(item -> registerItemWithPredicates(item, predicates));
    }

    public static void registerItemWithPredicates(Item item, List<Consumer<? extends Item>> predicates) {
        predicates.forEach(pred -> ((Consumer<Item>) pred).accept(item));
    }

    public static void registerAllModelPredicates() {
        // Grenades
//        registerItemWithPredicates(Z7Items.ITEM_FRAG_GRENADE, List.of(USAGE_STAGE, IS_ACTIVE, IS_PRIMED));
//        registerItemWithPredicates(Z7Items.ITEM_CONTACT_GRENADE, List.of(USAGE_STAGE, IS_ACTIVE, IS_PRIMED));
//        registerItemWithPredicates(Z7Items.ITEM_MOLOTOV_GRENADE, List.of(USAGE_STAGE, IS_PRIMED));
//        registerItemWithPredicates(Z7Items.ITEM_STICKY_GRENADE, List.of(USAGE_STAGE, IS_ACTIVE, IS_PRIMED));
//        registerItemWithPredicates(Z7Items.ITEM_BOWLING_BALL_GRENADE, List.of());

        // == Grenades
        registerItemsWithPredicates(
            List.of(
                Z7Items.ITEM_FRAG_GRENADE,
                Z7Items.ITEM_CONTACT_GRENADE,
                Z7Items.ITEM_STICKY_GRENADE),
            List.of(
                stagePredicate("is_priming", FragGrenadeItem.STAGE_PRIMING),
                stagePredicate("is_primed", FragGrenadeItem.STAGE_PRIMED),
                USAGE_TICKS));

        registerItemWithPredicates(
            Z7Items.ITEM_MOLOTOV_GRENADE,
            List.of(
                stagePredicate("is_lit", MolotovGrenadeItem.STAGE_LIT)));

        // == Guns
        // -- Pistols
        registerItemsWithPredicates(
            List.of(
                Z7Items.ITEM_PISTOL_FLINTLOCK,
                Z7Items.ITEM_PISTOL_BASIC),
            List.of(
                stagePredicate("is_readying", FlintlockPistolItem.STAGE_COCKING),
                stagePredicate("is_reloading", FlintlockPistolItem.STAGE_RELOADING),
                stagePredicate("is_ready_to_fire", FlintlockPistolItem.STAGE_COCKED),
                stagePredicate("is_firing", FlintlockPistolItem.STAGE_FIRING),
                stagePredicate("is_broken", FlintlockPistolItem.STAGE_BROKEN),
                USAGE_TICKS,
                IN_GUI));

        registerItemWithPredicates(
            Z7Items.ITEM_PISTOL_EOKA,
            List.of(
                stagePredicate("is_striking", EokaPistol.STAGE_STRIKING),
                stagePredicate("is_reloading", EokaPistol.STAGE_RELOADING),
                stagePredicate("is_firing", EokaPistol.STAGE_FIRING),
                stagePredicate("is_broken", EokaPistol.STAGE_BROKEN),
                USAGE_TICKS,
                IN_GUI));

        // -- Magnums
        registerItemsWithPredicates(
            List.of(
                Z7Items.ITEM_PISTOL_DEAGLE,
                Z7Items.ITEM_PISTOL_GLOCK),
            List.of(
                stagePredicate("is_reloading", DeaglePistolItem.STAGE_RELOADING),
                stagePredicate("is_ready_to_fire", DeaglePistolItem.STAGE_READY),
                stagePredicate("is_firing", DeaglePistolItem.STAGE_FIRING),
                stagePredicate("is_broken", DeaglePistolItem.STAGE_BROKEN),
                stagePredicate("is_readying", DeaglePistolItem.STAGE_READYING),
                USAGE_TICKS,
                IN_GUI));

        // -- Shotguns
        registerItemWithPredicates(
            Z7Items.ITEM_SHOTGUN_BASIC,
            List.of(
                stagePredicate("is_pumped", PumpShotgunItem.STAGE_PUMPED_DOWN, PumpShotgunItem.STAGE_PUMPING_UP),
                stagePredicate("is_reloading", PumpShotgunItem.STAGE_RELOADING),
                stagePredicate("is_ready_to_fire", PumpShotgunItem.STAGE_PUMPED),
                stagePredicate("is_firing", PumpShotgunItem.STAGE_FIRING),
                stagePredicate("is_broken", PumpShotgunItem.STAGE_BROKEN),
                USAGE_TICKS,
                IN_GUI));

        registerItemWithPredicates(
            Z7Items.ITEM_SHOTGUN_AA12,
            List.of(
                stagePredicate("is_reloading", Aa12ShotgunItem.STAGE_RELOADING),
                stagePredicate("is_ready_to_fire", Aa12ShotgunItem.STAGE_DEFAULT),
                stagePredicate("is_firing", Aa12ShotgunItem.STAGE_FIRING),
                stagePredicate("is_broken", Aa12ShotgunItem.STAGE_BROKEN),
                USAGE_TICKS,
                IN_GUI));

        registerItemWithPredicates(
            Z7Items.ITEM_SHOTGUN_DBLBRL,
            List.of(
                stagePredicate("is_reloading", dblbrlShotgunItem.STAGE_RELOADING),
                stagePredicate("is_ready_to_fire", dblbrlShotgunItem.STAGE_DEFAULT),
                stagePredicate("is_firing", dblbrlShotgunItem.STAGE_FIRING),
                stagePredicate("is_broken", dblbrlShotgunItem.STAGE_BROKEN),
                USAGE_TICKS,
                IN_GUI));

        // -- Rifles
        registerItemWithPredicates(
            Z7Items.ITEM_RIFLE_AK,
            List.of(
                stagePredicate("is_readying", AkRifleItem.STAGE_COCKING),
                stagePredicate("is_reloading", AkRifleItem.STAGE_RELOADING),
                stagePredicate("is_ready_to_fire", AkRifleItem.STAGE_COCKED),
                stagePredicate("is_firing", AkRifleItem.STAGE_FIRING),
                stagePredicate("is_broken", AkRifleItem.STAGE_BROKEN),
                USAGE_TICKS,
                IN_GUI));

        // == Misc
        registerItemWithPredicates(
            Z7Items.ITEM_FIRE_LANCE,
            List.of(
                stagePredicate("is_reloading", AkRifleItem.STAGE_RELOADING),
                stagePredicate("is_ready_to_fire", AkRifleItem.STAGE_COCKED),
                stagePredicate("is_firing", AkRifleItem.STAGE_FIRING),
                stagePredicate("is_broken", AkRifleItem.STAGE_BROKEN),
                USAGE_TICKS,
                IN_GUI));

        // -- "Numerous" items
        registerItemsWithPredicates(
            List.of(
                Z7Items.ITEM_AMMO_PISTOL,
                Z7Items.ITEM_AMMO_SHOTGUN,
                Z7Items.ITEM_AMMO_RUBBER_SHOTGUN,
                Z7Items.ITEM_AMMO_MAGNUM,
                Z7Items.ITEM_ANTIBIOTICS,
                Z7Items.ITEM_PAINKILLERS),
            List.of(STACK_COUNT));
    }
}
