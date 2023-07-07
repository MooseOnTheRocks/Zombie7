package dev.foltz.item.gun.behavior;

import dev.foltz.item.gun.Z7IGunlike;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class Z7WholeClipReloadBehavior implements Z7IReloadBehavior {
    protected final int stageOnSuccess;
    protected final int stageDefault;
    protected final int stageReloading;
    protected final List<Integer> stagesCancelable;
    protected final List<Integer> stagesOverridable;

    public Z7WholeClipReloadBehavior(int stageSuccess, List<Integer> stagesCancelable, List<Integer> stagesOverridable) {
        this.stageOnSuccess = stageSuccess;
        this.stageDefault = Z7IGunlike.GLOBAL_STAGE_DEFAULT;
        this.stageReloading = Z7IGunlike.GLOBAL_STAGE_RELOADING;
        this.stagesCancelable = List.copyOf(stagesCancelable);
        this.stagesOverridable = List.copyOf(stagesOverridable);
    }

    @Override
    public boolean canReload(ItemStack stack, LivingEntity entity) {
        if (!(stack.getItem() instanceof Z7IGunlike gunlike)) {
            return false;
        }

        boolean hasAmmoInInventory = entity instanceof PlayerEntity player && gunlike.hasAmmoInInventory(player);
        boolean hasRoomInGun = gunlike.getAmmoInGun(stack).size() < gunlike.getAmmoCapacity();
        int gunStage = gunlike.getGunStage(stack);

        return (gunStage == stageDefault && hasRoomInGun && hasAmmoInInventory)
            || stagesCancelable.contains(gunStage) || stagesOverridable.contains(gunStage);
    }

    @Override
    public ItemStack beginReload(ItemStack stack, LivingEntity entity) {
        if (!(stack.getItem() instanceof Z7IGunlike gunlike)) {
            return stack;
        }

        int gunStage = gunlike.getGunStage(stack);

        if ((gunStage == stageDefault || stagesOverridable.contains(gunStage)) && gunlike.getAmmoInGun(stack).size() < gunlike.getAmmoCapacity()) {
            gunlike.setGunStage(stack, stageReloading);
            gunlike.setUsageStage(stack, 0);
            playSoundReloadBegin(stack, entity);
        }
        else if (stagesCancelable.contains(gunStage)) {
            gunlike.setGunStage(stack, stageDefault);
            gunlike.resetUsageStage(stack);
            playSoundReloadCancel(stack, entity);
        }

        return stack;
    }

    @Override
    public ItemStack tickReload(ItemStack stack, LivingEntity entity) {
        if (!(stack.getItem() instanceof Z7IGunlike gunlike)) {
            return stack;
        }

        int usageStage = gunlike.getUsageStage(stack);


        if (usageStage >= gunlike.getMaxUsageTicks(stack)) {
            if (entity instanceof PlayerEntity player) {
                for (int i = gunlike.getAmmoInGun(stack).size(); i < gunlike.getAmmoCapacity(); i++) {
                    int slot = gunlike.getAmmoCategory().findAmmoSlot(player);
                    if (slot == -1) {
                        break;
                    }
                    ItemStack ammoStack = player.getInventory().getStack(slot);
                    gunlike.loadOneBullet(stack, ammoStack, !player.getAbilities().creativeMode);
                }
            }
            else {
                for (int i = gunlike.getAmmoInGun(stack).size(); i < gunlike.getAmmoCapacity(); i++) {
                    gunlike.loadOneBullet(stack, gunlike.getAmmoCategory().defaultItem.get().getDefaultStack(), false);
                }
            }
            gunlike.setGunStage(stack, stageOnSuccess);
            gunlike.resetUsageStage(stack);
            playSoundReloadEnd(stack, entity);
//            endReload(stack, entity);
        }
        else {
            gunlike.updateUsageStage(stack);
        }

        return stack;
    }

    @Override
    public ItemStack endReload(ItemStack stack, LivingEntity entity) {
        if (!(stack.getItem() instanceof Z7IGunlike gunlike)) {
            return stack;
        }

        if (gunlike.getGunStage(stack) == stageReloading) {
            if (gunlike.hasAmmoInGun(stack)) {
                gunlike.setGunStage(stack, stageOnSuccess);
            }
            else {
                gunlike.setGunStage(stack, stageDefault);
            }
            gunlike.resetUsageStage(stack);
        }

        return stack;
    }

    @Override
    public void playSoundReloadBegin(ItemStack stack, LivingEntity entity) {
        entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 0.5f, 3.0f);
    }

    @Override
    public void playSoundReloadStep(ItemStack stack, LivingEntity entity) {
        entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.5f, 1.0f);
    }

    @Override
    public void playSoundReloadEnd(ItemStack stack, LivingEntity entity) {
        entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.5f, 1.0f);
        entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 0.5f, 1.0f);
    }

    @Override
    public void playSoundReloadCancel(ItemStack stack, LivingEntity entity) {
        entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_CROSSBOW_LOADING_END, SoundCategory.PLAYERS, 0.5f, 3.0f);
    }
}
