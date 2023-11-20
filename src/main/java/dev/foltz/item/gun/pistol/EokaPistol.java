package dev.foltz.item.gun.pistol;

import dev.foltz.item.ammo.category.AmmoCategories;
import dev.foltz.item.gun.GunStageBuilder;
import dev.foltz.item.gun.GunStagedItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

import java.util.Map;

import static dev.foltz.Z7Util.*;

public class EokaPistol extends GunStagedItem<EokaPistol> {
    public static final String STAGE_DEFAULT = "default";
    public static final String STAGE_BROKEN = "broken";
    public static final String STAGE_RELOADING = "reloading";
    public static final String STAGE_STRIKING = "striking";
    public static final String STAGE_FIRING = "firing";

    public EokaPistol() {
        super(50, AmmoCategories.AMMO_CATEGORY_CANNON_BALL, 1, Map.of(
            STAGE_DEFAULT, new GunStageBuilder<>()
                .barColor(stack -> ORANGE)
                .onInit(tryReloadInit(STAGE_RELOADING))
                .onPressShoot(tryShootOrReload(STAGE_STRIKING, STAGE_RELOADING))
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

            STAGE_STRIKING, new GunStageBuilder<EokaPistol>(ticksFromSeconds(0.4f))
                .barColor(view -> ORANGE)
                .onInit(view -> {
                    view.item.playSoundStrike(view.stack, view.entity);
                    var roll = Math.random();
                    System.out.println("Eoka strike roll: " + roll);
                    if (view.entity instanceof PlayerEntity player) {
                        if (!player.isCreative()) {
                            int damage = view.stack.getDamage() + 1;
                            if (damage > view.stack.getMaxDamage()) {
                                damage = view.stack.getMaxDamage();
                            }
                            view.stack.setDamage(damage);
                            if (view.item.isBroken(view.stack)) {
                                player.incrementStat(Stats.BROKEN.getOrCreateStat(view.item));
                                player.sendToolBreakStatus(Hand.MAIN_HAND);
                                return STAGE_BROKEN;
                            }
                        }
                    }

                    if (roll > 0.7) {
                        return STAGE_FIRING;
                    }
                    else {
                        return STAGE_STRIKING;
                    }
                })
                .onLastTick(view -> STAGE_DEFAULT)
                .onUnselected(view -> STAGE_DEFAULT),

            STAGE_FIRING, new GunStageBuilder<>(ticksFromSeconds(0.8f)).tickWhileUnselected()
                .barColor(stack -> RED)
                .barProgress(stack -> stack.getItem() instanceof GunStagedItem<?> gun ? (gun.getAmmoInGun(stack).size() + (1 - gun.getStageTicks(stack) / (float) (gun.getMaxStageTicks(stack) == 0 ? 1f : gun.getMaxStageTicks(stack)))) / (float) gun.getMaxAmmoCapacity(stack) : 0f)
                .onInit(doFire())
                .onLastTick(view -> view.item.isBroken(view.stack) ? STAGE_BROKEN : STAGE_DEFAULT),

            STAGE_BROKEN, new GunStageBuilder<>()
                .barColor(stack -> RED)
                .barProgress(stack -> 1.0f)));
    }

    @Override
    public void playSoundFireBegin(ItemStack stack, Entity entity) {
        entity.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.8f, 2.0f);
    }

    public void playSoundStrike(ItemStack stack, Entity entity) {
        entity.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.5f, 2.0f);
    }

    @Override
    public float getModifiedBulletAccuracy(ItemStack gunStack, ItemStack bulletStack, float accuracy) {
        return accuracy * 0.5f;
    }

    @Override
    public float getModifiedBulletSpeed(ItemStack gunStack, ItemStack bulletStack, float speed) {
        return speed * 0.8f;
    }

    @Override
    public float getModifiedBulletBaseRange(ItemStack gunStack, ItemStack bulletStack, float range) {
        return 0.7f;
    }
}
