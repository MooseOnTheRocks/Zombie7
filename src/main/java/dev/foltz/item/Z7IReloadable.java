package dev.foltz.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface Z7IReloadable {
    boolean canReload(ItemStack stack, LivingEntity entity);
    boolean isReloading(ItemStack stack);
    ItemStack beginReload(ItemStack stack, LivingEntity entity);
    ItemStack tickReload(ItemStack stack, LivingEntity entity);
    ItemStack endReload(ItemStack stack, LivingEntity entity);

    default void tickReloadInventory(ItemStack stack, World world, Entity entity, int slot, boolean selected) {}

    default boolean tryReloading(ItemStack stack, LivingEntity entity) {
        if (isReloading(stack)) {
            return true;
        }
        else {
//            System.out.println("tryReloading isReloading is false!");
            if(canReload(stack, entity)) {
                beginReload(stack, entity);
            }
        }

        return isReloading(stack);
    }
}
