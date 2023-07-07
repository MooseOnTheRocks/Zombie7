package dev.foltz.item.gun.behavior;

import dev.foltz.item.Z7IReloadable;
import dev.foltz.item.gun.Z7IGunlike;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface Z7IReloadBehavior extends Z7IReloadable {
    @Override
    default boolean isReloading(ItemStack stack) {
        if (!(stack.getItem() instanceof Z7IGunlike gunlike)) {
            return false;
        }

        return gunlike.getGunStage(stack) == Z7IGunlike.GLOBAL_STAGE_RELOADING;
    }

    @Override
    default void tickReloadInventory(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(stack.getItem() instanceof Z7IGunlike gunlike)) {
            return;
        }

        if (!selected) {
            gunlike.setGunStage(stack, Z7IGunlike.GLOBAL_STAGE_DEFAULT);
            gunlike.resetUsageStage(stack);
        }
    }

    void playSoundReloadBegin(ItemStack stack, LivingEntity entity);
    void playSoundReloadStep(ItemStack stack, LivingEntity entity);
    void playSoundReloadEnd(ItemStack stack, LivingEntity entity);
    void playSoundReloadCancel(ItemStack stack, LivingEntity entity);
}
