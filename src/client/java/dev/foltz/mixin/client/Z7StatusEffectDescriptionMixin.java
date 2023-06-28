package dev.foltz.mixin.client;

import dev.foltz.status.Z7InfectionStatusEffect;
import dev.foltz.status.Z7StatusEffect;
import dev.foltz.status.Z7StatusEffects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(AbstractInventoryScreen.class)
public abstract class Z7StatusEffectDescriptionMixin {
    @Inject(method="getStatusEffectDescription", at=@At("HEAD"))
    protected void customStatusEffectDescription(StatusEffectInstance statusEffect, CallbackInfoReturnable<Text> cir) {
//        if (statusEffect.getEffectType() == Z7StatusEffects.STATUS_EFFECT_INFECTION) {
//            MutableText mutableText = statusEffect.getEffectType().getName().copy();
//            int percent = MathHelper.lerp((float) statusEffect.getDuration() / (float) Z7InfectionStatusEffect.MAX_TICKS, 100, 0);
//            cir.setReturnValue(mutableText
//                    .append(ScreenTexts.SPACE)
//                    .append(Text.of(percent + "%")));
//        }
    }

    @Redirect(method="drawStatusEffectDescriptions", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectUtil;durationToString(Lnet/minecraft/entity/effect/StatusEffectInstance;F)Lnet/minecraft/text/Text;"))
    protected Text customStatusEffectDurationDescription(StatusEffectInstance effect, float multiplier) {
        if (effect.getEffectType() == Z7StatusEffects.STATUS_EFFECT_CONCUSSION) {
            return MutableText.of(Text.of("*bonk*").getContent()).formatted(Formatting.ITALIC);
        }
        else if (effect.getEffectType() == Z7StatusEffects.STATUS_EFFECT_INFECTION) {
            int percent = MathHelper.lerp((float) effect.getDuration() / (float) Z7InfectionStatusEffect.MAX_TICKS, 100, 0);
            return MutableText.of(Text.of(percent + "%").getContent());
        }
        else {
            return StatusEffectUtil.durationToString(effect, 1.0f);
        }
    }
}
