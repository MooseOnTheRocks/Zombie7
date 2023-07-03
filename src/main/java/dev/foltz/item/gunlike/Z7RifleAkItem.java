package dev.foltz.item.gunlike;

import dev.foltz.Z7Util;
import dev.foltz.item.ammo.Z7AmmoItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class Z7RifleAkItem extends Z7GunItem {
    public Z7RifleAkItem() {
        super(1200, Z7Util.ticksFromSeconds(0.15f), Z7Util.ticksFromSeconds(2.5f), 30, Z7AmmoItem.AmmoCategory.PISTOL_AMMO);
    }

    @Override
    public boolean isReadyToFire(ItemStack stack) {
        return getGunStage(stack) == 1 || getGunStage(stack) == 2;
    }

    @Override
    public int getMaxUsageTicks(ItemStack stack) {
        return getGunStage(stack) == 0
                ? Z7Util.ticksFromSeconds(0.75f)
                : 0;
    }

    @Override
    public ItemStack beginShoot(ItemStack stack, World world, LivingEntity entity) {
        if (getGunStage(stack) == 0) {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.8f, 2.0f);
            return resetUsageTicks(stack);
        }
        if (getGunStage(stack) == 1) {
            return resetUsageTicks(setGunStage(stack, 2));
        }
        return resetUsageTicks(stack);
    }

    public ItemStack tickShoot(ItemStack stack, World world, LivingEntity entity) {
        // Failing to use gun.
        if (!hasAmmoInGun(stack)) {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.PLAYERS, 0.5f, 1.0f);
            return setGunStage(stack, 0);
        }
        else if (getGunStage(stack) == 0) {
            if (getUsageTicks(stack) >= getMaxUsageTicks(stack)) {
                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.6f, 1.0f);
                return resetUsageTicks(setGunStage(stack, 1));
            }
            else {
                return updateUsageTicks(stack);
            }
        }
        else if (getGunStage(stack) == 2) {
            if (isFiring(world, stack)) {
                return stack;
            }
            else {
                if (shoot(stack, world, entity)) {
                    return resetLastFiredTime(world, stack);
                }
                else {
                    world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.5f, 0.8f);
                    return setGunStage(stack, 0);
                }
            }
        }

        return stack;
    }

    @Override
    public ItemStack endShoot(ItemStack stack, World world, LivingEntity entity) {
        if (getGunStage(stack) == 2) {
            return resetUsageTicks(setGunStage(stack, 1));
        }
        return resetUsageTicks(stack);
    }

    @Override
    public int getInitialReloadStage(ItemStack stack) {
        return 0;
    }

    @Override
    public ItemStack beginReload(ItemStack stack, World world, LivingEntity entity) {
        if (!(entity instanceof PlayerEntity player)) {
            return stack;
        }

        if (getGunStage(stack) == 1) {
            return resetUsageTicks(setGunStage(stack, 0));
        }

        if (getGunStage(stack) == 0) {
            setGunStage(stack, 0);
        }
        else {
            return stack;
        }

        setGunStage(stack, 0);

        if (!canReload(stack, player)) {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.PLAYERS, 0.8f, 2.0f);
            return resetUsageTicks(setGunStage(stack, 0));
        }

        tryReloading(player, stack);
        return stack;
    }

    @Override
    public ItemStack tickReload(ItemStack stack, World world, LivingEntity entity) {
        if (!(entity instanceof PlayerEntity player) || getGunStage(stack) != 0) {
            return stack;
        }

        if (!canReload(stack, player)) {
            return resetUsageTicks(stack);
        }

        int reloadStage = getReloadStage(stack);
//        boolean isNewReloadStage = ((ammoCapacity * reloadStage) / reloadingTicks) != ((ammoCapacity * (reloadStage - 1)) / reloadingTicks);
        boolean isDoneReloading = reloadStage >= reloadingTicks;

        if (!isDoneReloading) {
            return updateReloadTicks(stack);
        }

        for (int loaded = 0; loaded < ammoCapacity; loaded++) {
            int ammoSlot = findAmmoSlot(player, ammoCategory);
            if (ammoSlot < 0) {
                break;
            }
            ItemStack ammoStack = player.getInventory().getStack(ammoSlot);
            stack = loadOneBullet(stack, ammoStack, !player.getAbilities().creativeMode);
        }

        // Gun fully loaded.
        if (getAmmoInGun(stack).size() >= ammoCapacity) {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.PLAYERS, 0.8f, 2.0f);
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 0.2f, 3f);
//            return resetUsageTicks(stack);
        }
        // Gun not fully loaded, but no more ammo available.
        else if (!hasAmmoInInventory(player)) {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.PLAYERS, 1.0f, 2.0f);
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.PLAYERS, 0.8f, 1.0f);
//            return resetUsageTicks(stack);
        }

        return resetUsageTicks(stack);
    }

    @Override
    public ItemStack endReload(ItemStack stack, World world, LivingEntity entity) {
        return super.endReload(stack, world, entity);
    }
}
