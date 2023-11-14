package dev.foltz.item.gun.shotgun;

import dev.foltz.item.gun.GunStagedItem;
import dev.foltz.item.ammo.AmmoItem;
import dev.foltz.item.gun.GunStageBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.Map;

import static dev.foltz.Z7Util.*;

public class PumpShotgunItem extends GunStagedItem<PumpShotgunItem> {
    public static final String STAGE_DEFAULT = "default";
    public static final String STAGE_BROKEN = "broken";
    public static final String STAGE_RELOADING = "reloading";
    public static final String STAGE_PUMPING_DOWN = "pumping";
    public static final String STAGE_PUMPED_DOWN = "pumped_down";
    public static final String STAGE_PUMPING_UP = "pumping_down";
    public static final String STAGE_PUMPED = "pumped";
    public static final String STAGE_FIRING = "firing";

    public PumpShotgunItem() {
        super(80, AmmoItem.AmmoCategory.SHOTGUN_AMMO, 4, Map.of(
            STAGE_DEFAULT, new GunStageBuilder<>()
                .onInit(tryShootOrReloadInit(STAGE_PUMPING_DOWN, STAGE_RELOADING))
                .onPressShoot(tryShootOrReload(STAGE_PUMPING_DOWN, STAGE_RELOADING))
                .onPressReload(tryReload(STAGE_RELOADING)),

            STAGE_RELOADING, new GunStageBuilder<>(ticksFromSeconds(2.5f))
                .barColor(stack -> YELLOW)
                .barProgress(stack -> ((GunStagedItem) stack.getItem()).getStageTicks(stack) / (float) ((GunStagedItem) stack.getItem()).getMaxStageTicks(stack))
                .onInitDo(view -> {
                    view.item.playSoundReloadBegin(view.stack, view.entity);
                    int usageStage = (int) MathHelper.map(view.item.getAmmoInGun(view.stack).size(), 0, view.item.getMaxAmmoCapacity(view.stack), 0, view.item.getMaxStageTicks(view.stack));
                    view.item.setStageTicks(view.stack, usageStage);
                })
                .onReleaseReload(view -> STAGE_DEFAULT)
                .onReleaseShoot(view -> STAGE_DEFAULT)
                .onTick(tryReloadOneBullet(STAGE_DEFAULT))
                .onUnselected(view -> STAGE_DEFAULT),

            STAGE_PUMPING_DOWN, new GunStageBuilder<>(ticksFromSeconds(0.25f))
                .onReleaseShoot(doCancel(STAGE_DEFAULT))
                .onPressReload(doCancel(STAGE_DEFAULT))
                .onLastTick(doReady(STAGE_PUMPING_UP))
                .onUnselected(view -> STAGE_DEFAULT),

            STAGE_PUMPED_DOWN, new GunStageBuilder<>()
                .onPressReload(doCancel(STAGE_DEFAULT))
                .onPressShoot(view -> STAGE_PUMPING_UP),

            STAGE_PUMPING_UP, new GunStageBuilder<>(ticksFromSeconds(0.4f))
                .onInit(doReady(STAGE_PUMPING_UP))
                .onReleaseShoot(doCancel(STAGE_PUMPED_DOWN))
                .onPressReload(doCancel(STAGE_DEFAULT))
                .onLastTick(doReady(STAGE_PUMPED))
                .onUnselected(view -> STAGE_PUMPED_DOWN),

            STAGE_PUMPED, new GunStageBuilder<>()
                .barColor(stack -> ORANGE)
                .onPressShoot(view -> STAGE_FIRING)
                .onPressReload(doCancel(STAGE_DEFAULT)),

            STAGE_FIRING, new GunStageBuilder<>(ticksFromSeconds(0.33f)).tickWhileUnselected()
                .barColor(stack -> RED)
                .barProgress(stack -> stack.getItem() instanceof GunStagedItem gun ? (gun.getAmmoInGun(stack).size() + (1 - gun.getStageTicks(stack) / (float) (gun.getMaxStageTicks(stack) == 0 ? 1f : gun.getMaxStageTicks(stack)))) / (float) gun.getMaxAmmoCapacity(stack) : 0f)
                .onInit(doFire())
                .onLastTick(view -> view.item.isBroken(view.stack) ? STAGE_BROKEN : STAGE_DEFAULT),

            STAGE_BROKEN, new GunStageBuilder<>()
                .barColor(stack -> RED)
                .barProgress(stack -> 1.0f)));
    }

    @Override
    public float getModifiedBulletAccuracy(ItemStack gunStack, ItemStack bulletStack, float accuracy) {
        return accuracy * 0.9f;
    }
}
