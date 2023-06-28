package dev.foltz.item;

import dev.foltz.Z7Util;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class Z7PistolItem extends Z7GunItem {
    public Z7PistolItem() {
        super(60, Z7Util.ticksFromSeconds(0.2f), Z7Util.ticksFromSeconds(2.5f), 4, Z7AmmoCategory.PISTOL_AMMO);
    }

    @Override
    public int getMaxUsageTicks(ItemStack stack) {
        return getGunStage(stack) == 0
                ? Z7Util.ticksFromSeconds(0.5f)
                : 0;
    }

    @Override
    public boolean isReadyToFire(ItemStack stack) {
        return getGunStage(stack) == 1;
    }

    @Override
    public ItemStack beginShoot(ItemStack stack, World world, LivingEntity entity) {
        int gunStage = getGunStage(stack);

        if (gunStage == 1) {
            var newStack = setGunStage(stack, 0);
            if (shoot(newStack, world, entity)) {
                return resetLastFiredTime(world, newStack);
            }
            else {
                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.5f, 0.8f);
                return resetUsageTicks(setGunStage(stack, 0));
            }
        }

        return resetUsageTicks(stack);
    }

    public ItemStack tickShoot(ItemStack stack, World world, LivingEntity entity) {
        // Failing to use gun.
        if (!hasAmmoInGun(stack)) {
            return resetUsageTicks(setGunStage(stack, 0));
        }
        else if (getGunStage(stack) == 0) {
            if (getUsageTicks(stack) >= getMaxUsageTicks(stack)) {
                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.8f, 2.0f);
                return resetUsageTicks(setGunStage(stack, 1));
            }
            else {
                return updateUsageTicks(stack);
            }
        }

        return stack;
    }

    @Override
    public ItemStack endShoot(ItemStack stack, World world, LivingEntity entity) {
        return resetUsageTicks(stack);
    }
}
