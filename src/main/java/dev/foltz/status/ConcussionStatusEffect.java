package dev.foltz.status;

import dev.foltz.Z7Util;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;

public class ConcussionStatusEffect extends Z7StatusEffect {
    public static final int SHORT_DURATION = Z7Util.ticksFromSeconds(10);
    public static final int LONG_DURATION = Z7Util.ticksFromMinutes(12);
    public ConcussionStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0xDDDDDD);
    }

//    @Override
//    public void onRemoved(AttributeContainer attributes) {
//        super.onRemoved(attributes);
//        attributes.get
//        entity.addStatusEffect(new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_CONCUSSION_LONG, LONG_DURATION));
//    }


    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getStatusEffect(this).getDuration() == 1) {
            entity.addStatusEffect(new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_BLEEDING_LONG, LONG_DURATION));
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }
}
