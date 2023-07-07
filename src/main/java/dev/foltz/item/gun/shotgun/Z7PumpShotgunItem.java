package dev.foltz.item.gun.shotgun;

import dev.foltz.Z7Util;
import dev.foltz.entity.Z7BulletEntity;
import dev.foltz.item.ammo.Z7AmmoItem;
import dev.foltz.item.gun.Z7SimpleGunItem;
import dev.foltz.item.gun.behavior.Z7OneBulletAtATimeReloadBehavior;
import dev.foltz.item.gun.behavior.Z7StagedShootBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;

public class Z7PumpShotgunItem extends Z7SimpleGunItem {
    private static final int STAGE_PUMPING_UP = 1;
    private static final int STAGE_PUMPING_DOWN = 2;
    private static final int STAGE_READY_ON_RELEASE = 3;
    private static final int STAGE_FIRE_ON_PRESS = 4;

    public Z7PumpShotgunItem() {
        super(100, 4, Z7AmmoItem.AmmoCategory.SHOTGUN_AMMO,
            Map.of(
                GLOBAL_STAGE_RELOADING, Z7Util.ticksFromSeconds(3f),
                GLOBAL_STAGE_FIRING, Z7Util.ticksFromSeconds(0.4f),
                STAGE_PUMPING_UP, Z7Util.ticksFromSeconds(0.25f),
                STAGE_PUMPING_DOWN, Z7Util.ticksFromSeconds(0.5f)
            ),
            new Z7OneBulletAtATimeReloadBehavior(
                List.of(STAGE_PUMPING_DOWN, STAGE_READY_ON_RELEASE, STAGE_FIRE_ON_PRESS),
                List.of(STAGE_PUMPING_UP)
            ),
            new Z7StagedShootBehavior(
                List.of(STAGE_PUMPING_UP, STAGE_PUMPING_DOWN),
                true,
                STAGE_READY_ON_RELEASE,
                STAGE_FIRE_ON_PRESS
            )
        );
    }

    @Override
    public float getModifiedBulletAccuracy(ItemStack gunStack, ItemStack bulletStack, float accuracy) {
        return accuracy * 0.9f;
    }

    public boolean isPumpingDown(ItemStack stack) {
        return getGunStage(stack) == STAGE_PUMPING_DOWN;
    }
}
