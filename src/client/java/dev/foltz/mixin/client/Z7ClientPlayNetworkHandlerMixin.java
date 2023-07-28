package dev.foltz.mixin.client;

import dev.foltz.entity.*;
import dev.foltz.entity.bullet.BulletBronzeEntity;
import dev.foltz.entity.bullet.BulletLeadEntity;
import dev.foltz.entity.grenade.ContactGrenadeEntity;
import dev.foltz.entity.grenade.FragGrenadeEntity;
import dev.foltz.entity.grenade.MolotovGrenadeEntity;
import dev.foltz.entity.grenade.StickyGrenadeEntity;
import dev.foltz.entity.misc.BowlingBallGrenadeEntity;
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
            entity = new FragGrenadeEntity(Z7Entities.FRAG_GRENADE_ENTITY, this.world);
        }
        else if (entityType == Z7Entities.CONTACT_GRENADE_ENTITY) {
            entity = new ContactGrenadeEntity(Z7Entities.CONTACT_GRENADE_ENTITY, this.world);
        }
        else if (entityType == Z7Entities.MOLOTOV_GRENADE_ENTITY) {
            entity = new MolotovGrenadeEntity(Z7Entities.MOLOTOV_GRENADE_ENTITY, this.world);
        }
        else if (entityType == Z7Entities.STICKY_GRENADE_ENTITY) {
            entity = new StickyGrenadeEntity(Z7Entities.STICKY_GRENADE_ENTITY, this.world);
        }
        else if (entityType == Z7Entities.BOWLING_BALL_GRENADE_ENTITY) {
            entity = new BowlingBallGrenadeEntity(Z7Entities.BOWLING_BALL_GRENADE_ENTITY, this.world);
        }
        else if (entityType == Z7Entities.BULLET_BRONZE_ENTITY) {
            entity = new BulletBronzeEntity(Z7Entities.BULLET_BRONZE_ENTITY, this.world);
        }
        else if (entityType == Z7Entities.BULLET_LEAD_ENTITY) {
            entity = new BulletLeadEntity(Z7Entities.BULLET_LEAD_ENTITY, this.world);
        }

        if (entity != null) {
            int entityId = packet.getId();
            entity.onSpawnPacket(packet);
            this.world.addEntity(entityId, entity);
            ci.cancel();
        }
    }
}
