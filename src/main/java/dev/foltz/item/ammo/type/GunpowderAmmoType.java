package dev.foltz.item.ammo.type;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

import static dev.foltz.Z7Util.identifier;

public class GunpowderAmmoType implements AmmoType {
    public static final TagKey<Item> AMMO_TYPE_GUNPOWDER = TagKey.of(RegistryKeys.ITEM, identifier("ammo_type_gunpowder"));

    @Override
    public TagKey<Item> getTypeKey() {
        return AMMO_TYPE_GUNPOWDER;
    }

    @Override
    public ItemStack getDefaultItemStack() {
        return Items.GUNPOWDER.getDefaultStack();
    }

    @Override
    public float getBaseDamage(ItemStack itemStack) {
        return 0;
    }

    @Override
    public float getBaseSpeed(ItemStack itemStack) {
        return 0;
    }

    @Override
    public float getBasePreferredRange(ItemStack itemStack) {
        return 0;
    }

    @Override
    public float getBaseAccuracy(ItemStack itemStack) {
        return 0;
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
    public List<? extends Entity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        return List.of();
    }
}
