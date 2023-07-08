package dev.foltz.item.ammo;

import dev.foltz.entity.Z7BulletBronzeEntity;
import dev.foltz.entity.Z7BulletEntity;
import dev.foltz.item.Z7Items;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import java.util.List;
import java.util.function.Supplier;

import static dev.foltz.Z7Util.identifier;

public abstract class Z7AmmoItem extends Item {
    public static final TagKey<Item> AMMO_TYPE_PISTOL_TAG = TagKey.of(RegistryKeys.ITEM, identifier("ammo_type_pistol"));
    public static final TagKey<Item> AMMO_TYPE_SHOTGUN_TAG = TagKey.of(RegistryKeys.ITEM, identifier("ammo_type_shotgun"));

    public Z7AmmoItem(Settings settings) {
        super(settings);
    }

    public abstract float getBaseDamage(ItemStack itemStack);

    public abstract float getBaseSpeed(ItemStack itemStack);

    public abstract float getBaseRange(ItemStack itemStack);

    public abstract float getBaseAccuracy(ItemStack itemStack);

    public abstract List<? extends Z7BulletEntity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack);

    public enum AmmoCategory {
        PISTOL_AMMO(AMMO_TYPE_PISTOL_TAG, () -> Z7Items.ITEM_AMMO_PISTOL),
        SHOTGUN_AMMO(AMMO_TYPE_SHOTGUN_TAG, () -> Z7Items.ITEM_AMMO_SHOTGUN);

        public final TagKey<Item> tag;
        public final Supplier<Z7AmmoItem> defaultItem;

        AmmoCategory(TagKey<Item> ammoCategory, Supplier<Z7AmmoItem> defaultItem) {
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
