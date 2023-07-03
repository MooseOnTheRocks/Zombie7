package dev.foltz;

import dev.foltz.entity.*;
import dev.foltz.entity.model.Z7BulletBronzeEntityModel;
import dev.foltz.entity.model.Z7BulletLeadEntityModel;
import dev.foltz.item.gunlike.Z7GunlikeItem;
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

    public static final EntityModelLayer MODEL_BULLET_BRONZE_LAYER = new EntityModelLayer(new Identifier(Zombie7.MODID, "bullet_bronze"), "main");
    public static final EntityModelLayer MODEL_BULLET_LEAD_LAYER = new EntityModelLayer(new Identifier(Zombie7.MODID, "bullet_lead"), "main");

	@Override
	public void onInitializeClient() {
        Z7ModelPredicates.registerAllModelPredicates();

        EntityRendererRegistry.register(Z7Entities.FRAG_GRENADE_ENTITY, Z7FragGrenadeEntityRenderer::new);
        EntityRendererRegistry.register(Z7Entities.MOLOTOV_GRENADE_ENTITY, Z7MolotovGrenadeEntityRenderer::new);

        EntityRendererRegistry.register(Z7Entities.BULLET_BRONZE_ENTITY, Z7BulletBronzeEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_BULLET_BRONZE_LAYER, Z7BulletBronzeEntityModel::getTexturedModelData);

        EntityRendererRegistry.register(Z7Entities.BULLET_LEAD_ENTITY, Z7BulletLeadEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_BULLET_LEAD_LAYER, Z7BulletLeadEntityModel::getTexturedModelData);


        ClientPreAttackCallback.EVENT.register((client, player, clickCount) -> {
            if (player.getMainHandStack().getItem() instanceof Z7GunlikeItem) {
                return true;
            }
            else {
                return false;
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (RELOAD_BIND.isPressed() && !isPressingReload) {
                ClientPlayNetworking.send(Z7Networking.RELOAD_PRESS_PACKET_ID, PacketByteBufs.empty());
                isPressingReload = true;
            }
            else if (!RELOAD_BIND.isPressed() && isPressingReload) {
                ClientPlayNetworking.send(Z7Networking.RELOAD_RELEASE_PACKET_ID, PacketByteBufs.empty());
                isPressingReload = false;
            }
        });
	}
}