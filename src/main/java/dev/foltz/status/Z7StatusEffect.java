package dev.foltz.status;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

public class Z7StatusEffect extends StatusEffect {
    public final boolean showParticles;

    public Z7StatusEffect(StatusEffectCategory category, int color) {
        this(category, color, false);
    }

    public Z7StatusEffect(StatusEffectCategory category, int color, boolean showParticles) {
        super(category, color);
        this.showParticles = showParticles;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    public StatusEffectInstance createEffectInstance(int duration) {
        return new StatusEffectInstance(this, duration, 0, false, false, true);
    }

    @Override
    public Z7StatusEffect addAttributeModifier(EntityAttribute attribute, String uuid, double amount, EntityAttributeModifier.Operation operation) {
        super.addAttributeModifier(attribute, uuid, amount, operation);
        return this;
    }
}
