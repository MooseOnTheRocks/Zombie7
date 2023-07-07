package dev.foltz.item.gun.rifle;

import dev.foltz.Z7Util;
import dev.foltz.item.ammo.Z7AmmoItem;
import dev.foltz.item.gun.Z7SimpleGunItem;
import dev.foltz.item.gun.behavior.Z7FullAutoShootBehavior;
import dev.foltz.item.gun.behavior.Z7WholeClipReloadBehavior;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;

public class Z7AkRifleItem extends Z7SimpleGunItem {
    private static final int STAGE_READYING = 1;
    private static final int STAGE_READY_ON_RELEASE = 2;
    private static final int STAGE_FIRE_ON_PRESS = 3;

    public Z7AkRifleItem() {
        super(420, 30, Z7AmmoItem.AmmoCategory.PISTOL_AMMO,
            Map.of(
                STAGE_READYING, Z7Util.ticksFromSeconds(0.5f),
                GLOBAL_STAGE_RELOADING, Z7Util.ticksFromSeconds(2.5f),
                GLOBAL_STAGE_FIRING, Z7Util.ticksFromSeconds(0.20f)
            ),
            new Z7WholeClipReloadBehavior(
                GLOBAL_STAGE_DEFAULT,
                List.of(STAGE_READYING, STAGE_READY_ON_RELEASE, STAGE_FIRE_ON_PRESS),
                List.of()
            ),
            new Z7FullAutoShootBehavior(
                List.of(STAGE_READYING),
                false,
                STAGE_READY_ON_RELEASE,
                STAGE_FIRE_ON_PRESS
            )
        );
    }

    @Override
    public float getModifiedBulletBaseRange(ItemStack gunStack, ItemStack bulletStack, float range) {
        return range + 32f;
    }

    @Override
    public float getModifiedBulletAccuracy(ItemStack gunStack, ItemStack bulletStack, float accuracy) {
        return accuracy;
    }

    @Override
    public float getModifiedBulletSpeed(ItemStack gunStack, ItemStack bulletStack, float speed) {
        return super.getModifiedBulletSpeed(gunStack, bulletStack, speed);
    }

    @Override
    public float getModifiedBulletDamage(ItemStack gunStack, ItemStack bulletStack, float damage) {
        return damage + 1.5f;
    }
}
