package dev.foltz.item.grenade;

import dev.foltz.Z7Util;
import dev.foltz.entity.Z7Entities;
import dev.foltz.entity.Z7FragGrenadeEntity;
import dev.foltz.entity.Z7GrenadeEntity;
import dev.foltz.entity.Z7MolotovGrenadeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class Z7FragGrenadeItem extends Z7GrenadeItem {
    public static final int STAGE_PRIME_ON_RELEASE = 1;

    public Z7FragGrenadeItem() {
        super(16, Map.of(GLOBAL_STAGE_PRIMED, Z7Util.ticksFromSeconds(7f)));
    }

    @Override
    public List<? extends Z7GrenadeEntity> createGrenadeEntities(LivingEntity entity, ItemStack stack) {
        boolean isActive = getGrenadeStage(stack) == GLOBAL_STAGE_PRIMED;
        Z7FragGrenadeEntity grenade = new Z7FragGrenadeEntity(Z7Entities.FRAG_GRENADE_ENTITY, entity.world);

        if (isActive) {
            int fuseTime = Math.max(1, getMaxUsageTicks(stack) - getUsageStage(stack));
            grenade.setFuseTime(fuseTime);
        }
        else if (getGrenadeStage(stack) == STAGE_PRIME_ON_RELEASE) {
            grenade.setFuseTime(Z7Util.ticksFromSeconds(7f));
        }
        else {
            grenade.setFuseTime(-1);
        }

        return List.of(grenade);
    }

    @Override
    public boolean isShooting(ItemStack stack, LivingEntity entity) {
        if (!(stack.getItem() instanceof Z7IGrenadelike grenadelike)) {
            return false;
        }

        int grenadeStage = getGrenadeStage(stack);
        return grenadeStage == STAGE_PRIME_ON_RELEASE;
    }

    @Override
    public boolean isReadyToFire(ItemStack stack) {
        if (!(stack.getItem() instanceof Z7IGrenadelike grenadelike)) {
            return false;
        }

        return getGrenadeStage(stack) == STAGE_PRIME_ON_RELEASE;
    }

    @Override
    public ItemStack beginShoot(ItemStack stack, LivingEntity entity) {
        if (!(stack.getItem() instanceof Z7IGrenadelike grenadelike)) {
            return stack;
        }

        if (getGrenadeStage(stack) == GLOBAL_STAGE_DEFAULT) {
            System.out.println("Default -> Prime on release");
            setGrenadeStage(stack, STAGE_PRIME_ON_RELEASE);
            resetUsageStage(stack);
        }

        return stack;
    }

    @Override
    public ItemStack endShoot(ItemStack stack, LivingEntity entity) {
        if (!(stack.getItem() instanceof Z7IGrenadelike grenadelike)) {
            return stack;
        }

        if (getGrenadeStage(stack) == STAGE_PRIME_ON_RELEASE) {
            System.out.println("Prime on release -> Primed!");
            setGrenadeStage(stack, GLOBAL_STAGE_PRIMED);
            resetUsageStage(stack);
        }

        return stack;
    }

    @Override
    public void tickShootInventory(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (getGrenadeStage(stack) == GLOBAL_STAGE_PRIMED) {
            if (getUsageStage(stack) >= getMaxUsageTicks(stack)) {
                world.createExplosion(null, entity.getX(), entity.getY(), entity.getZ(), 2.0f, World.ExplosionSourceType.TNT);
                resetUsageStage(stack);
                setGrenadeStage(stack, GLOBAL_STAGE_DEFAULT);
            }
            else {
                updateUsageStage(stack);
            }
        }
        else if (!selected && getGrenadeStage(stack) == STAGE_PRIME_ON_RELEASE) {
            setGrenadeStage(stack, GLOBAL_STAGE_DEFAULT);
        }
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        int stage = getGrenadeStage(stack);

        if (stage == GLOBAL_STAGE_PRIMED) {
            return Math.round(13f * (1 - (float) getUsageStage(stack) / (float) getMaxUsageTicks(stack)));
        }

        return super.getItemBarStep(stack);
    }
}
