package dev.foltz.item.ammo.type;

import dev.foltz.entity.Z7Entities;
import dev.foltz.entity.bullet.BulletGrapeshotEntity;
import dev.foltz.entity.bullet.BulletRubberEntity;
import dev.foltz.entity.misc.CannonBallEntity;
import dev.foltz.item.Z7Items;
import dev.foltz.item.gun.GunStagedItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.List;

import static dev.foltz.Z7Util.identifier;

public class GrapeshotAmmoType implements AmmoType {
    public static final TagKey<Item> AMMO_TYPE_GRAPESHOT = TagKey.of(RegistryKeys.ITEM, identifier("ammo_type_grapeshot"));

    @Override
    public TagKey<Item> getTypeKey() {
        return AMMO_TYPE_GRAPESHOT;
    }

    @Override
    public ItemStack getDefaultItemStack() {
        return Z7Items.ITEM_AMMO_GRAPESHOT.getDefaultStack();
    }


    @Override
    public float getBaseDamage(ItemStack itemStack) {
        return 7f;
    }

    @Override
    public float getBaseSpeed(ItemStack itemStack) {
        return 1f;
    }

    @Override
    public float getBasePreferredRange(ItemStack itemStack) {
        return 32f;
    }

    @Override
    public float getBaseAccuracy(ItemStack itemStack) {
        return 0.5f;
    }

    @Override
    public float getBaseRecoilMagnitude(ItemStack itemStack) {
        return 1f;
    }

    @Override
    public float getBaseRecoilDuration(ItemStack itemStack) {
        return 40f;
    }

    @Override
    public List<BulletGrapeshotEntity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        ArrayList<BulletGrapeshotEntity> bullets = new ArrayList<>();
        int n = player.getWorld().random.nextFloat() > 0.75f ? 9 : 6;
        for (int i = 0; i < n; i++) {
            BulletGrapeshotEntity bullet = new BulletGrapeshotEntity(Z7Entities.BULLET_GRAPESHOT_ENTITY, player.getWorld());
            bullet.setHitBoxExpansion(0.25f);
            bullet = modifyBulletEntity(bullet, player, gunStack, ammoStack);
            bullets.add(bullet);
        }
        return List.copyOf(bullets);
    }
}
