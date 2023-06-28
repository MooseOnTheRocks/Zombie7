package dev.foltz.mixin.client;

import dev.foltz.Zombie7;
import dev.foltz.entity.Z7BulletEntity;
import dev.foltz.entity.Z7GrenadeEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Vec3d;
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
        if (entityType == Zombie7.GRENADE_ENTITY) {
            entity = new Z7GrenadeEntity(Zombie7.GRENADE_ENTITY, this.world);
        }
        else if (entityType == Zombie7.BULLET_ENTITY) {
            entity = new Z7BulletEntity(Zombie7.BULLET_ENTITY, this.world);
        }

        if (entity != null) {
            int entityId = packet.getId();
            entity.onSpawnPacket(packet);
            this.world.addEntity(entityId, entity);
            ci.cancel();
        }
    }
}
