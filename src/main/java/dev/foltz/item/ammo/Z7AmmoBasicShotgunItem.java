package dev.foltz.item.ammo;

import dev.foltz.entity.Z7BulletLeadEntity;
import dev.foltz.entity.Z7Entities;
import dev.foltz.item.gun.GunStagedItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Z7AmmoBasicShotgunItem extends Z7AmmoItem {
    public Z7AmmoBasicShotgunItem() {
        super(new FabricItemSettings());
    }

    @Override
    public float getBaseDamage(ItemStack itemStack) {
        return 5f;
    }

    @Override
    public float getBaseSpeed(ItemStack itemStack) {
        return 2.0f;
    }

    @Override
    public float getBaseRange(ItemStack itemStack) {
        return 10f;
    }

    @Override
    public float getBaseAccuracy(ItemStack itemStack) {
        return 0.6f;
    }

    @Override
    public List<Z7BulletLeadEntity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        ArrayList<Z7BulletLeadEntity> bullets = new ArrayList<>();
        int n = player.world.random.nextFloat() > 0.75f ? 9 : 6;
        for (int i = 0; i < n; i++) {
            Z7BulletLeadEntity bullet = new Z7BulletLeadEntity(Z7Entities.BULLET_LEAD_ENTITY, player.world);
            bullet.setHitBoxExpansion(0.0f);
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
//            System.out.println("shotgun ammo totalAccuracy = " + totalAccuracy);
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
            else if (totalAccuracy >= 0.45f) {
                divergence = 8f;
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
//            System.out.println("divergence = " + divergence);
            bullet.setVelocity(player, player.getPitch(), player.getYaw(), 0f, totalSpeed, divergence);
            bullet.setBaseDistance(baseDistance);

            bullets.add(bullet);
        }
        return List.copyOf(bullets);
    }
}
