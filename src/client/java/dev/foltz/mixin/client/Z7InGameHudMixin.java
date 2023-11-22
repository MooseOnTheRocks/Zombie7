package dev.foltz.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.foltz.Zombie7Client;
import dev.foltz.item.gun.GunStagedItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static dev.foltz.Zombie7Client.*;

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
        if (client.player == null || !(client.player.getMainHandStack().getItem() instanceof GunStagedItem<?> gun)) {
            return;
        }
        var gunStack = client.player.getMainHandStack();

        // -- Render dynamic crosshair
        int size = 31;
        int stride = 32;
        int windowWidth = context.getScaledWindowWidth();
        int windowHeight = context.getScaledWindowHeight();
        int textureWidth = 512;
        int textureHeight = 512;

        float wantedAccuracy = gun.getGunRecoil(client.player.getMainHandStack(), client.player);
        float diff = wantedAccuracy - Zombie7Client.currentAccuracyValue;
        boolean isFiring = gun.getStageName(client.player.getMainHandStack()).equals("firing");
        diff *= 0.075f;
        Zombie7Client.currentAccuracyValue += diff;
        if (isFiring || Math.abs(Zombie7Client.currentAccuracyValue - wantedAccuracy) <= 0.001f) {
            Zombie7Client.currentAccuracyValue = wantedAccuracy;
        }
        Zombie7Client.currentAccuracyValue = MathHelper.clamp(Zombie7Client.currentAccuracyValue, 0f, 1f);
        int index = gun.getCrosshairFrameIndex(Zombie7Client.currentAccuracyValue, client.player);
        int u = stride * (index % 16);
        int v = stride * (index / 16);
        context.drawTexture(gun.getCrosshairTexture(), (windowWidth - size) / 2, (windowHeight - size) / 2, u, v, size, size, textureWidth, textureHeight);


        // -- Render bullet info
        RenderSystem.defaultBlendFunc();
        float scale = 1.0f;

        final int slotsWidth = 96;
        final int slotsHeight = 68;

        final int u0 = 64;
        final int v0 = 32;
        final int width0 = 32;
        final int height0 = 32;

        final int u1 = 64;
        final int v1 = 0;
        final int width1 = 32;
        final int height1 = 32;

        final int u2 = 32;
        final int v2 = 0;
        final int width2 = 32;
        final int height2 = 50;

        final int u3 = 0;
        final int v3 = 0;
        final int width3 = 32;
        final int height3 = 68;

        final int uExtra = 72;
        final int vExtra = 64;
        final int widthExtra = 16;
        final int heightExtra = 4;

        var ammoList = gun.getCompactAmmoList(gunStack);

        int x = 0;
        int y = windowHeight - 74;
        int itemX = x + 8;
        int itemY = y + 8;
        int itemOffY = 18;

        switch (ammoList.size()) {
            case 0 -> {
                drawScaledTexture(context, scale, GUI_AMMO_SLOTS, x, y, u0, v0, width0, height0, slotsWidth, slotsHeight);
            }
            case 1 -> {
                drawScaledTexture(context, scale, GUI_AMMO_SLOTS, x, y, u1, v1, width1, height1, slotsWidth, slotsHeight);
                drawScaledItemWithCount(context, client.textRenderer, scale, ammoList.get(0), itemX, itemY);
            }
            case 2 -> {
                drawScaledTexture(context, scale, GUI_AMMO_SLOTS, x, y, u2, v2, width2, height2, slotsWidth, slotsHeight);
                drawScaledItemWithCount(context, client.textRenderer, scale, ammoList.get(0), itemX, itemY);
                drawScaledItemWithCount(context, client.textRenderer, scale, ammoList.get(1), itemX, itemY + itemOffY);
            }
            case 3 -> {
                drawScaledTexture(context, scale, Zombie7Client.GUI_AMMO_SLOTS, x, y, u3, v3, width3, height3, slotsWidth, slotsHeight);
                drawScaledItemWithCount(context, client.textRenderer, scale, ammoList.get(0), itemX, itemY);
                drawScaledItemWithCount(context, client.textRenderer, scale, ammoList.get(1), itemX, itemY + itemOffY);
                drawScaledItemWithCount(context, client.textRenderer, scale, ammoList.get(2), itemX, itemY + itemOffY * 2);
            }
            default -> {
                drawScaledTexture(context, scale, GUI_AMMO_SLOTS, x, y, u3, v3, width3, height3, slotsWidth, slotsHeight);
                drawScaledItemWithCount(context, client.textRenderer, scale, ammoList.get(0), itemX, itemY);
                drawScaledItemWithCount(context, client.textRenderer, scale, ammoList.get(1), itemX, itemY + itemOffY);
                drawScaledItemWithCount(context, client.textRenderer, scale, ammoList.get(2), itemX, itemY + itemOffY * 2);
                drawScaledTexture(context, scale, GUI_AMMO_SLOTS, x + 8, y + height3, uExtra, vExtra, widthExtra, heightExtra, slotsWidth, slotsHeight);
            }
        }

        ci.cancel();
    }
}
