package dev.foltz.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.foltz.Zombie7Client;
import dev.foltz.status.StatusEffects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(BackgroundRenderer.class)
public abstract class Z7FogModifierMixin {
    @Inject(method="getFogModifier", at=@At("RETURN"), cancellable = true)
    private static void getFogModifier(Entity entity, float tickDelta, CallbackInfoReturnable<BackgroundRenderer.StatusEffectFogModifier> cir) {
        if (cir.getReturnValue() == null && entity instanceof LivingEntity living) {
            if (Zombie7Client.FOG_MODIFIER.shouldApply(living, tickDelta)) {
                cir.setReturnValue(Zombie7Client.FOG_MODIFIER);
            }
            else if (Zombie7Client.FOG_MODIFIER_LONG.shouldApply(living, tickDelta)) {
                cir.setReturnValue(Zombie7Client.FOG_MODIFIER_LONG);
            }
        }
    }

    @Inject(method="render", at=@At("TAIL"))
    private static void render(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfo ci) {
        if (!(camera.getFocusedEntity() instanceof LivingEntity entity)) {
            return;
        }

        if (entity.hasStatusEffect(StatusEffects.STATUS_EFFECT_CONCUSSION)) {
            StatusEffectInstance status = entity.getStatusEffect(StatusEffects.STATUS_EFFECT_CONCUSSION);
            if (status != null) {
                RenderSystem.clearColor(0.9f, 0.9f, 0.9f, 0.0f);
            }
        }
        else if (entity.hasStatusEffect(StatusEffects.STATUS_EFFECT_CONCUSSION_LONG)) {
            StatusEffectInstance status = entity.getStatusEffect(StatusEffects.STATUS_EFFECT_CONCUSSION_LONG);
            if (status != null) {
                RenderSystem.clearColor(0.9f, 0.9f, 0.9f, 0.0f);
            }
        }
    }
}

