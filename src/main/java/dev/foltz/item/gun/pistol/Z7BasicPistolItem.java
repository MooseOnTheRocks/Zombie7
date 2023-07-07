package dev.foltz.item.gun.pistol;

import dev.foltz.Z7Util;
import dev.foltz.item.ammo.Z7AmmoItem;
import dev.foltz.item.gun.Z7SimpleGunItem;
import dev.foltz.item.gun.behavior.Z7OneBulletAtATimeReloadBehavior;
import dev.foltz.item.gun.behavior.Z7StagedShootBehavior;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;

public class Z7BasicPistolItem extends Z7SimpleGunItem {
    private static final int STAGE_READYING = 1;
    private static final int STAGE_READY_ON_RELEASE = 2;
    private static final int STAGE_FIRE_ON_PRESS = 3;

    public Z7BasicPistolItem() {
        super(
            // Gun durability: number of shots before breaking.
            100,
            // Ammo capacity: maximum number of bullets loaded in the gun.
            4,
            // Ammo category: which bullets this gun can use.
            Z7AmmoItem.AmmoCategory.PISTOL_AMMO,
            // Mapping stage -> max usage ticks for that stage (default is 0 ticks for stages not included).
            Map.of(
                GLOBAL_STAGE_RELOADING, Z7Util.ticksFromSeconds(2.5f),
                GLOBAL_STAGE_FIRING, Z7Util.ticksFromSeconds(0.2f),
                STAGE_READYING, Z7Util.ticksFromSeconds(0.55f)
            ),
            // Reloading behavior
            new Z7OneBulletAtATimeReloadBehavior(
                List.of(STAGE_READYING, STAGE_READY_ON_RELEASE, STAGE_FIRE_ON_PRESS),
                List.of()
            ),
            // Shooting behavior
            new Z7StagedShootBehavior(
                List.of(STAGE_READYING),
                false,
                STAGE_READY_ON_RELEASE,
                STAGE_FIRE_ON_PRESS
            )
        );
    }

    @Override
    public float getModifiedBulletAccuracy(ItemStack gunStack, ItemStack bulletStack, float accuracy) {
        return accuracy * 0.90f;
    }
}
