package dev.foltz.item.gun.behavior;

import dev.foltz.item.gun.Z7IGunlike;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.List;

public class Z7FullAutoShootBehavior implements Z7IShootBehavior {
    protected final int stageDefault;
    protected final List<Integer> preFireStages;
    protected final boolean retainPrefireStage;
    protected final int readyToFireOnReleaseStage;
    protected final int readyToFireStage;
    protected final int firingStage;

    public Z7FullAutoShootBehavior(List<Integer> preFireStages, boolean retainPrefireStage, int readyToFireOnReleaseStage, int readyToFireStage) {
        this.stageDefault = Z7IGunlike.GLOBAL_STAGE_DEFAULT;
        this.preFireStages = List.copyOf(preFireStages);
        this.retainPrefireStage = retainPrefireStage;
        this.readyToFireOnReleaseStage = readyToFireOnReleaseStage;
        this.readyToFireStage = readyToFireStage;
        this.firingStage = Z7IGunlike.GLOBAL_STAGE_FIRING;
    }

    @Override
    public void tickShootInventory(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(stack.getItem() instanceof Z7IGunlike gunlike)) {
            return;
        }

        if (!gunlike.isFiringCooldown(entity.world, stack) && gunlike.getGunStage(stack) == firingStage) {
            if (gunlike.hasAmmoInGun(stack)) {
                gunlike.setGunStage(stack, readyToFireStage);
            }
            else {
                gunlike.setGunStage(stack, stageDefault);
            }
            gunlike.resetUsageStage(stack);
        }
        else if (!selected) {
            if (gunlike.getGunStage(stack) == readyToFireStage || gunlike.getGunStage(stack) == readyToFireOnReleaseStage) {
                gunlike.setGunStage(stack, readyToFireStage);
            }
            else if (gunlike.getGunStage(stack) != stageDefault && !retainPrefireStage && preFireStages.contains(gunlike.getGunStage(stack))) {
                gunlike.setGunStage(stack, stageDefault);
            }
            gunlike.resetUsageStage(stack);
        }
    }

    @Override
    public boolean canShoot(ItemStack stack, LivingEntity entity) {
        if (!(stack.getItem() instanceof Z7IGunlike gunlike)) {
            return false;
        }

        int gunStage = gunlike.getGunStage(stack);
        return gunlike.hasAmmoInGun(stack)
            && (gunStage == stageDefault || preFireStages.contains(gunStage) || gunStage == readyToFireStage || gunStage == readyToFireOnReleaseStage);
    }

    @Override
    public boolean isShooting(ItemStack stack, LivingEntity entity) {
        if (!(stack.getItem() instanceof Z7IGunlike gunlike)) {
            return false;
        }

        int gunStage = gunlike.getGunStage(stack);
        return preFireStages.contains(gunStage) || gunStage == readyToFireOnReleaseStage || gunStage == firingStage;
    }

    @Override
    public boolean isReadyToFire(ItemStack stack) {
        if (!(stack.getItem() instanceof Z7IGunlike gunlike)) {
            return false;
        }

        int gunStage = gunlike.getGunStage(stack);
        return gunStage == readyToFireStage || gunStage == readyToFireOnReleaseStage;
    }

    @Override
    public boolean isInFiringCooldown(ItemStack stack) {
        if (!(stack.getItem() instanceof Z7IGunlike gunlike)) {
            return false;
        }

        return gunlike.getGunStage(stack) == firingStage;
    }

    @Override
    public ItemStack beginShoot(ItemStack stack, LivingEntity entity) {
        if (!(stack.getItem() instanceof Z7IGunlike gunlike)) {
            return stack;
        }


        int gunStage = gunlike.getGunStage(stack);
//        System.out.println("beginShoot, gunStage = " + gunStage);

        if (gunStage == stageDefault) {
            if (preFireStages.isEmpty()) {
                gunlike.setGunStage(stack, readyToFireOnReleaseStage);
                gunlike.resetUsageStage(stack);
            }
            else {
                gunlike.setGunStage(stack, preFireStages.get(0));
                gunlike.resetUsageStage(stack);
            }
        }
        else if (gunStage == readyToFireStage) {
            gunlike.shootGun(stack, entity.world, entity);
            gunlike.setGunStage(stack, firingStage);
            gunlike.resetUsageStage(stack);
            gunlike.resetLastFiredTime(stack, entity.world);
            playSoundFireBegin(stack, entity);
        }

        return stack;
    }

    @Override
    public ItemStack tickShoot(ItemStack stack, LivingEntity entity) {
        if (!(stack.getItem() instanceof Z7IGunlike gunlike)) {
            return stack;
        }

//        System.out.println("tickShoot");

        int gunStage = gunlike.getGunStage(stack);

        if (preFireStages.contains(gunStage)) {
            int usageStage = gunlike.getUsageStage(stack);

            if (usageStage >= gunlike.getMaxUsageTicks(stack)) {
                int index = preFireStages.indexOf(gunStage);
                if (index == preFireStages.size() - 1) {
                    gunlike.setGunStage(stack, readyToFireOnReleaseStage);
                    playSoundReadyToFire(stack, entity);
                }
                else {
                    gunlike.setGunStage(stack, preFireStages.get(index + 1));
                    playSoundReadyToFire(stack, entity);
                }
                gunlike.resetUsageStage(stack);
            }
            else {
//                if (usageStage == 0 && gunlike.getMaxUsageTicks(stack) > 0) {
//                    playSoundPreFireStep(stack, entity);
//                }
                gunlike.updateUsageStage(stack);
            }
        }

        return stack;
    }

    @Override
    public ItemStack endShoot(ItemStack stack, LivingEntity entity) {
        if (!(stack.getItem() instanceof Z7IGunlike gunlike)) {
            return stack;
        }

//        System.out.println("endShoot");

        int gunStage = gunlike.getGunStage(stack);

        if (preFireStages.contains(gunStage)) {
            if (retainPrefireStage) {
            }
            else {
                gunlike.setGunStage(stack, stageDefault);
            }
            gunlike.resetUsageStage(stack);
        }
        else if (gunStage == readyToFireOnReleaseStage) {
            gunlike.setGunStage(stack, readyToFireStage);
            gunlike.resetUsageStage(stack);
        }

        return stack;
    }

    @Override
    public void playSoundFireBegin(ItemStack stack, LivingEntity entity) {
        entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.3f, 2.0f);
    }

    @Override
    public void playSoundPreFireStep(ItemStack stack, LivingEntity entity) {
        entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.25f, 4.0f);
    }

    @Override
    public void playSoundReadyToFire(ItemStack stack, LivingEntity entity) {
        entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.25f, 8.0f);
    }
}
