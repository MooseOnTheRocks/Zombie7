package dev.foltz.mixin;


import dev.foltz.status.Z7ConcussionStatusEffect;
import dev.foltz.status.Z7InfectionStatusEffect;
import dev.foltz.status.Z7StatusEffect;
import dev.foltz.status.Z7StatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.tag.ItemTags;
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
//            System.out.println("Fall distance: " + victim.fallDistance);
//            System.out.println("Fall damage: " + damage);
            if (damage > 1) {
                victim.addStatusEffect(new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_BROKEN_BONE, 60 * 20));
            }
        }

        if (attacker instanceof LivingEntity living && living.isUndead() && !victim.isUndead() && attacker != victim) {
            var effect = victim.getStatusEffect(Z7StatusEffects.STATUS_EFFECT_INFECTION);
            if (effect != null) {
                int nextTicks = (int) (0.99f * (effect.getDuration() - 2));
                victim.removeStatusEffect(Z7StatusEffects.STATUS_EFFECT_INFECTION);
                victim.addStatusEffect(new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_INFECTION, nextTicks));
            }
            else {
                victim.addStatusEffect(new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_INFECTION, Z7InfectionStatusEffect.MAX_TICKS));
            }
        }

        if (attacker instanceof LivingEntity && attacker != victim) {
            victim.addStatusEffect(new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_CONCUSSION, Z7ConcussionStatusEffect.SHORT_DURATION));
        }

        if (attacker != null) {
            for (var handItem : attacker.getHandItems()) {
                if (handItem.streamTags().anyMatch(tag -> tag == ItemTags.SWORDS || tag == ItemTags.AXES)) {
                    victim.addStatusEffect(new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_BLEEDING, 600));
                }
            }
        }
    }
}
