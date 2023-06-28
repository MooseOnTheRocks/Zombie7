package dev.foltz.item;

import dev.foltz.Z7Util;
import dev.foltz.Zombie7;
import dev.foltz.entity.Z7GrenadeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class Z7ShotgunItem extends Z7GunItem {
    public Z7ShotgunItem() {
        super(120, Z7Util.ticksFromSeconds(0.2f), Z7Util.ticksFromSeconds(3.5f), 4, Z7AmmoCategory.SHOTGUN_AMMO);
    }

    @Override
    public int getMaxUsageTicks(ItemStack stack) {
        int stage = getGunStage(stack);
        if (stage == 0 || stage == 1) {
            return Z7Util.ticksFromSeconds(0.9f);
        }
        else {
            return 0;
        }
    }

    @Override
    public boolean isReadyToFire(ItemStack stack) {
        return getGunStage(stack) == 2;
    }

    @Override
    public ItemStack beginShoot(ItemStack stack, World world, LivingEntity entity) {
        int gunStage = getGunStage(stack);

        if (gunStage == 2) {
            var newStack = setGunStage(stack, 0);
            if (shoot(newStack, world, entity)) {
                return resetLastFiredTime(world, newStack);
            }
            else {
                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.5f, 0.8f);
                return resetUsageTicks(setGunStage(stack, 0));
            }
        }

        return stack;
    }

    public ItemStack tickShoot(ItemStack stack, World world, LivingEntity entity) {
        int stage = getGunStage(stack);

        if (!hasAmmoInGun(stack)) {
            return resetUsageTicks(setGunStage(stack, 0));
        }
        else if (stage == 0) {
            if (getUsageTicks(stack) >= getMaxUsageTicks(stack) / 2f) {
                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.8f, 2.0f);
                return setGunStage(stack, 1);
            }
            else {
                return updateUsageTicks(stack);
            }
        }
        else if (stage == 1) {
            if (getUsageTicks(stack) >= getMaxUsageTicks(stack)) {
                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.8f, 2.0f);
                return resetUsageTicks(setGunStage(stack, 2));
            }
            else {
                return updateUsageTicks(stack);
            }
        }
        else {
            return stack;
        }
    }

    @Override
    public ItemStack endShoot(ItemStack stack, World world, LivingEntity entity) {
        int stage = getGunStage(stack);
        if (stage == 1) {
            return resetUsageTicks(setGunStage(stack, 0));
        }
        else {
            return resetUsageTicks(stack);
        }
    }
}
