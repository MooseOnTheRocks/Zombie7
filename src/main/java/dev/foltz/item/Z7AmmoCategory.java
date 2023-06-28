package dev.foltz.item;

import dev.foltz.Zombie7;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

public enum Z7AmmoCategory {
    PISTOL_AMMO(Zombie7.AMMO_TYPE_PISTOL, Z7Items.ITEM_AMMO_PISTOL),
    SHOTGUN_AMMO(Zombie7.AMMO_TYPE_SHOTGUN, Z7Items.ITEM_AMMO_SHOTGUN);

    public final TagKey<Item> tag;
    public final Z7AmmoItem defaultItem;
    private Z7AmmoCategory(TagKey<Item> ammoCategory, Z7AmmoItem defaultItem) {
        this.tag = ammoCategory;
        this.defaultItem = defaultItem;
    }
}
