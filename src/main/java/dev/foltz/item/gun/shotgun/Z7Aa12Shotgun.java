package dev.foltz.item.gun.shotgun;

import dev.foltz.Z7Util;
import dev.foltz.item.ammo.Z7AmmoItem;
import dev.foltz.item.gun.Z7SimpleGunItem;
import dev.foltz.item.gun.behavior.Z7FullAutoShootBehavior;
import dev.foltz.item.gun.behavior.Z7WholeClipReloadBehavior;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;

public class Z7Aa12Shotgun extends Z7SimpleGunItem {
    private static final int STAGE_READY_ON_RELEASE = 1;
    private static final int STAGE_FIRE_ON_PRESS = 2;

    public Z7Aa12Shotgun() {
        super(360, 20, Z7AmmoItem.AmmoCategory.SHOTGUN_AMMO,
            Map.of(
                GLOBAL_STAGE_RELOADING, Z7Util.ticksFromSeconds(1.2f),
                GLOBAL_STAGE_FIRING, Z7Util.ticksFromSeconds(0.35f)
            ),
            new Z7WholeClipReloadBehavior(
                STAGE_READY_ON_RELEASE,
                List.of(),
                List.of(STAGE_FIRE_ON_PRESS)
            ),
            new Z7FullAutoShootBehavior(
                List.of(),
                false,
                STAGE_READY_ON_RELEASE,
                STAGE_FIRE_ON_PRESS
            )
        );
    }

    @Override
    public float getModifiedBulletAccuracy(ItemStack gunStack, ItemStack bulletStack, float accuracy) {
        return accuracy * 0.8f;
    }
}
