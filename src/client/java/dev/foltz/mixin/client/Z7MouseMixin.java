package dev.foltz.mixin.client;

import dev.foltz.network.Z7Networking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Mouse.class)
public class Z7MouseMixin {
    @Inject(method="onMouseButton", at=@At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    // Signature is correct, despite complaints otherwise.
    // TODO: Fix bug: able to open inventory while shooting, which makes gun continue shooting.
    protected void mouseAction(long window, int button, int action, int mods, CallbackInfo ci, boolean bl, int i) {
        // Ensure client is connected to a server and clicking in-game
        if (MinecraftClient.getInstance().getNetworkHandler() == null || MinecraftClient.getInstance().currentScreen != null) {
            return;
        }

        if (button == 0) {
            if (action == 1) {
                // Left mouse pressed
                ClientPlayNetworking.send(Z7Networking.BEGIN_SHOOT_PACKET_ID, PacketByteBufs.empty());
//                System.out.println("Left mouse!!");
            }
            else {
                // Left mouse released
                ClientPlayNetworking.send(Z7Networking.END_SHOOT_PACKET_ID, PacketByteBufs.empty());
//                System.out.println("Right mouse!!");
            }
        }
        else if (button == 1) {
            if (action == 1) {
                // Right mouse pressed
            }
            else {
                // Right mouse released
            }
        }
    }
}
