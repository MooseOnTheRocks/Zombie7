package dev.foltz.item.gun.rifle;

import dev.foltz.item.ammo.AmmoItem;
import dev.foltz.item.gun.GunStageBuilder;
import dev.foltz.item.gun.GunStagedItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.Map;

import static dev.foltz.Z7Util.*;

public class MusketRifleItem extends GunStagedItem {
    public static final String STAGE_DEFAULT = "default";
    public static final String STAGE_BROKEN = "broken";
    public static final String STAGE_RELOADING = "reloading";
    public static final String STAGE_COCKING = "cocking";
    public static final String STAGE_COCKED = "cocked";
    public static final String STAGE_FIRING = "firing";

    public MusketRifleItem() {
        super(5, AmmoItem.AmmoCategory.PISTOL_AMMO, 1, Map.of(
                STAGE_DEFAULT, new GunStageBuilder()
                        .onInit(tryShootOrReloadInit(STAGE_COCKING, STAGE_RELOADING))
                        .onPressShoot(tryShootOrReload(STAGE_COCKING, STAGE_RELOADING))
                        .onPressReload(tryReload(STAGE_RELOADING)),

                STAGE_RELOADING, new GunStageBuilder(ticksFromSeconds(1.5f))
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

                STAGE_COCKING, new GunStageBuilder(ticksFromSeconds(1.0f))
                        .barColor(stack -> GREEN)
                        .onInitDo(view -> view.item.playSoundPreFireStep(view.stack, view.entity))
                        .onReleaseShoot(doCancel(STAGE_DEFAULT))
                        .onPressReload(doCancel(STAGE_DEFAULT))
                        .onLastTick(doReady(STAGE_COCKED))
                        .onUnselected(view -> STAGE_DEFAULT),

                STAGE_COCKED, new GunStageBuilder()
                        .barColor(stack -> ORANGE)
                        .onPressShoot(view -> STAGE_FIRING)
                        .onPressReload(doCancel(STAGE_DEFAULT)),

                STAGE_FIRING, new GunStageBuilder(ticksFromSeconds(0.5f)).tickWhileUnselected()
                        .barColor(stack -> RED)
                        .barProgress(stack -> stack.getItem() instanceof GunStagedItem gun ? (gun.getAmmoInGun(stack).size() + (1 - gun.getStageTicks(stack) / (float) (gun.getMaxStageTicks(stack) == 0 ? 1f : gun.getMaxStageTicks(stack)))) / (float) gun.getMaxAmmoCapacity(stack) : 0f)
                        .onInit(doFire())
                        .onLastTick(view -> view.item.isBroken(view.stack) ? STAGE_BROKEN : STAGE_DEFAULT),

                STAGE_BROKEN, new GunStageBuilder()
                        .barColor(stack -> RED)
                        .barProgress(stack -> 1.0f)));
    }

    @Override
    public float getModifiedBulletAccuracy(ItemStack gunStack, ItemStack bulletStack, float accuracy) {
        return accuracy * 0.8f;
    }
}
