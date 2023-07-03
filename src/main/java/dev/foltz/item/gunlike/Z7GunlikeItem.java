package dev.foltz.item.gunlike;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface Z7GunlikeItem {
    ItemStack beginShoot(ItemStack stack, World world, LivingEntity entity);
    ItemStack tickShoot(ItemStack stack, World world, LivingEntity entity);
    ItemStack endShoot(ItemStack stack, World world, LivingEntity entity);

    boolean shoot(ItemStack itemStack, World world, LivingEntity entity);

    default float getModifiedBulletDamage(ItemStack gunStack, ItemStack bulletStack, float damage) {
        return damage;
    }

    default float getModifiedBulletSpeed(ItemStack gunStack, ItemStack bulletStack, float speed) {
        return speed;
    }

    default float getModifiedBulletBaseRange(ItemStack gunStack, ItemStack bulletStack, float range) {
        return range;
    }

    ItemStack beginReload(ItemStack stack, World world, LivingEntity entity);
    ItemStack tickReload(ItemStack stack, World world, LivingEntity entity);
    ItemStack endReload(ItemStack stack, World world, LivingEntity entity);

    boolean isReloading(ItemStack stack);

    boolean hasAmmoInGun(ItemStack stack);

    boolean canReload(ItemStack stack, PlayerEntity player);


    boolean isBroken(ItemStack stack);
}
