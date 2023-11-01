package dev.foltz.item.ammo;

import dev.foltz.entity.Z7Entities;
import dev.foltz.entity.bullet.BulletBronzeEntity;
import dev.foltz.entity.bullet.Z7BulletEntity;
import dev.foltz.item.CompositeResourceItem;
import dev.foltz.item.Z7Items;
import dev.foltz.item.gun.GunStagedItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import java.util.List;
import java.util.function.Supplier;

import static dev.foltz.Z7Util.identifier;

public abstract class AmmoItem extends CompositeResourceItem {
    public static final TagKey<Item> AMMO_TYPE_PISTOL_TAG = TagKey.of(RegistryKeys.ITEM, identifier("ammo_type_pistol"));
    public static final TagKey<Item> AMMO_TYPE_MAGNUM_TAG = TagKey.of(RegistryKeys.ITEM, identifier("ammo_type_magnum"));
    public static final TagKey<Item> AMMO_TYPE_SHOTGUN_TAG = TagKey.of(RegistryKeys.ITEM, identifier("ammo_type_shotgun"));

    public AmmoItem(Settings settings) {
        super(settings);
    }

    public abstract float getBaseDamage(ItemStack itemStack);

    public abstract float getBaseSpeed(ItemStack itemStack);

    public abstract float getBaseRange(ItemStack itemStack);

    public abstract float getBaseAccuracy(ItemStack itemStack);

    public abstract List<? extends Z7BulletEntity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack);

    public <T extends Z7BulletEntity> T modifyBulletEntity(T bullet, PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
//        BulletBronzeEntity bullet = new BulletBronzeEntity(Z7Entities.BULLET_BRONZE_ENTITY, player.world);
//        bullet.setHitBoxExpansion(1.0f);
        bullet.setPosition(player.getX(), player.getEyeY() - bullet.getHeight() / 2f, player.getZ());
        bullet.setOwner(player);

        float totalDamage = getBaseDamage(ammoStack);
        float totalSpeed = getBaseSpeed(ammoStack);
        float baseDistance = getBaseRange(ammoStack);
        float totalAccuracy = getBaseAccuracy(ammoStack);
        if (gunStack.getItem() instanceof GunStagedItem gun) {
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

    public enum AmmoCategory {
        PISTOL_AMMO(AMMO_TYPE_PISTOL_TAG, () -> Z7Items.ITEM_AMMO_PISTOL),
        MAGNUM_AMMO(AMMO_TYPE_MAGNUM_TAG, () -> Z7Items.ITEM_AMMO_MAGNUM),
        SHOTGUN_AMMO(AMMO_TYPE_SHOTGUN_TAG, () -> Z7Items.ITEM_AMMO_SHOTGUN);



        public final TagKey<Item> tag;
        public final Supplier<AmmoItem> defaultItem;

        AmmoCategory(TagKey<Item> ammoCategory, Supplier<AmmoItem> defaultItem) {
            this.tag = ammoCategory;
            this.defaultItem = defaultItem;
        }

        public int findAmmoSlot(PlayerEntity player) {
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
    }
}
