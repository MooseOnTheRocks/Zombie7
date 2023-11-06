package dev.foltz.item.ammo;

import dev.foltz.entity.bullet.BulletBronzeEntity;
import dev.foltz.entity.Z7Entities;
import dev.foltz.item.CompositeResourceItem;
import dev.foltz.item.gun.GunStagedItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class PistolBasicAmmoItem extends CompositeResourceItem implements AmmoItem {
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
        BulletBronzeEntity bullet = new BulletBronzeEntity(Z7Entities.BULLET_BRONZE_ENTITY, player.getWorld());
        bullet.setHitBoxExpansion(0.5f);
        bullet = modifyBulletEntity(bullet, player, gunStack, ammoStack);
        return List.of(bullet);
    }
}
