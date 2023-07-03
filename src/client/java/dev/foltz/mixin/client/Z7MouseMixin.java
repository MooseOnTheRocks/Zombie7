package dev.foltz.mixin.client;

import dev.foltz.network.Z7Networking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class Z7MouseMixin {
    @Inject(
        method="onMouseButton",
        at=@At(
            value="INVOKE_ASSIGN",
            target="Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;",
            ordinal=0
        )
    )
    protected void mouseAction(long window, int button, int action, int mods, CallbackInfo ci) {
//         Ensure client is connected to a server and clicking in-game
        if (MinecraftClient.getInstance().getNetworkHandler() == null || MinecraftClient.getInstance().currentScreen != null) {
            return;
        }

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (action == GLFW.GLFW_PRESS) {
                // Left mouse pressed
                ClientPlayNetworking.send(Z7Networking.SHOOT_PRESS_PACKET_ID, PacketByteBufs.empty());
            }
            else {
                // Left mouse released
                ClientPlayNetworking.send(Z7Networking.SHOOT_RELEASE_PACKET_ID, PacketByteBufs.empty());
            }
        }
        else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (action == GLFW.GLFW_PRESS) {
                // Right mouse pressed
//                ClientPlayNetworking.send(Z7Networking.ZOOM_PRESS_PACKET_ID, PacketByteBufs.empty());
            }
            else {
                // Right mouse released
//                ClientPlayNetworking.send(Z7Networking.ZOOM_RELEASE_PACKET_ID, PacketByteBufs.empty());
            }
        }
    }
}
