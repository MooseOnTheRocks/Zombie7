package dev.foltz.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.foltz.AccuracyCalculator;
import dev.foltz.Zombie7Client;
import dev.foltz.item.gun.GunStagedItem;
import dev.foltz.item.stage.StagedItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InGameHud.class)
public abstract class Z7InGameHudMixin {
    @Shadow @Final private MinecraftClient client;

    @Shadow @Final private static Identifier ICONS;

    @Inject(
            method="renderCrosshair",
            at=@At(
                value="INVOKE",
                target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V",
                ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    protected void doRenderGunCrosshair(DrawContext context, CallbackInfo ci, GameOptions gameOptions) {
        if (client.player == null || !(client.player.getMainHandStack().getItem() instanceof GunStagedItem<?>)) {
            return;
        }
//        System.out.println("Custom crosshair!");
        int size = 31;
        int stride = 32;
        int windowWidth = context.getScaledWindowWidth();
        int windowHeight = context.getScaledWindowHeight();
        int textureWidth = 512;
        int textureHeight = 512;
        int frameCount = 10;
//        context.drawTexture(Zombie7Client.GUN_CROSSHAIR, (windowWidth - 15) / 2, (windowHeight - 15) / 2, 0, 0, 15, 15);
//        context.drawTexture(ICONS, (windowWidth - size) / 2, (windowHeight - size) / 2, 0, 0, size, size);
//        context.drawTexture(Zombie7Client.GUN_CROSSHAIR, (windowWidth - size) / 2, (windowHeight - size) / 2, 0, 0, size, size, textureWidth, textureHeight);
//        int index = (int) (System.currentTimeMillis() / 1000) % 8;
//        System.out.println("index = " + (index + 1) + "/8");
        float acc = AccuracyCalculator.calculateAccuracy(null, client.player);
        int index = (int) Math.ceil(acc * (frameCount - 1));
        System.out.println("index = " + (index + 1) + "/" + frameCount);
        int u = stride * index;
        int v = 0;
        context.drawTexture(Zombie7Client.GUN_CROSSHAIR, (windowWidth - size) / 2, (windowHeight - size) / 2, u, v, size, size, textureWidth, textureHeight);
        RenderSystem.defaultBlendFunc();
        ci.cancel();
    }
}
