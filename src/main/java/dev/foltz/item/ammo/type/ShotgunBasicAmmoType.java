package dev.foltz.item.ammo.type;

import dev.foltz.entity.bullet.BulletLeadEntity;
import dev.foltz.entity.Z7Entities;
import dev.foltz.item.CompositeResourceItem;
import dev.foltz.item.Z7Items;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.List;

import static dev.foltz.Z7Util.identifier;

public class ShotgunBasicAmmoType implements AmmoType {
    public static final TagKey<Item> AMMO_TYPE_SHOTGUN_TAG = TagKey.of(RegistryKeys.ITEM, identifier("ammo_type_shotgun_basic"));

    @Override
    public TagKey<Item> getTypeKey() {
        return AMMO_TYPE_SHOTGUN_TAG;
    }

    @Override
    public ItemStack getDefaultItemStack() {
        return Z7Items.ITEM_AMMO_SHOTGUN.getDefaultStack();
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
    public float getBasePreferredRange(ItemStack itemStack) {
        return 10f;
    }

    @Override
    public float getBaseAccuracy(ItemStack itemStack) {
        return 0.6f;
    }

    @Override
    public float getBaseRecoilMagnitude(ItemStack itemStack) {
        return 0;
    }

    @Override
    public float getBaseRecoilDuration(ItemStack itemStack) {
        return 0;
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
