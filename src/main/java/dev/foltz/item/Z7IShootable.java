package dev.foltz.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface Z7IShootable {

    boolean canShoot(ItemStack stack, LivingEntity entity);
    boolean isShooting(ItemStack stack, LivingEntity entity);
    boolean isReadyToFire(ItemStack stack);
    boolean isInFiringCooldown(ItemStack stack);
    ItemStack beginShoot(ItemStack stack, LivingEntity entity);
    ItemStack tickShoot(ItemStack stack, LivingEntity entity);
    ItemStack endShoot(ItemStack stack, LivingEntity entity);
    default void tickShootInventory(ItemStack stack, World world, Entity entity, int slot, boolean selected) {}

    default boolean tryShooting(ItemStack stack, LivingEntity entity) {
        if (isShooting(stack, entity)) {
            return true;
        }
        else {
//            System.out.println("tryShooting isShooting is false!");
            if (canShoot(stack, entity)) {
                beginShoot(stack, entity);
            }
        }

        return isShooting(stack, entity);
    }
}
