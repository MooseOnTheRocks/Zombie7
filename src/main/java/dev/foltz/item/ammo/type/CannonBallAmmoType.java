package dev.foltz.item.ammo.type;

import dev.foltz.entity.Z7Entities;
import dev.foltz.entity.misc.CannonBallEntity;
import dev.foltz.item.Z7Items;
import dev.foltz.item.gun.GunStagedItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

import static dev.foltz.Z7Util.identifier;

public class CannonBallAmmoType implements AmmoType {
    public static final TagKey<Item> AMMO_TYPE_CANNON_BALL = TagKey.of(RegistryKeys.ITEM, identifier("ammo_type_cannon_ball"));

    @Override
    public TagKey<Item> getTypeKey() {
        return AMMO_TYPE_CANNON_BALL;
    }

    @Override
    public ItemStack getDefaultItemStack() {
        return Z7Items.ITEM_CANNON_BALL.getDefaultStack();
    }


    @Override
    public float getBaseDamage(ItemStack itemStack) {
        return 10f;
    }

    @Override
    public float getBaseSpeed(ItemStack itemStack) {
        return 2f;
    }

    @Override
    public float getBasePreferredRange(ItemStack itemStack) {
        return 32f;
    }

    @Override
    public float getBaseAccuracy(ItemStack itemStack) {
        return 0.9f;
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
    public List<? extends Entity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        CannonBallEntity bullet = new CannonBallEntity(Z7Entities.CANNON_BALL_ENTITY, player.getWorld());
//        bullet = modifyBulletEntity(bullet, player, gunStack, ammoStack);
        bullet.setPosition(player.getX(), player.getEyeY() - bullet.getHeight() / 2f, player.getZ());
        bullet.setOwner(player);
        bullet.setItemStack(ammoStack.copyWithCount(1));

        float totalDamage = getBaseDamage(ammoStack);
        float totalSpeed = getBaseSpeed(ammoStack);
        float baseDistance = getBasePreferredRange(ammoStack);
        float totalAccuracy = getBaseAccuracy(ammoStack);
        if (gunStack.getItem() instanceof GunStagedItem<?> gun) {
            totalDamage = gun.getModifiedBulletDamage(gunStack, ammoStack, totalDamage);
            totalSpeed = gun.getModifiedBulletSpeed(gunStack, ammoStack, totalSpeed);
            baseDistance = gun.getModifiedBulletBaseRange(gunStack, ammoStack, baseDistance);
            totalAccuracy = gun.getModifiedBulletAccuracy(gunStack, ammoStack, totalAccuracy);
        }
        bullet.setDamage(totalDamage);
//        float divergence = determineDivergence(totalAccuracy);

        bullet.setVelocity(player, player.getPitch(), player.getYaw(), 0f, totalSpeed, 1.0f);
//        bullet.setBaseDistance(baseDistance);

        return List.of(bullet);
    }
}
