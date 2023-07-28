package dev.foltz.item.ammo;

import dev.foltz.entity.bullet.BulletBronzeEntity;
import dev.foltz.entity.Z7Entities;
import dev.foltz.item.gun.GunStagedItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class MagnumBasicAmmoItem extends AmmoItem {
    public MagnumBasicAmmoItem() {
        super(new FabricItemSettings());
    }

    @Override
    public float getBaseDamage(ItemStack itemStack) {
        return 8f;
    }

    @Override
    public float getBaseSpeed(ItemStack itemStack) {
        return 4f;
    }

    @Override
    public float getBaseRange(ItemStack itemStack) {
        return 64f;
    }

    @Override
    public float getBaseAccuracy(ItemStack itemStack) {
        return 0.90f;
    }

    @Override
    public List<BulletBronzeEntity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        BulletBronzeEntity bullet = new BulletBronzeEntity(Z7Entities.BULLET_BRONZE_ENTITY, player.world);
        bullet.setHitBoxExpansion(1.0f);
        bullet.setPosition(player.getX(), player.getEyeY() - bullet.getHeight() / 2f, player.getZ());
        bullet.setOwner(player);

        float totalDamage = getBaseDamage(ammoStack);
        float totalSpeed = getBaseSpeed(ammoStack);
        float baseDistance = getBaseRange(ammoStack);
        float totalAccuracy = getBaseAccuracy(ammoStack);
        if (gunStack.getItem() instanceof GunStagedItem gun) {
            totalDamage = gun.getModifiedBulletDamage(gunStack, ammoStack, totalDamage);
            totalSpeed = gun.getModifiedBulletSpeed(gunStack, ammoStack, totalSpeed);
            baseDistance = gun.getModifiedBulletBaseRange(gunStack, ammoStack, baseDistance);
            totalAccuracy = gun.getModifiedBulletAccuracy(gunStack, ammoStack, totalAccuracy);
        }
        bullet.setDamage(totalDamage);
        float divergence = determineDivergence(totalAccuracy);

        bullet.setVelocity(player, player.getPitch(), player.getYaw(), 0f, totalSpeed, divergence);
        bullet.setBaseDistance(baseDistance);

        return List.of(bullet);
    }
}
