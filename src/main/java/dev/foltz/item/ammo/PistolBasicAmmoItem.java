package dev.foltz.item.ammo;

import dev.foltz.entity.bullet.BulletBronzeEntity;
import dev.foltz.entity.Z7Entities;
import dev.foltz.item.gun.GunStagedItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class PistolBasicAmmoItem extends AmmoItem {
    public PistolBasicAmmoItem() {
        super(new FabricItemSettings());
    }

    @Override
    public float getBaseDamage(ItemStack itemStack) {
        return 5f;
    }

    @Override
    public float getBaseSpeed(ItemStack itemStack) {
        return 3.5f;
    }

    @Override
    public float getBaseRange(ItemStack itemStack) {
        return 32f;
    }

    @Override
    public float getBaseAccuracy(ItemStack itemStack) {
        return 0.85f;
    }

    @Override
    public List<BulletBronzeEntity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        BulletBronzeEntity bullet = new BulletBronzeEntity(Z7Entities.BULLET_BRONZE_ENTITY, player.world);
        bullet.setHitBoxExpansion(0.5f);
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
        float divergence;
        if (totalAccuracy >= 0.95f) {
            divergence = 0f;
        }
        else if (totalAccuracy >= 0.9f) {
            divergence = 0.25f;
        }
        else if (totalAccuracy >= 0.8f) {
            divergence = 0.75f;
        }
        else if (totalAccuracy >= 0.7f) {
            divergence = 1f;
        }
        else if (totalAccuracy >= 0.6f) {
            divergence = 3f;
        }
        else if (totalAccuracy >= 0.5f) {
            divergence = 6f;
        }
        else if (totalAccuracy >= 0.4f) {
            divergence = 10f;
        }
        else if (totalAccuracy >= 0.3f) {
            divergence = 15f;
        }
        else if (totalAccuracy >= 0.2f) {
            divergence = 20f;
        }
        else if (totalAccuracy >= 0.1f) {
            divergence = 25f;
        }
        else {
            divergence = 50f;
        }

        bullet.setVelocity(player, player.getPitch(), player.getYaw(), 0f, totalSpeed, divergence);
        bullet.setBaseDistance(baseDistance);

        return List.of(bullet);
    }
}