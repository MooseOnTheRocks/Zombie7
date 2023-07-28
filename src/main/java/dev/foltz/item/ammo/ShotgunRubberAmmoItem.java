package dev.foltz.item.ammo;

import dev.foltz.entity.Z7Entities;
import dev.foltz.entity.bullet.BulletBronzeEntity;
import dev.foltz.entity.bullet.BulletLeadEntity;
import dev.foltz.entity.bullet.BulletRubberEntity;
import dev.foltz.item.gun.GunStagedItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShotgunRubberAmmoItem extends AmmoItem {
    public ShotgunRubberAmmoItem() {
        super(new FabricItemSettings());
    }

    @Override
    public float getBaseDamage(ItemStack itemStack) {
        return 0f;
    }

    @Override
    public float getBaseSpeed(ItemStack itemStack) {
        return 1.5f;
    }

    @Override
    public float getBaseRange(ItemStack itemStack) {
        return 12f;
    }

    @Override
    public float getBaseAccuracy(ItemStack itemStack) {
        return 0.5f;
    }

    @Override
    public List<BulletRubberEntity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        ArrayList<BulletRubberEntity> bullets = new ArrayList<>();
        int n = player.world.random.nextFloat() > 0.75f ? 12 : 9;
        for (int i = 0; i < n; i++) {
            BulletRubberEntity bullet = new BulletRubberEntity(Z7Entities.BULLET_RUBBER_ENTITY, player.world);
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
            float divergence = determineDivergence(totalAccuracy);
//            System.out.println("divergence = " + divergence);
            bullet.setVelocity(player, player.getPitch(), player.getYaw(), 0f, totalSpeed, divergence);
            bullet.setBaseDistance(baseDistance);

            bullets.add(bullet);
        }
        return List.copyOf(bullets);
    }
}
