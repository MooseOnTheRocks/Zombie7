package dev.foltz;

import com.mojang.datafixers.util.Function4;
import dev.foltz.item.grenade.Z7GrenadeItem;
import dev.foltz.item.gun.Z7GunItem;
import dev.foltz.item.Z7Items;
import dev.foltz.item.gun.shotgun.Z7PumpShotgunItem;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

public class Z7ModelPredicates {
    public static final Consumer<Z7GunItem> USAGE_STAGE = makePredicate("usage_stage", (gun, stack, livingEntity, world) -> {
        int maxUse = gun.getMaxUsageTicks(stack);
        return maxUse == 0 ? 0.0f : gun.getUsageStage(stack) / (float) maxUse;
    });

    public static final Consumer<Item> STACK_COUNT = makePredicate("stack_count", (item, stack, livingEntity, world) -> stack.getCount() / (float) stack.getMaxCount());
    public static final Consumer<Z7GrenadeItem> IS_PRIMED = makeBooleanPredicate("is_primed", (grenade, stack, livingEntity, world) -> grenade.isPrimed(stack));
    public static final Consumer<Z7GrenadeItem> IS_ACTIVE = makeBooleanPredicate("is_active", (grenade, stack, livingEntity, world) -> grenade.isActive(stack));
    public static final Consumer<Z7GunItem> IS_READY_TO_FIRE = makeBooleanPredicate("is_ready_to_fire", (gun, stack, livingEntity, world) -> gun.isReadyToFire(stack));
    public static final Consumer<Z7GunItem> IS_FIRING = makeBooleanPredicate("is_firing", (gun, stack, livingEntity, world) -> gun.isFiringCooldown(world, stack));
    public static final Consumer<Z7GunItem> IS_RELOADING = makeBooleanPredicate("is_reloading", (gun, stack, livingEntity, world) -> gun.isReloading(stack));
    public static final Consumer<Z7GunItem> IS_BROKEN = makeBooleanPredicate("is_broken", (gun, stack, livingEntity, world) -> gun.isBroken(stack));
    public static final Consumer<Z7GunItem> FIRING_STAGE = makePredicate("firing_stage", (gun, stack, livingEntity, world) -> gun.getTotalFiringTicks() == 0 ? 0 : gun.getTotalFiringTicks(world, stack) / (float) gun.getTotalFiringTicks());
    public static final Consumer<Z7GunItem> RELOAD_STAGE = makePredicate("reload_stage", (gun, stack, livingEntity, world) -> gun.isReloading(stack) && gun.getMaxUsageTicks(stack) != 0 ? gun.getUsageStage(stack) / (float) gun.getMaxUsageTicks(stack) : 0);

    public static final Consumer<Z7GunItem> IS_PUMPING_DOWN = makeBooleanPredicate("is_pumping_down", (gun, stack, livingEntity, world) -> gun instanceof Z7PumpShotgunItem shotgun && shotgun.isPumpingDown(stack));


    public static <T extends Item> Consumer<T> makePredicate(String name, Function4<T, ItemStack, LivingEntity, World, Float> predicate) {
        return item -> ModelPredicateProviderRegistry.register(item, new Identifier(name), (stack, world, entity, seed) -> entity == null ? 0.0f : predicate.apply(item, stack, entity, world));
    }

    public static <T extends Item> Consumer<T> makeBooleanPredicate(String name, Function4<T, ItemStack, LivingEntity, World, Boolean> predicate) {
        return item -> ModelPredicateProviderRegistry.register(item, new Identifier(name), (stack, world, entity, seed) -> entity != null && predicate.apply(item, stack, entity, world) ? 1.0f : 0.0f);
    }

    public static void registerItemsWithPredicates(List<Item> items, List<Consumer<? extends Item>> predicates) {
        items.forEach(item -> registerItemWithPredicates(item, predicates));
    }

    public static void registerItemWithPredicates(Item item, List<Consumer<? extends Item>> predicates) {
        predicates.forEach(pred -> ((Consumer<Item>) pred).accept(item));
    }

    public static void registerAllModelPredicates() {
        // Grenades
        registerItemWithPredicates(Z7Items.ITEM_FRAG_GRENADE, List.of(USAGE_STAGE, IS_ACTIVE, IS_PRIMED));
        registerItemWithPredicates(Z7Items.ITEM_MOLOTOV_GRENADE, List.of(USAGE_STAGE, IS_PRIMED));
        registerItemWithPredicates(Z7Items.ITEM_STICKY_GRENADE, List.of(USAGE_STAGE, IS_ACTIVE, IS_PRIMED));
        registerItemWithPredicates(Z7Items.ITEM_BOWLING_BALL_GRENADE, List.of());

        // Guns
        registerItemsWithPredicates(
            List.of(Z7Items.ITEM_PISTOL_BASIC, Z7Items.ITEM_PISTOL_FLINTLOCK),
            List.of(USAGE_STAGE, IS_RELOADING, RELOAD_STAGE, IS_READY_TO_FIRE, IS_FIRING, FIRING_STAGE, IS_BROKEN));

        registerItemWithPredicates(
            Z7Items.ITEM_RIFLE_AK,
            List.of(USAGE_STAGE, IS_RELOADING, RELOAD_STAGE, IS_READY_TO_FIRE, IS_FIRING, FIRING_STAGE, IS_BROKEN));

        registerItemWithPredicates(
            Z7Items.ITEM_SHOTGUN_BASIC,
            List.of(IS_PUMPING_DOWN, USAGE_STAGE, IS_RELOADING, RELOAD_STAGE, IS_READY_TO_FIRE, IS_FIRING, FIRING_STAGE, IS_BROKEN));

        registerItemWithPredicates(
            Z7Items.ITEM_SHOTGUN_AA12,
            List.of(USAGE_STAGE, IS_RELOADING, RELOAD_STAGE, IS_READY_TO_FIRE, IS_FIRING, FIRING_STAGE, IS_BROKEN));


        // "Numerous" items
        registerItemsWithPredicates(
            List.of(
                Z7Items.ITEM_AMMO_PISTOL,
                Z7Items.ITEM_AMMO_SHOTGUN,
                Z7Items.ITEM_ANTIBIOTICS,
                Z7Items.ITEM_PAINKILLERS),
            List.of(STACK_COUNT));
    }
}
