package dev.foltz.item.ammo;

import dev.foltz.entity.Z7Entities;
import dev.foltz.entity.bullet.BulletBronzeEntity;
import dev.foltz.entity.bullet.BulletLeadEntity;
import dev.foltz.entity.bullet.BulletRubberEntity;
import dev.foltz.item.CompositeResourceItem;
import dev.foltz.item.gun.GunStagedItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShotgunRubberAmmoItem extends CompositeResourceItem implements AmmoItem {
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
        int n = player.getWorld().random.nextFloat() > 0.75f ? 12 : 9;
        for (int i = 0; i < n; i++) {
            BulletRubberEntity bullet = new BulletRubberEntity(Z7Entities.BULLET_RUBBER_ENTITY, player.getWorld());
            bullet.setHitBoxExpansion(0.25f);
            bullet = modifyBulletEntity(bullet, player, gunStack, ammoStack);
            bullets.add(bullet);
        }
        return List.copyOf(bullets);
    }
}
