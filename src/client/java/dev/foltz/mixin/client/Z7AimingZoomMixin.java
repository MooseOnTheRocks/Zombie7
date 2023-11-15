package dev.foltz.mixin.client;

import dev.foltz.item.gun.GunStagedItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class Z7AimingZoomMixin {
    @Inject(
        method="getFovMultiplier",
        at=@At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isUsingItem()Z"),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true)
    public void doAiming(CallbackInfoReturnable<Float> cir, float f, ItemStack stack) {
        var self = ((AbstractClientPlayerEntity) (Object) this);
        if (self.isUsingItem() && MinecraftClient.getInstance().options.getPerspective().isFirstPerson() && stack.getItem() instanceof GunStagedItem<?> gun) {
            float g = self.getItemUseTime() / gun.getAimingTimeModifier();
            g = g > 1.0f ? 1.0f : (g *= g);
            f *= 1.0f - g * gun.getAimingZoomModifier();
            var ret = MathHelper.lerp((float) MinecraftClient.getInstance().options.getFovEffectScale().getValue().floatValue(), (float)1.0f, (float)f);
//            System.out.println("Setting zoom: " + ret);
            cir.setReturnValue(ret);
        }
    }
}
