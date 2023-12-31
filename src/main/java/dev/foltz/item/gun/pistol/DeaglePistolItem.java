package dev.foltz.item.gun.pistol;

import dev.foltz.item.ammo.category.AmmoCategories;
import dev.foltz.item.gun.GunStagedItem;
import dev.foltz.item.gun.GunStageBuilder;
import net.minecraft.item.ItemStack;

import java.util.Map;

import static dev.foltz.Z7Util.*;

public class DeaglePistolItem extends GunStagedItem<DeaglePistolItem> {
    public static final int FIRE_WAIT_TICKS = ticksFromSeconds(0.4f);
    public static final String STAGE_DEFAULT = "default";
    public static final String STAGE_BROKEN = "broken";
    public static final String STAGE_RELOADING = "reloading";
    public static final String STAGE_READYING = "readying";
    public static final String STAGE_READY = "ready";
    public static final String STAGE_FIRING = "firing";

    public DeaglePistolItem() {
        super(250, AmmoCategories.AMMO_CATEGORY_MAGNUM, 8, Map.of(
            STAGE_DEFAULT, new GunStageBuilder<>()
                .onInit(tryShootOrReloadInit(STAGE_READYING, STAGE_RELOADING))
                .onPressShoot(tryShootOrReload(STAGE_READYING, STAGE_RELOADING))
                .onPressReload(tryReload(STAGE_RELOADING)),

            STAGE_RELOADING, new GunStageBuilder<>(ticksFromSeconds(2f))
                .barColor(stack -> YELLOW)
                .barProgress(stack -> ((GunStagedItem<?>) stack.getItem()).getStageTicks(stack) / (float) ((GunStagedItem<?>) stack.getItem()).getMaxStageTicks(stack))
                .onInitDo(view -> view.item.playSoundReloadBegin(view.stack, view.entity))
                .onReleaseReload(view -> STAGE_DEFAULT)
                .onReleaseShoot(view -> STAGE_DEFAULT)
                .onLastTick(tryReloadWholeClip(STAGE_DEFAULT))
                .onUnselected(view -> STAGE_DEFAULT),

            STAGE_READYING, new GunStageBuilder<>(ticksFromSeconds(0.75f))
                .onLastTick(view -> {
                    view.playerState.setPressingShoot(false);
                    return doReady(STAGE_READY).handleEvent(view);
                })
                .onReleaseReload(doCancel(STAGE_DEFAULT))
                .onReleaseShoot(doCancel(STAGE_DEFAULT)),

            STAGE_READY, new GunStageBuilder<>()
                .barColor(stack -> ORANGE)
                .onInit(tryShootOrReloadInit(STAGE_FIRING, STAGE_RELOADING))
                .onPressShoot(tryShootOrReload(STAGE_FIRING, STAGE_RELOADING))
                .onPressReload(doCancel(STAGE_DEFAULT)),

            STAGE_FIRING, new GunStageBuilder<>(ticksFromSeconds(0.5f)).tickWhileUnselected()
                .barColor(stack -> RED)
                .barProgress(stack -> stack.getItem() instanceof GunStagedItem<?> gun ? (gun.getAmmoInGun(stack).size() + (1 - gun.getStageTicks(stack) / (float) (gun.getMaxStageTicks(stack) == 0 ? 1f : gun.getMaxStageTicks(stack)))) / (float) gun.getMaxAmmoCapacity(stack) : 0f)
                .onInit(view -> {
                    view.playerState.setPressingShoot(false);
                    return doFire().handleEvent(view);
                })
                .onPressShoot(view -> {
                    if (view.item.getStageTicks(view.stack) <= FIRE_WAIT_TICKS) {
                        return view.stageId;
                    }
                    return tryFireReset(STAGE_FIRING).handleEvent(view);
                })
                .onLastTick(view -> view.item.isBroken(view.stack) ? STAGE_BROKEN : STAGE_READY),

            STAGE_BROKEN, new GunStageBuilder<>()
                .barColor(stack -> RED)
                .barProgress(stack -> 1.0f)));
    }

    @Override
    public float getModifiedBulletAccuracy(ItemStack gunStack, ItemStack bulletStack, float accuracy) {
        return accuracy * 0.90f;
    }
}
