package dev.foltz;

import dev.foltz.entity.*;
import dev.foltz.entity.model.Z7BulletEntityModel;
import dev.foltz.entity.model.Z7GrenadeEntityModel;
import dev.foltz.item.Z7GunItem;
import dev.foltz.network.Z7Networking;
import dev.foltz.render.Z7ConcussionFogModifier;
import dev.foltz.render.Z7ConcussionLongFogModifier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class Zombie7Client implements ClientModInitializer {
    public static final Z7ConcussionFogModifier FOG_MODIFIER = new Z7ConcussionFogModifier();
    public static final Z7ConcussionLongFogModifier FOG_MODIFIER_LONG = new Z7ConcussionLongFogModifier();

    public static final KeyBinding RELOAD_BIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.zombie7.reload",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.category.zombie7"
    ));

    private boolean isPressingReload = false;

//    public static final EntityModelLayer MODEL_GRENADE_LAYER = new EntityModelLayer(new Identifier(Zombie7.MODID, "grenade"), "main");
    public static final EntityModelLayer MODEL_BULLET_LAYER = new EntityModelLayer(new Identifier(Zombie7.MODID, "bullet"), "main");

	@Override
	public void onInitializeClient() {
        Z7ModelPredicates.registerAllModelPredicates();

        EntityRendererRegistry.register(Zombie7.GRENADE_ENTITY, context -> new Z7GrenadeEntityRenderer(context, 1.0f, true));
//        EntityModelLayerRegistry.registerModelLayer(MODEL_GRENADE_LAYER, Z7GrenadeEntityModel::getTexturedModelData);

        EntityRendererRegistry.register(Zombie7.BULLET_ENTITY, Z7BulletEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_BULLET_LAYER, Z7BulletEntityModel::getTexturedModelData);

        ClientPreAttackCallback.EVENT.register((client, player, clickCount) -> {
            if (player.getMainHandStack().getItem() instanceof Z7GunItem) {
                return true;
            }
            else {
                return false;
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (RELOAD_BIND.isPressed() && !isPressingReload) {
                ClientPlayNetworking.send(Z7Networking.BEGIN_RELOAD_PACKET_ID, PacketByteBufs.empty());
                isPressingReload = true;
            }
            else if (!RELOAD_BIND.isPressed() && isPressingReload) {
                ClientPlayNetworking.send(Z7Networking.END_RELOAD_PACKET_ID, PacketByteBufs.empty());
                isPressingReload = false;
            }
        });
	}
}