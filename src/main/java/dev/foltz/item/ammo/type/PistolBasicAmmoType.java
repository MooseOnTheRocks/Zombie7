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

public class PistolBasicAmmoType implements AmmoType {
    public static final TagKey<Item> AMMO_TYPE_PISTOL_TAG = TagKey.of(RegistryKeys.ITEM, identifier("ammo_type_pistol_basic"));

    @Override
    public TagKey<Item> getTypeKey() {
        return AMMO_TYPE_PISTOL_TAG;
    }

    @Override
    public ItemStack getDefaultItemStack() {
        return Z7Items.ITEM_AMMO_PISTOL.getDefaultStack();
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
    public float getBasePreferredRange(ItemStack itemStack) {
        return 32f;
    }

    @Override
    public float getBaseAccuracy(ItemStack itemStack) {
        return 0.85f;
    }

    @Override
    public float getBaseRecoilMagnitude(ItemStack itemStack) {
        return 1.0f;
    }

    @Override
    public float getBaseRecoilDuration(ItemStack itemStack) {
        return 15f;
    }

    @Override
    public List<BulletBronzeEntity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        BulletBronzeEntity bullet = new BulletBronzeEntity(Z7Entities.BULLET_BRONZE_ENTITY, player.getWorld());
        bullet.setHitBoxExpansion(0.5f);
        bullet = modifyBulletEntity(bullet, player, gunStack, ammoStack);
        return List.of(bullet);
    }
}
