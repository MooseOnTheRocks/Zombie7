package dev.foltz;

import dev.foltz.item.Z7GunItem;
import dev.foltz.item.Z7Items;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class Z7ModelPredicates {

    public static void registerIsBrokenPredicate(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("is_broken"), (stack, world, entity, seed) -> {
            if (entity == null || !(stack.getItem() instanceof Z7GunItem gunItem)) {
                return 0.0f;
            }

            return gunItem.isBroken(stack) ? 1.0f : 0.0f;
        });
    }

    public static void registerIsReloadingPredicate(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("is_reloading"), (stack, world, entity, seed) -> {
            if (entity == null || !(stack.getItem() instanceof Z7GunItem gunItem)) {
                return 0.0f;
            }

            return gunItem.isReloading(stack) ? 1.0f : 0.0f;
        });
    }

    public static void registerReloadStagePredicate(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("reload_stage"), (stack, world, entity, seed) -> {
            if (entity == null || !(stack.getItem() instanceof Z7GunItem gunItem)) {
                return 0.0f;
            }

            return (float) gunItem.getReloadStage(stack) / (float) gunItem.reloadingTicks;
        });
    }

    public static void registerFiringStagePredicate(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("firing_stage"), (stack, world, entity, seed) -> {
            if (entity == null || !(stack.getItem() instanceof Z7GunItem gunItem)) {
                return 0.0f;
            }

            return (float) gunItem.getFiringTicks(world, stack) / (float) gunItem.firingTicks;
        });
    }

    public static void registerIsFiringPredicate(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("is_firing"), (stack, world, entity, seed) -> {
            if (entity == null || !(stack.getItem() instanceof Z7GunItem gunItem)) {
                return 0.0f;
            }

            return gunItem.isFiring(world, stack) ? 1.0f : 0.0f;
        });
    }

    public static void registerIsReadyToFirePredicate(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("is_ready_to_fire"), (stack, world, entity, seed) -> {
            if (entity == null || !(stack.getItem() instanceof Z7GunItem gunItem)) {
                return 0.0f;
            }

            return gunItem.isReadyToFire(stack) ? 1.0f : 0.0f;
        });
    }

    public static void registerUsageStagePredicate(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("usage_stage"), (stack, world, entity, seed) -> {
            if (entity == null || !(stack.getItem() instanceof Z7GunItem gunItem)) {
                return 0.0f;
            }

            int timeTotal = gunItem.getMaxUsageTicks(stack);
            if (timeTotal == 0) {
                return 0.0f;
            }
            int timeUsed =  gunItem.getUsageTicks(stack);
            return (float) timeUsed / (float) timeTotal;
        });
    }

    public static void registerStackCountPredicate(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("stack_count"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0f;
            }

            return (float) stack.getCount() / (float) stack.getMaxCount();
        });
    }

    public static void registerAllModelPredicates() {
        // Pistol
        registerUsageStagePredicate(Z7Items.ITEM_PISTOL_BASIC);
        registerIsReloadingPredicate(Z7Items.ITEM_PISTOL_BASIC);
        registerReloadStagePredicate(Z7Items.ITEM_PISTOL_BASIC);
        registerIsReadyToFirePredicate(Z7Items.ITEM_PISTOL_BASIC);
        registerIsFiringPredicate(Z7Items.ITEM_PISTOL_BASIC);
        registerFiringStagePredicate(Z7Items.ITEM_PISTOL_BASIC);
        registerIsBrokenPredicate(Z7Items.ITEM_PISTOL_BASIC);


        // Pistol Flintlock
        registerUsageStagePredicate(Z7Items.ITEM_PISTOL_FLINTLOCK);
        registerIsReloadingPredicate(Z7Items.ITEM_PISTOL_FLINTLOCK);
        registerReloadStagePredicate(Z7Items.ITEM_PISTOL_FLINTLOCK);
        registerIsReadyToFirePredicate(Z7Items.ITEM_PISTOL_FLINTLOCK);
        registerIsFiringPredicate(Z7Items.ITEM_PISTOL_FLINTLOCK);
        registerFiringStagePredicate(Z7Items.ITEM_PISTOL_FLINTLOCK);
        registerIsBrokenPredicate(Z7Items.ITEM_PISTOL_FLINTLOCK);


        // Shotgun
        registerUsageStagePredicate(Z7Items.ITEM_SHOTGUN_BASIC);
        registerIsReloadingPredicate(Z7Items.ITEM_SHOTGUN_BASIC);
        registerReloadStagePredicate(Z7Items.ITEM_SHOTGUN_BASIC);
        registerIsReadyToFirePredicate(Z7Items.ITEM_SHOTGUN_BASIC);
        registerIsFiringPredicate(Z7Items.ITEM_SHOTGUN_BASIC);
        registerFiringStagePredicate(Z7Items.ITEM_SHOTGUN_BASIC);
        registerIsBrokenPredicate(Z7Items.ITEM_SHOTGUN_BASIC);


        // "Numerous" items
        registerStackCountPredicate(Z7Items.ITEM_AMMO_PISTOL);
        registerStackCountPredicate(Z7Items.ITEM_AMMO_SHOTGUN);
        registerStackCountPredicate(Z7Items.ITEM_ANTIBIOTICS);
    }
}
