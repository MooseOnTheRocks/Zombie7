package dev.foltz;

import com.mojang.datafixers.util.Function4;
import dev.foltz.item.StagedGunItem;
import dev.foltz.item.grenade.Z7GrenadeItem;
import dev.foltz.item.gun.Z7GunItem;
import dev.foltz.item.Z7Items;
import dev.foltz.item.gun.pistol.DeaglePistolItem;
import dev.foltz.item.gun.pistol.FlintlockPistolItem;
import dev.foltz.item.gun.rifle.AkRifleItem;
import dev.foltz.item.gun.shotgun.Aa12ShotgunItem;
import dev.foltz.item.gun.shotgun.PumpShotgunItem;
import dev.foltz.item.stage.StagedItem;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Z7ModelPredicates {
//    public static final Consumer<Z7GunItem> USAGE_STAGE = makePredicate("usage_stage", (gun, stack, livingEntity, world) -> {
//        int maxUse = gun.getMaxUsageTicks(stack);
//        return maxUse == 0 ? 0.0f : gun.getUsageStage(stack) / (float) maxUse;
//    });

    public static final Consumer<Item> STACK_COUNT = makePredicate("stack_count", (item, stack, livingEntity, world) -> stack.getCount() / (float) stack.getMaxCount());
    public static final Consumer<Z7GrenadeItem> IS_PRIMED = booleanPredicate("is_primed", (grenade, stack, livingEntity, world) -> grenade.isPrimed(stack));
    public static final Consumer<Z7GrenadeItem> IS_ACTIVE = booleanPredicate("is_active", (grenade, stack, livingEntity, world) -> grenade.isActive(stack));

    public static final Consumer<StagedGunItem> TEST_USAGE_TICKS = makePredicate("usage_stage", (gun, stack, livingEntity, world) -> {
        int maxUse = gun.getMaxStageTicks(stack);
        return maxUse == 0 ? 0.0f : gun.getStageTicks(stack) / (float) maxUse;
    });


    public static <T extends Item> Consumer<T> makePredicate(String name, Function4<T, ItemStack, LivingEntity, World, Float> predicate) {
        return item -> ModelPredicateProviderRegistry.register(item, new Identifier(name), (stack, world, entity, seed) -> entity == null ? 0.0f : predicate.apply(item, stack, entity, world));
    }

    public static <T extends Item> Consumer<T> booleanPredicate(String name, Function4<T, ItemStack, LivingEntity, World, Boolean> predicate) {
        return makePredicate(name, (item, stack, entity, world) -> predicate.apply(item, stack, entity, world) ? 1.0f : 0.0f);
    }

    public static <T extends StagedItem> Consumer<T> stagePredicate(String predName, String... stageName) {
        return booleanPredicate(predName, (item, stack, entity, world) -> Arrays.asList(stageName).contains(item.getStageName(stack)));
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

        // == Guns
        // -- Pistols
        registerItemsWithPredicates(
            List.of(Z7Items.ITEM_PISTOL_FLINTLOCK, Z7Items.ITEM_PISTOL_BASIC),
            List.of(
                stagePredicate("is_readying", FlintlockPistolItem.STAGE_COCKING),
                stagePredicate("is_reloading", FlintlockPistolItem.STAGE_RELOADING),
                stagePredicate("is_ready_to_fire", FlintlockPistolItem.STAGE_COCKED),
                stagePredicate("is_firing", FlintlockPistolItem.STAGE_FIRING),
                stagePredicate("is_broken", FlintlockPistolItem.STAGE_BROKEN),
                TEST_USAGE_TICKS));

        // -- Magnums
        registerItemWithPredicates(
            Z7Items.ITEM_PISTOL_DEAGLE,
            List.of(
                stagePredicate("is_reloading", DeaglePistolItem.STAGE_RELOADING),
                stagePredicate("is_ready_to_fire", DeaglePistolItem.STAGE_READY),
                stagePredicate("is_firing", DeaglePistolItem.STAGE_FIRING),
                stagePredicate("is_broken", DeaglePistolItem.STAGE_BROKEN),
                stagePredicate("is_readying", DeaglePistolItem.STAGE_READYING),
                TEST_USAGE_TICKS));

        // -- Shotguns
        registerItemWithPredicates(
            Z7Items.ITEM_SHOTGUN_BASIC,
            List.of(
                stagePredicate("is_pumped", PumpShotgunItem.STAGE_PUMPED_DOWN, PumpShotgunItem.STAGE_PUMPING_UP),
                stagePredicate("is_reloading", PumpShotgunItem.STAGE_RELOADING),
                stagePredicate("is_ready_to_fire", PumpShotgunItem.STAGE_PUMPED),
                stagePredicate("is_firing", PumpShotgunItem.STAGE_FIRING),
                stagePredicate("is_broken", PumpShotgunItem.STAGE_BROKEN),
                TEST_USAGE_TICKS));

        registerItemWithPredicates(
            Z7Items.ITEM_SHOTGUN_AA12,
            List.of(
                stagePredicate("is_reloading", Aa12ShotgunItem.STAGE_RELOADING),
                stagePredicate("is_ready_to_fire", Aa12ShotgunItem.STAGE_DEFAULT),
                stagePredicate("is_firing", Aa12ShotgunItem.STAGE_FIRING),
                stagePredicate("is_broken", Aa12ShotgunItem.STAGE_BROKEN),
                TEST_USAGE_TICKS));

        // -- Rifles
        registerItemWithPredicates(
                Z7Items.ITEM_RIFLE_AK,
                List.of(
                    stagePredicate("is_readying", AkRifleItem.STAGE_COCKING),
                    stagePredicate("is_reloading", AkRifleItem.STAGE_RELOADING),
                    stagePredicate("is_ready_to_fire", AkRifleItem.STAGE_COCKED),
                    stagePredicate("is_firing", AkRifleItem.STAGE_FIRING),
                    stagePredicate("is_broken", AkRifleItem.STAGE_BROKEN),
                    TEST_USAGE_TICKS));

        // "Numerous" items
        registerItemsWithPredicates(
            List.of(
                Z7Items.ITEM_AMMO_PISTOL,
                Z7Items.ITEM_AMMO_SHOTGUN,
                Z7Items.ITEM_AMMO_MAGNUM,
                Z7Items.ITEM_ANTIBIOTICS,
                Z7Items.ITEM_PAINKILLERS),
            List.of(STACK_COUNT));
    }
}
