package dev.foltz.item.ammo;

import dev.foltz.entity.Z7BulletBronzeEntity;
import dev.foltz.entity.Z7BulletEntity;
import dev.foltz.item.Z7Items;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

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

    public abstract <T extends Z7BulletEntity> List<T> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack);

    public enum AmmoCategory {
        PISTOL_AMMO(AMMO_TYPE_PISTOL_TAG, Z7Items.ITEM_AMMO_PISTOL),
        SHOTGUN_AMMO(AMMO_TYPE_SHOTGUN_TAG, Z7Items.ITEM_AMMO_SHOTGUN);

        public final TagKey<Item> tag;
        public final Z7AmmoItem defaultItem;

        AmmoCategory(TagKey<Item> ammoCategory, Z7AmmoItem defaultItem) {
            this.tag = ammoCategory;
            this.defaultItem = defaultItem;
        }
    }
}
