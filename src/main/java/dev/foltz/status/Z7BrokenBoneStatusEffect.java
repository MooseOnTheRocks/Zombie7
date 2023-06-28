package dev.foltz.status;

import dev.foltz.Zombie7;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;

public class Z7BrokenBoneStatusEffect extends Z7StatusEffect {
    public static final int TICKS_PER_UPDATE = 30;
    public Z7BrokenBoneStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0x660000);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % TICKS_PER_UPDATE == 0;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
//        entity.damage(entity.getDamageSources().create(Zombie7.BLEEDING_DAMAGE_TYPE), 0.5f);
    }
}
