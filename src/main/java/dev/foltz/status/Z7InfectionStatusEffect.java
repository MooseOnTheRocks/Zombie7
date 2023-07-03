package dev.foltz.status;

import dev.foltz.Z7Util;
import dev.foltz.Zombie7;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class Z7InfectionStatusEffect extends Z7StatusEffect {
    public static final int MAX_TICKS = Z7Util.ticksFromMinutes(60);

    public Z7InfectionStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0x33BB22);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.world.isClient) {
            entity.damage(entity.getDamageSources().create(Zombie7.INFECTION_DAMAGE_TYPE), Float.MAX_VALUE);
        }
    }

//    @Override
//    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
//    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration == 1;
    }
}
