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
        BulletBronzeEntity bullet = new BulletBronzeEntity(Z7Entities.BULLET_BRONZE_ENTITY, player.getWorld());
        bullet.setHitBoxExpansion(1.0f);
        bullet = modifyBulletEntity(bullet, player, gunStack, ammoStack);
        return List.of(bullet);
    }
}
