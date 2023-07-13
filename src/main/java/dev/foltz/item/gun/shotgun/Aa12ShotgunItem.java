package dev.foltz.item.gun.shotgun;

import dev.foltz.item.StagedGunItem;
import dev.foltz.item.ammo.Z7AmmoItem;
import dev.foltz.item.stage.GunStageBuilder;
import dev.foltz.item.stage.GunStageView;
import net.minecraft.item.ItemStack;

import java.util.Map;

import static dev.foltz.Z7Util.*;

public class Aa12ShotgunItem extends StagedGunItem {
    public static final String STAGE_DEFAULT = "default";
    public static final String STAGE_BROKEN = "broken";
    public static final String STAGE_RELOADING = "reloading";
    public static final String STAGE_FIRING = "firing";

    public Aa12ShotgunItem() {
        super(360, Z7AmmoItem.AmmoCategory.SHOTGUN_AMMO, 20, Map.of(
            STAGE_DEFAULT, new GunStageBuilder()
                .barColor(stack -> ORANGE)
                .onInit(tryShootOrReloadInit(STAGE_FIRING, STAGE_RELOADING))
                .onPressShoot(tryShootOrReload(STAGE_FIRING, STAGE_RELOADING))
                .onPressReload(tryReload(STAGE_RELOADING)),

            STAGE_RELOADING, new GunStageBuilder(ticksFromSeconds(1.2f))
                .barColor(stack -> YELLOW)
                .barProgress(stack -> ((StagedGunItem) stack.getItem()).getStageTicks(stack) / (float) ((StagedGunItem) stack.getItem()).getMaxStageTicks(stack))
                .onInitDo(view -> view.gun.playSoundReloadBegin(view.stack, view.entity))
                .onReleaseReload(view -> STAGE_DEFAULT)
                .onReleaseShoot(view -> STAGE_DEFAULT)
                .onLastTick(view -> {
                    view.playerState.setPressingShoot(false);
                    return tryReloadWholeClip(STAGE_DEFAULT).handleEvent(view);
                })
                .onUnselected(view -> STAGE_DEFAULT),

            STAGE_FIRING, new GunStageBuilder(ticksFromSeconds(0.45f)).tickWhileUnselected()
                .barColor(stack -> RED)
                .barProgress(stack -> stack.getItem() instanceof StagedGunItem gun ? (gun.getAmmoInGun(stack).size() + (1 - gun.getStageTicks(stack) / (float) (gun.getMaxStageTicks(stack) == 0 ? 1f : gun.getMaxStageTicks(stack)))) / (float) gun.getMaxAmmoCapacity(stack) : 0f)
                .onInit(doFire())
                .onLastTick(view -> view.gun.isBroken(view.stack) ? STAGE_BROKEN : STAGE_DEFAULT),

            STAGE_BROKEN, new GunStageBuilder()
                .barColor(stack -> RED)
                .barProgress(stack -> 1.0f)));
    }

    @Override
    public float getModifiedBulletAccuracy(ItemStack gunStack, ItemStack bulletStack, float accuracy) {
        return accuracy * 0.8f;
    }
}
