package dev.foltz.item.gun.behavior;

import dev.foltz.item.Z7IShootable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface Z7IShootBehavior extends Z7IShootable {
    void playSoundFireBegin(ItemStack stack, LivingEntity entity);
    void playSoundPreFireStep(ItemStack stack, LivingEntity entity);
    void playSoundReadyToFire(ItemStack stack, LivingEntity entity);
}

