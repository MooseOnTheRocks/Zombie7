package dev.foltz.status;

import dev.foltz.Z7Util;
import dev.foltz.Zombie7;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;

public class Z7ConcussionStatusEffect extends Z7StatusEffect {
    public static final int SHORT_DURATION = Z7Util.ticksFromSeconds(10);
    public static final int LONG_DURATION = Z7Util.ticksFromMinutes(12);
    public Z7ConcussionStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0xDDDDDD);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);
        entity.addStatusEffect(new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_CONCUSSION_LONG, LONG_DURATION));
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
//        return duration % 30 == 0;
        return false;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
//        entity.damage(entity.getDamageSources().create(Zombie7.BLEEDING_DAMAGE_TYPE), 0.5f);
    }
}
