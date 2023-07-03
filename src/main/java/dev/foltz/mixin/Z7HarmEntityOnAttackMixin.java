package dev.foltz.mixin;


import dev.foltz.Z7Util;
import dev.foltz.status.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Note: Maybe this should be on LivingEntity.applyDamage(DamageSource source, float amount)
@Mixin(DamageTracker.class)
public abstract class Z7HarmEntityOnAttackMixin {
    @Shadow @Final private LivingEntity entity;

    @Inject(method="onDamage", at=@At("TAIL"))
    protected void chanceInfect(DamageSource damageSource, float originalHealth, float damage, CallbackInfo ci) {
        var victim = this.entity;
        var attacker = damageSource.getAttacker();

        if (damageSource.isOf(DamageTypes.FALL)) {
            if (damage > 3) {
                var effect = victim.getStatusEffect(Z7StatusEffects.STATUS_EFFECT_BROKEN_BONE);
                if (effect != null) {
                    victim.removeStatusEffect(Z7StatusEffects.STATUS_EFFECT_BROKEN_BONE);
                }
                effect = victim.getStatusEffect(Z7StatusEffects.STATUS_EFFECT_TWISTED_ANKLE);
                if (effect != null) {
                    victim.removeStatusEffect(Z7StatusEffects.STATUS_EFFECT_TWISTED_ANKLE);
                }
                int hurtTime = (int) MathHelper.lerp(Math.min(damage, 20) / 20f, Z7Util.ticksFromMinutes(5), Z7Util.ticksFromMinutes(30));
                System.out.println("Damage = " + damage + ", hurtTime = " + hurtTime);
                victim.addStatusEffect(new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_BROKEN_BONE, hurtTime));
            }
            else {
                var effect = victim.getStatusEffect(Z7StatusEffects.STATUS_EFFECT_BROKEN_BONE);
                if (effect == null) {
                    effect = victim.getStatusEffect(Z7StatusEffects.STATUS_EFFECT_TWISTED_ANKLE);
                    if (effect != null) {
                        victim.removeStatusEffect(Z7StatusEffects.STATUS_EFFECT_TWISTED_ANKLE);
                    }
                    int hurtTime = Z7Util.ticksFromMinutes(2.5f);
                    victim.addStatusEffect(new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_TWISTED_ANKLE, hurtTime));
                }
            }
        }

        boolean roll = Math.random() > 0.75;
        if (attacker instanceof LivingEntity living && living.isUndead() && !victim.isUndead() && attacker != victim) {
            var effect = victim.getStatusEffect(Z7StatusEffects.STATUS_EFFECT_INFECTION);
            if (effect != null) {
                int nextTicks = effect.getDuration() - Z7Util.ticksFromMinutes(1f);
                System.out.println("Next ticks = " + nextTicks);
                victim.removeStatusEffect(Z7StatusEffects.STATUS_EFFECT_INFECTION);
                victim.addStatusEffect(new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_INFECTION, nextTicks));
            }
            else if (roll) {
                victim.addStatusEffect(new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_INFECTION, Z7InfectionStatusEffect.MAX_TICKS));
            }
        }

        roll = Math.random() > 0.5;
        if (attacker instanceof LivingEntity && attacker != victim && attacker.isLiving() && roll) {
            var effect = victim.getStatusEffect(Z7StatusEffects.STATUS_EFFECT_CONCUSSION);
            if (effect != null) {
                victim.removeStatusEffect(Z7StatusEffects.STATUS_EFFECT_CONCUSSION);
            }

            effect = victim.getStatusEffect(Z7StatusEffects.STATUS_EFFECT_CONCUSSION_LONG);
            if (effect != null) {
                victim.removeStatusEffect(Z7StatusEffects.STATUS_EFFECT_CONCUSSION_LONG);
            }

            victim.addStatusEffect(new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_CONCUSSION, Z7ConcussionStatusEffect.SHORT_DURATION));
        }

        roll = Math.random() > 0.4;
        if (attacker != null) {
            for (var handItem : attacker.getHandItems()) {
                if (handItem.isIn(ItemTags.SWORDS) || handItem.isIn(ItemTags.AXES)) {
                    var effect = victim.getStatusEffect(Z7StatusEffects.STATUS_EFFECT_BLEEDING);
                    if (effect != null) {
                        victim.removeStatusEffect(Z7StatusEffects.STATUS_EFFECT_BLEEDING);
                    }

                    effect = victim.getStatusEffect(Z7StatusEffects.STATUS_EFFECT_BLEEDING_LONG);
                    if (effect != null) {
                        victim.removeStatusEffect(Z7StatusEffects.STATUS_EFFECT_BLEEDING_LONG);
                    }

                    victim.addStatusEffect(new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_BLEEDING, Z7BleedingStatusEffect.SHORT_DURATION));
                }
            }
        }
    }
}
