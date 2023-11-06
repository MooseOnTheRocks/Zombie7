package dev.foltz;

import dev.foltz.entity.*;
import dev.foltz.entity.misc.BowlingBallGrenadeEntity;
import dev.foltz.entity.misc.CannonBallEntity;
import dev.foltz.entity.model.BulletBronzeEntityModel;
import dev.foltz.entity.model.BulletLeadEntityModel;
import dev.foltz.entity.model.BulletRubberEntityModel;
import dev.foltz.item.stage.StagedItem;
import dev.foltz.network.Z7Networking;
import dev.foltz.render.ConcussionFogModifier;
import dev.foltz.render.ConcussionLongFogModifier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class Zombie7Client implements ClientModInitializer {
    public static final ConcussionFogModifier FOG_MODIFIER = new ConcussionFogModifier();
    public static final ConcussionLongFogModifier FOG_MODIFIER_LONG = new ConcussionLongFogModifier();

    public static final KeyBinding RELOAD_BIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.zombie7.reload",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.category.zombie7"
    ));

    private boolean isPressingReload = false;
    private boolean hasOpenScreen = false;

    public static final EntityModelLayer MODEL_BULLET_BRONZE_LAYER = new EntityModelLayer(new Identifier(Zombie7.MODID, "bullet_bronze"), "main");
    public static final EntityModelLayer MODEL_BULLET_LEAD_LAYER = new EntityModelLayer(new Identifier(Zombie7.MODID, "bullet_lead"), "main");
    public static final EntityModelLayer MODEL_BULLET_RUBBER_LAYER = new EntityModelLayer(new Identifier(Zombie7.MODID, "bullet_rubber"), "main");

	@Override
	public void onInitializeClient() {
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new Z7ModelResourceProvider());

        Z7ModelPredicates.registerAllModelPredicates();

        EntityRendererRegistry.register(Z7Entities.FRAG_GRENADE_ENTITY, FragGrenadeEntityRenderer::new);
        EntityRendererRegistry.register(Z7Entities.CONTACT_GRENADE_ENTITY, ContactGrenadeEntityRenderer::new);
        EntityRendererRegistry.register(Z7Entities.MOLOTOV_GRENADE_ENTITY, MolotovGrenadeEntityRenderer::new);
        EntityRendererRegistry.register(Z7Entities.STICKY_GRENADE_ENTITY, StickyGrenadeEntityRenderer::new);
        EntityRendererRegistry.register(Z7Entities.BOWLING_BALL_GRENADE_ENTITY, BowlingBallGrenadeEntityRenderer::new);
        EntityRendererRegistry.register(Z7Entities.CANNON_BALL_ENTITY, CannonBallEntityRenderer::new);

        EntityRendererRegistry.register(Z7Entities.BULLET_BRONZE_ENTITY, BulletBronzeEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_BULLET_BRONZE_LAYER, BulletBronzeEntityModel::getTexturedModelData);

        EntityRendererRegistry.register(Z7Entities.BULLET_LEAD_ENTITY, BulletLeadEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_BULLET_LEAD_LAYER, BulletLeadEntityModel::getTexturedModelData);


        EntityRendererRegistry.register(Z7Entities.BULLET_RUBBER_ENTITY, BulletRubberEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_BULLET_RUBBER_LAYER, BulletRubberEntityModel::getTexturedModelData);

        ClientPreAttackCallback.EVENT.register((client, player, clickCount) -> {
            Item item = player.getMainHandStack().getItem();
            if (item instanceof StagedItem) {
                return true;
            }
            else {
                return false;
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.world == null) {
                return;
            }

            if (RELOAD_BIND.isPressed() && !isPressingReload) {
                ClientPlayNetworking.send(Z7Networking.RELOAD_PRESS_PACKET_ID, PacketByteBufs.empty());
                isPressingReload = true;
            }
            else if (!RELOAD_BIND.isPressed() && isPressingReload) {
                ClientPlayNetworking.send(Z7Networking.RELOAD_RELEASE_PACKET_ID, PacketByteBufs.empty());
                isPressingReload = false;
            }

            if (!hasOpenScreen && client.currentScreen != null) {
//                System.out.println("Open screen");
                hasOpenScreen = true;
                ClientPlayNetworking.send(Z7Networking.RELOAD_RELEASE_PACKET_ID, PacketByteBufs.empty());
                ClientPlayNetworking.send(Z7Networking.SHOOT_RELEASE_PACKET_ID, PacketByteBufs.empty());
            }
            else if (hasOpenScreen && client.currentScreen == null) {
//                System.out.println("Closing screen.");
                hasOpenScreen = false;
//                ClientPlayNetworking.send(Z7Networking.RELOAD_RELEASE_PACKET_ID, PacketByteBufs.empty());
//                ClientPlayNetworking.send(Z7Networking.SHOOT_RELEASE_PACKET_ID, PacketByteBufs.empty());
            }
        });
	}
}