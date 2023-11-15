package dev.foltz.mixin.client;

import dev.foltz.item.gun.GunStagedItem;
import dev.foltz.item.stage.StagedItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HeldItemRenderer.class)
public abstract class Z7HeldItemRendererMixin {
    @Shadow protected abstract void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress);

    @Shadow public abstract void renderItem(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light);

    @Shadow public abstract void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

    @Accessor
    protected abstract ItemStack getMainHand();

    // todo: getUsingItemHandRenderType

    @Inject(
        method="renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
        at=@At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            ordinal = 0),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true)
    public void doRenderNoEquipOffset(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci, float f_, Hand hand, float g_, HeldItemRenderer.HandRenderType handRenderType, float h_, float i_, float j_, float k_) {
        if (player.isUsingItem() && player.getActiveItem().getItem() instanceof StagedItem<?>) {
//            System.out.println("Controlling gun!");
            var self = (HeldItemRenderer) (Object) this;
            var stack = player.getMainHandStack();
//            self.renderFirstPersonItem(player, tickDelta, g, Hand.MAIN_HAND, 0.0f, getMainHand(), 0.0f, matrices, vertexConsumers, light);
            // == renderFirstPersonItem
            System.out.println("Proper rendering");
            boolean bl = hand == Hand.MAIN_HAND;
            Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
            boolean bl2;
            boolean bl3 = bl2 = arm == Arm.RIGHT;
            matrices.push();
            this.applyEquipOffset(matrices, Arm.RIGHT, 0);

            float m = (float)stack.getMaxUseTime() - ((float) player.getItemUseTimeLeft() - tickDelta + 1.0f);
            float f = m / 20.0f;
            f = (f * f + f * 2.0f) / 3.0f;
            if (f > 1.0f) {
                f = 1.0f;
            }
            if (f > 0.1f) {
                float g = MathHelper.sin((float)((m - 0.1f) * 1.3f));
                float h = f - 0.1f;
                float j = g * h;
                matrices.translate(j * 0.0f, j * 0.004f, j * 0.0f);
            }
            matrices.translate(f * 0.0f, f * 0.0f, f * 0.04f);

            this.renderItem(player, stack, bl2 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND, !bl2, matrices, vertexConsumers, light);
            matrices.pop();

//            matrices.push();
//            int l = 1;
//            matrices.translate((float)l * -0.2785682f, 0.18344387f, 0.15731531f);
//            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-13.935f));
//            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 35.3f));
//            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)l * -9.785f));
//            float m = (float)stack.getMaxUseTime() - ((float) player.getItemUseTimeLeft() - tickDelta + 1.0f);
//            float f = m / 20.0f;
//            f = (f * f + f * 2.0f) / 3.0f;
//            if (f > 1.0f) {
//                f = 1.0f;
//            }
//            if (f > 0.1f) {
//                float g = MathHelper.sin((float)((m - 0.1f) * 1.3f));
//                float h = f - 0.1f;
//                float j = g * h;
//                matrices.translate(j * 0.0f, j * 0.004f, j * 0.0f);
//            }
//            matrices.translate(f * 0.0f, f * 0.0f, f * 0.04f);
//            matrices.scale(1.0f, 1.0f, 1.0f + f * 0.2f);
//            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float)l * 45.0f));
//            this.renderItem((LivingEntity)player, stack, bl2 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND, !bl2, matrices, vertexConsumers, light);
//            matrices.pop();

            vertexConsumers.draw();
            ci.cancel();
        }
    }
}
