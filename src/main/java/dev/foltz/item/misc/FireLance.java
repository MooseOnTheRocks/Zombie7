package dev.foltz.item.misc;

import dev.foltz.Zombie7;
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
import net.minecraft.world.World;

import java.util.Map;

import static dev.foltz.Z7Util.*;
import static dev.foltz.Z7Util.RED;

public class FireLance extends GunStagedItem<FireLance> {
    public static final String STAGE_DEFAULT = "default";
    public static final String STAGE_BROKEN = "broken";
    public static final String STAGE_RELOADING = "reloading";
    public static final String STAGE_FIRING = "firing";

    public FireLance() {
        super(20, AmmoCategories.AMMO_CATEGORY_GUNPOWDER, 1, Map.of(
            STAGE_DEFAULT, new GunStageBuilder<>()
                .barColor(stack -> ORANGE)
                .onInit(tryReloadInit(STAGE_RELOADING))
                .onPressShoot(tryShootOrReload(STAGE_FIRING, STAGE_RELOADING))
                .onPressReload(tryReload(STAGE_RELOADING)),

            STAGE_RELOADING, new GunStageBuilder<>(ticksFromSeconds(3f))
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

            STAGE_FIRING, new GunStageBuilder<>(ticksFromSeconds(8f)).tickWhileUnselected()
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
        entity.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.PLAYERS, 1.0f, -2.0f);
    }

    @Override
    public boolean shootGun(ItemStack itemStack, World world, Entity entity) {
        if (!(entity instanceof PlayerEntity player)) {
            return false;
        }

//        System.out.println("Shooting gun!");

        boolean isCreative = player.getAbilities().creativeMode;
        ItemStack ammoStack = popNextBullet(itemStack);

        if (ammoStack.isEmpty()) {
            return false;
        }

        if (!world.isClient) {
//            createBulletEntities(player, itemStack, ammoStack).forEach(world::spawnEntity);
//             spawnBulletParticlesFromServer(player, itemStack, ammoStack);
            double yaw = player.getYaw();
            double theta = (yaw / 180d) * Math.PI;
            float d = 0.5f;
            double xx = d * Math.cos(theta) + player.getX();
            double zz = d * Math.sin(theta) + player.getZ();
            Zombie7.LOGGER.debug("Boom!");
            world.createExplosion(player, xx, player.getBodyY(0.75f), zz, 1.0F, World.ExplosionSourceType.MOB);

            if (!isCreative) {
                int damage = itemStack.getDamage() + 1;
                if (damage > itemStack.getMaxDamage()) {
                    damage = itemStack.getMaxDamage();
                }
                itemStack.setDamage(damage);
                if (isBroken(itemStack)) {
                    player.incrementStat(Stats.BROKEN.getOrCreateStat(itemStack.getItem()));
                    player.sendToolBreakStatus(Hand.MAIN_HAND);
                }
            }
        }

        player.incrementStat(Stats.USED.getOrCreateStat(this));

        return true;
    }
}
