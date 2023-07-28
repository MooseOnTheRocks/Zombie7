package dev.foltz.status;

import dev.foltz.Z7Util;
import dev.foltz.Zombie7;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;

public class BleedingStatusEffect extends Z7StatusEffect {
    public static final int TICKS_PER_UPDATE = Z7Util.ticksFromSeconds(1.5f);
    public static final int LONG_DURATION = Z7Util.ticksFromMinutes(10);
    public static final int SHORT_DURATION = Z7Util.ticksFromSeconds(15);
    public BleedingStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0x660000);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % TICKS_PER_UPDATE == 0;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.damage(entity.getDamageSources().create(Zombie7.BLEEDING_DAMAGE_TYPE), 0.33f);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.STATUS_EFFECT_BLEEDING_LONG, LONG_DURATION));
    }
}
