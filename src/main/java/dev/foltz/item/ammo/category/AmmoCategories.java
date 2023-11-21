package dev.foltz.item.ammo.category;

import static dev.foltz.item.ammo.type.AmmoTypes.*;

public abstract class AmmoCategories {
    public static final AmmoCategory AMMO_CATEGORY_PISTOL = new AmmoCategory(AMMO_TYPE_PISTOL_BASIC);
    public static final AmmoCategory AMMO_CATEGORY_MAGNUM = new AmmoCategory(AMMO_TYPE_MAGNUM_BASIC);
    public static final AmmoCategory AMMO_CATEGORY_RIFLE = new AmmoCategory(AMMO_TYPE_PISTOL_BASIC);
    public static final AmmoCategory AMMO_CATEGORY_SHOTGUN = new AmmoCategory(AMMO_TYPE_SHOTGUN_BASIC, AMMO_TYPE_SHOTGUN_RUBBER);
    public static final AmmoCategory AMMO_CATEGORY_CANNON_BALL = new AmmoCategory(AMMO_TYPE_CANNON_BALL);
    public static final AmmoCategory AMMO_CATEGORY_HAND_CANNON = new AmmoCategory(AMMO_TYPE_CANNON_BALL, AMMO_TYPE_GRAPESHOT);
    public static final AmmoCategory AMMO_CATEGORY_GUNPOWDER = new AmmoCategory(AMMO_TYPE_GUNPOWDER);
}
