package dev.foltz.status;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class Z7HealingStatusEffect extends Z7StatusEffect {
    public static final int TICKS_PER_UPDATE = 15;
    public Z7HealingStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xFF0000);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % TICKS_PER_UPDATE == 0;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(0.5f);
        }
    }
}
