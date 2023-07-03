package dev.foltz.mixin.client;

import dev.foltz.Zombie7;
import dev.foltz.entity.*;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public class Z7ClientPlayNetworkHandlerMixin {
    @Shadow private ClientWorld world;

    @Inject(
            method = "onEntitySpawn(Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;getEntityType()Lnet/minecraft/entity/EntityType;"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    ) // thank you parzivail
    private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci, EntityType entityType) {
        Entity entity = null;
        if (entityType == Z7Entities.FRAG_GRENADE_ENTITY) {
            entity = new Z7FragGrenadeEntity(Z7Entities.FRAG_GRENADE_ENTITY, this.world);
        }
        else if (entityType == Z7Entities.MOLOTOV_GRENADE_ENTITY) {
            entity = new Z7MolotovGrenadeEntity(Z7Entities.MOLOTOV_GRENADE_ENTITY, this.world);
        }
        else if (entityType == Z7Entities.BULLET_BRONZE_ENTITY) {
            entity = new Z7BulletBronzeEntity(Z7Entities.BULLET_BRONZE_ENTITY, this.world);
        }
        else if (entityType == Z7Entities.BULLET_LEAD_ENTITY) {
            entity = new Z7BulletLeadEntity(Z7Entities.BULLET_LEAD_ENTITY, this.world);
        }

        if (entity != null) {
            int entityId = packet.getId();
            entity.onSpawnPacket(packet);
            this.world.addEntity(entityId, entity);
            ci.cancel();
        }
    }
}
