package dev.foltz.item.ammo;

import dev.foltz.entity.bullet.BulletLeadEntity;
import dev.foltz.entity.Z7Entities;
import dev.foltz.item.CompositeResourceItem;
import dev.foltz.item.gun.GunStagedItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShotgunBasicAmmoItem extends CompositeResourceItem implements AmmoItem {
    public ShotgunBasicAmmoItem() {
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
    public List<BulletLeadEntity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        ArrayList<BulletLeadEntity> bullets = new ArrayList<>();
        int n = player.getWorld().random.nextFloat() > 0.75f ? 9 : 6;
        for (int i = 0; i < n; i++) {
            BulletLeadEntity bullet = new BulletLeadEntity(Z7Entities.BULLET_LEAD_ENTITY, player.getWorld());
            bullet.setHitBoxExpansion(0.0f);
            bullet = modifyBulletEntity(bullet, player, gunStack, ammoStack);
            bullets.add(bullet);
        }
        return List.copyOf(bullets);
    }
}
