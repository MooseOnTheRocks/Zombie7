package dev.foltz.item.ammo.type;

import dev.foltz.item.Z7Items;

public abstract class AmmoTypes {
    public static final AmmoType AMMO_TYPE_PISTOL_BASIC = new PistolBasicAmmoType();
    public static final AmmoType AMMO_TYPE_MAGNUM_BASIC = new MagnumBasicAmmoType();
    public static final AmmoType AMMO_TYPE_SHOTGUN_BASIC = new ShotgunBasicAmmoType();
    public static final AmmoType AMMO_TYPE_SHOTGUN_RUBBER = new ShotgunRubberAmmoType();
    public static final AmmoType AMMO_TYPE_CANNON_BALL = new CannonBallAmmoType();
    public static final AmmoType AMMO_TYPE_GUNPOWDER = new GunpowderAmmoType();
}
