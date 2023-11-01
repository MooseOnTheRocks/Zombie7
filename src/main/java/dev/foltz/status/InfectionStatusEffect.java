package dev.foltz.status;

import dev.foltz.Z7Util;
import dev.foltz.Zombie7;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;

public class InfectionStatusEffect extends Z7StatusEffect {
    public static final int MAX_TICKS = Z7Util.ticksFromMinutes(60);

    public InfectionStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0x33BB22);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient) {
            entity.damage(entity.getDamageSources().create(Zombie7.INFECTION_DAMAGE_TYPE), Float.MAX_VALUE);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration <= 1;
    }
}
