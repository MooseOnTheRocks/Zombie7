package dev.foltz.item.grenade;

import dev.foltz.Z7Util;
import dev.foltz.entity.Z7BowlingBallGrenadeEntity;
import dev.foltz.entity.Z7Entities;
import dev.foltz.entity.Z7GrenadeEntity;
import dev.foltz.entity.Z7MolotovGrenadeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class Z7BowlingBallGrenadeItem extends Z7GrenadeItem {
    public Z7BowlingBallGrenadeItem() {
        super(16, Map.of());
    }

    public List<? extends Z7GrenadeEntity> createGrenadeEntities(LivingEntity entity, ItemStack stack) {
        Z7BowlingBallGrenadeEntity grenade = new Z7BowlingBallGrenadeEntity(Z7Entities.BOWLING_BALL_GRENADE_ENTITY, entity.world);
        return List.of(grenade);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int usedTime = getMaxUseTime(stack) - remainingUseTicks;
        if (getGrenadeStage(stack) != GLOBAL_STAGE_PRIMED && usedTime < Z7Util.ticksFromSeconds(0.25f)) {
            return;
        }

        final int fullThrowTime = Z7Util.ticksFromSeconds(3f);
        float throwSpeed = MathHelper.map(Math.min(usedTime, fullThrowTime), 0, fullThrowTime, 0.1f, 1.0f);
        if (user.getStatusEffect(StatusEffects.STRENGTH) != null) {
            throwSpeed *= 1 + (0.5 * user.getStatusEffect(StatusEffects.STRENGTH).getAmplifier());
        }
        throwGrenade(stack, world, user, throwSpeed);
    }

    @Override
    public boolean canReload(ItemStack stack, LivingEntity entity) {
        return false;
    }

    @Override
    public ItemStack beginShoot(ItemStack stack, LivingEntity entity) {
        return stack;
    }

    @Override
    public ItemStack endShoot(ItemStack stack, LivingEntity entity) {
        return stack;
    }
}
