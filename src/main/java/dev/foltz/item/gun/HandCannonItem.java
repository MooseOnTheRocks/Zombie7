package dev.foltz.item.gun;

import dev.foltz.item.ammo.category.AmmoCategories;
import net.minecraft.util.math.MathHelper;

import java.util.Map;

import static dev.foltz.Z7Util.*;

public class HandCannonItem extends GunStagedItem<HandCannonItem> {
    public static final String STAGE_DEFAULT = "default";
    public static final String STAGE_RELOADING = "reloading";
    public static final String STAGE_FUSE_LIT = "fuse_lit";
    public static final String STAGE_FIRING = "firing";
    public static final String STAGE_BROKEN = "broken";

    public HandCannonItem() {
        super(50, AmmoCategories.AMMO_CATEGORY_HAND_CANNON, 1, Map.of(
            STAGE_DEFAULT, new GunStageBuilder<>()
                .barColor(stack -> ORANGE)
                .onInit(tryReloadInit(STAGE_RELOADING))
                .onPressShoot(tryShootOrReload(STAGE_FUSE_LIT, STAGE_RELOADING))
                .onPressReload(tryReload(STAGE_RELOADING)),

            STAGE_RELOADING, new GunStageBuilder<>(ticksFromSeconds(2f))
                .barColor(stack -> YELLOW)
                .barProgress(stack -> ((GunStagedItem<?>) stack.getItem()).getStageTicks(stack) / (float) ((GunStagedItem<?>) stack.getItem()).getMaxStageTicks(stack))
                .onInitDo(view -> {
                    view.item.playSoundReloadBegin(view.stack, view.entity);
                    int usageStage = (int) MathHelper.map(view.item.getAmmoInGun(view.stack).size(), 0, view.item.getMaxAmmoCapacity(view.stack), 0, view.item.getMaxStageTicks(view.stack));
                    view.item.setStageTicks(view.stack, usageStage);
                })
                .onReleaseReload(view -> STAGE_DEFAULT)
                .onReleaseShoot(view -> STAGE_DEFAULT)
                .onTick(tryReloadOneBullet(STAGE_DEFAULT))
                .onUnselected(view -> STAGE_DEFAULT),

            STAGE_FUSE_LIT, new GunStageBuilder<>(ticksFromSeconds(2.5f)).tickWhileUnselected()
                .barColor(stack -> RED)
                .barProgress(stack ->
                    stack.getItem() instanceof GunStagedItem<?> gun
                        ? (gun.getStageTicks(stack) / (gun.getMaxStageTicks(stack) == 0 ? 1f : gun.getMaxStageTicks(stack)))
                        : 1f)
                .onPressReload(view -> STAGE_DEFAULT)
                .onLastTick(view -> STAGE_FIRING),

            STAGE_FIRING, new GunStageBuilder<>(ticksFromSeconds(1.0f)).tickWhileUnselected()
                .barColor(stack -> RED)
                .barProgress(stack -> stack.getItem() instanceof GunStagedItem<?> gun ? (gun.getAmmoInGun(stack).size() + (1 - gun.getStageTicks(stack) / (float) (gun.getMaxStageTicks(stack) == 0 ? 1f : gun.getMaxStageTicks(stack)))) / (float) gun.getMaxAmmoCapacity(stack) : 0f)
                .onInit(doFire())
                .onLastTick(view -> view.item.isBroken(view.stack) ? STAGE_BROKEN : STAGE_DEFAULT),

            STAGE_BROKEN, new GunStageBuilder<>()
                .barColor(stack -> RED)
                .barProgress(stack -> 1.0f)
        ));
    }
}
