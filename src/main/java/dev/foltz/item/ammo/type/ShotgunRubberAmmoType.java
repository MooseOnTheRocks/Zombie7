package dev.foltz.item.ammo.type;

import dev.foltz.entity.Z7Entities;
import dev.foltz.entity.bullet.BulletRubberEntity;
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

public class ShotgunRubberAmmoType implements AmmoType {
    public static final TagKey<Item> AMMO_TYPE_SHOTGUN_RUBBER_TAG = TagKey.of(RegistryKeys.ITEM, identifier("ammo_type_shotgun_rubber"));

    @Override
    public TagKey<Item> getTypeKey() {
        return AMMO_TYPE_SHOTGUN_RUBBER_TAG;
    }

    @Override
    public ItemStack getDefaultItemStack() {
        return Z7Items.ITEM_AMMO_SHOTGUN_RUBBER.getDefaultStack();
    }

    @Override
    public float getBaseDamage(ItemStack itemStack) {
        return 0f;
    }

    @Override
    public float getBaseSpeed(ItemStack itemStack) {
        return 1.25f;
    }

    @Override
    public float getBasePreferredRange(ItemStack itemStack) {
        return 12f;
    }

    @Override
    public float getBaseAccuracy(ItemStack itemStack) {
        return 0.5f;
    }

    @Override
    public float getBaseRecoilMagnitude(ItemStack itemStack) {
        return 0.5f;
    }

    @Override
    public float getBaseRecoilDuration(ItemStack itemStack) {
        return 15f;
    }

    @Override
    public List<BulletRubberEntity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        ArrayList<BulletRubberEntity> bullets = new ArrayList<>();
        int n = player.getWorld().random.nextFloat() > 0.75f ? 9 : 6;
        for (int i = 0; i < n; i++) {
            BulletRubberEntity bullet = new BulletRubberEntity(Z7Entities.BULLET_RUBBER_ENTITY, player.getWorld());
            bullet.setHitBoxExpansion(0.25f);
            bullet = modifyBulletEntity(bullet, player, gunStack, ammoStack);
            bullets.add(bullet);
        }
        return List.copyOf(bullets);
    }
}
