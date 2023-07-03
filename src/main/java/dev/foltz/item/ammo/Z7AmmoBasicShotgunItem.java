package dev.foltz.item.ammo;

import dev.foltz.Zombie7;
import dev.foltz.entity.Z7BulletBronzeEntity;
import dev.foltz.entity.Z7BulletLeadEntity;
import dev.foltz.entity.Z7Entities;
import dev.foltz.item.gunlike.Z7GunlikeItem;
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
        return 10f;
    }

    @Override
    public float getBaseSpeed(ItemStack itemStack) {
        return 2.0f;
    }

    @Override
    public float getBaseRange(ItemStack itemStack) {
        return 12f;
    }

    @Override
    public List<Z7BulletLeadEntity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        ArrayList<Z7BulletLeadEntity> bullets = new ArrayList<>();
        int n = 5;
        for (int i = 0; i < n; i++) {
            Z7BulletLeadEntity bullet = new Z7BulletLeadEntity(Z7Entities.BULLET_LEAD_ENTITY, player.world);
            bullet.setPosition(player.getX(), player.getEyeY(), player.getZ());
            bullet.setOwner(player);

            float totalDamage = getBaseDamage(ammoStack);
            float totalSpeed = getBaseSpeed(ammoStack);
            float baseDistance = getBaseRange(ammoStack);
            if (gunStack.getItem() instanceof Z7GunlikeItem gun) {
                totalDamage = gun.getModifiedBulletDamage(gunStack, ammoStack, totalDamage);
                totalSpeed = gun.getModifiedBulletSpeed(gunStack, ammoStack, totalSpeed);
                baseDistance = gun.getModifiedBulletBaseRange(gunStack, ammoStack, baseDistance);
            }
            bullet.setDamage(totalDamage);
            bullet.setVelocity(player, player.getPitch(), player.getYaw(), 0f, totalSpeed, 10.0f);
            bullet.setBaseDistance(baseDistance);

            bullets.add(bullet);
        }
        return List.copyOf(bullets);
    }
}
