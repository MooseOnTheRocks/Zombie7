package dev.foltz.item.ammo.type;

import dev.foltz.entity.bullet.Z7BulletEntity;
import dev.foltz.item.Z7Items;
import dev.foltz.item.gun.GunStagedItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import java.util.List;
import java.util.function.Supplier;

import static dev.foltz.Z7Util.identifier;

public interface AmmoType {
    TagKey<Item> getTypeKey();

    ItemStack getDefaultItemStack();

    float getBaseDamage(ItemStack itemStack);

    float getBaseSpeed(ItemStack itemStack);

    float getBasePreferredRange(ItemStack itemStack);

    float getBaseAccuracy(ItemStack itemStack);

    float getBaseRecoilMagnitude(ItemStack itemStack);

    float getBaseRecoilDuration(ItemStack itemStack);

    List<? extends Entity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack);

    default <T extends Z7BulletEntity> T modifyBulletEntity(T bullet, PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        bullet.setPosition(player.getX(), player.getEyeY() - bullet.getHeight() / 2f, player.getZ());
        bullet.setOwner(player);

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
        float divergence = determineDivergence(totalAccuracy);

        bullet.setVelocity(player, player.getPitch(), player.getYaw(), 0f, totalSpeed, divergence);
        bullet.setBaseDistance(baseDistance);

        return bullet;
    }

    default int findAmmoSlot(PlayerEntity player) {
        var tag = getTypeKey();
        if (player.getMainHandStack().isIn(tag)) {
            return player.getInventory().selectedSlot;
        }
        else if (player.getOffHandStack().isIn(tag)) {
            return PlayerInventory.OFF_HAND_SLOT;
        }
        else {
            for (int slot = 0; slot < player.getInventory().main.size(); slot++) {
                ItemStack stack = player.getInventory().getStack(slot);
                if (stack.isIn(tag)) {
                    return slot;
                }
            }
        }
        return -1;
    }

    public static float determineDivergence(float totalAccuracy) {
             if (totalAccuracy >= 0.95f) return 0f;
        else if (totalAccuracy >= 0.90f) return 0.25f;
        else if (totalAccuracy >= 0.80f) return 0.75f;
        else if (totalAccuracy >= 0.70f) return 1f;
        else if (totalAccuracy >= 0.60f) return 3f;
        else if (totalAccuracy >= 0.50f) return 6f;
        else if (totalAccuracy >= 0.45f) return 8f;
        else if (totalAccuracy >= 0.40f) return 10f;
        else if (totalAccuracy >= 0.30f) return 15f;
        else if (totalAccuracy >= 0.20f) return 20f;
        else if (totalAccuracy >= 0.10f) return 25f;
        else                             return 50f;
    }
}
