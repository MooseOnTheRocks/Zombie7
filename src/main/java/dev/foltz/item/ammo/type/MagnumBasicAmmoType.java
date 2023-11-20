package dev.foltz.item.ammo.type;

import dev.foltz.entity.bullet.BulletBronzeEntity;
import dev.foltz.entity.Z7Entities;
import dev.foltz.item.CompositeResourceItem;
import dev.foltz.item.Z7Items;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

import static dev.foltz.Z7Util.identifier;

public class MagnumBasicAmmoType implements AmmoType {
    public static final TagKey<Item> AMMO_TYPE_MAGNUM_TAG = TagKey.of(RegistryKeys.ITEM, identifier("ammo_type_magnum_basic"));

    @Override
    public TagKey<Item> getTypeKey() {
        return AMMO_TYPE_MAGNUM_TAG;
    }

    @Override
    public ItemStack getDefaultItemStack() {
        return Z7Items.ITEM_AMMO_MAGNUM.getDefaultStack();
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
    public float getBasePreferredRange(ItemStack itemStack) {
        return 64f;
    }

    @Override
    public float getBaseAccuracy(ItemStack itemStack) {
        return 0.90f;
    }

    @Override
    public float getBaseRecoilMagnitude(ItemStack itemStack) {
        return 1.0f;
    }

    @Override
    public float getBaseRecoilDuration(ItemStack itemStack) {
        return 25f;
    }

    @Override
    public List<BulletBronzeEntity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        BulletBronzeEntity bullet = new BulletBronzeEntity(Z7Entities.BULLET_BRONZE_ENTITY, player.getWorld());
        bullet.setHitBoxExpansion(1.0f);
        bullet = modifyBulletEntity(bullet, player, gunStack, ammoStack);
        return List.of(bullet);
    }
}
