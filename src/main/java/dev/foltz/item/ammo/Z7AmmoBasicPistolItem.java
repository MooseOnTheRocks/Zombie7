package dev.foltz.item.ammo;

import dev.foltz.Zombie7;
import dev.foltz.entity.Z7BulletBronzeEntity;
import dev.foltz.entity.Z7BulletEntity;
import dev.foltz.entity.Z7Entities;
import dev.foltz.item.gunlike.Z7GunlikeItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class Z7AmmoBasicPistolItem extends Z7AmmoItem {
    public Z7AmmoBasicPistolItem() {
        super(new FabricItemSettings());
    }

    @Override
    public float getBaseDamage(ItemStack itemStack) {
        return 7f;
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
    public List<Z7BulletBronzeEntity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        Z7BulletBronzeEntity bullet = new Z7BulletBronzeEntity(Z7Entities.BULLET_BRONZE_ENTITY, player.world);
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
        bullet.setVelocity(player, player.getPitch(), player.getYaw(), 0f, totalSpeed, 0.0f);
        bullet.setBaseDistance(baseDistance);

        return List.of(bullet);
    }
}
