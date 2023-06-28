package dev.foltz.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.foltz.status.Z7StatusEffect;
import dev.foltz.status.Z7StatusEffects;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public abstract class Z7WorldRendererFogMixin {
    private static final float ALPHA_LONG = 0.8f;

    @Inject(method="renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;getSubmersionType()Lnet/minecraft/client/render/CameraSubmersionType;"), cancellable = true)
    public void renderSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        if (camera.getFocusedEntity() instanceof LivingEntity living) {
            if (living.hasStatusEffect(Z7StatusEffects.STATUS_EFFECT_CONCUSSION)) {
                var status = living.getStatusEffect(Z7StatusEffects.STATUS_EFFECT_CONCUSSION);
                RenderSystem.setShaderFogColor(0.9f, 0.9f, 0.9f, 1.0f);
                ci.cancel();
            }
            else if (living.hasStatusEffect(Z7StatusEffects.STATUS_EFFECT_CONCUSSION_LONG)) {
                var status = living.getStatusEffect(Z7StatusEffects.STATUS_EFFECT_CONCUSSION_LONG);
                RenderSystem.setShaderFogColor(0.9f, 0.9f, 0.9f, 1.0f);
                ci.cancel();
            }
        }
    }
}
