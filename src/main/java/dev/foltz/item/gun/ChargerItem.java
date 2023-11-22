package dev.foltz.item.gun;

import dev.foltz.item.ammo.category.AmmoCategories;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.Map;

import static dev.foltz.Z7Util.*;
import static dev.foltz.Z7Util.RED;

public class ChargerItem extends GunStagedItem<ChargerItem> {
    public static final Identifier CHARGER_CROSSHAIR = identifier("textures/gui/spinning_crosshair.png");
    public static final String STAGE_DEFAULT = "default";
    public static final String STAGE_CHARGING = "charging";
    public static final String STAGE_FIRING = "firing";
    public static final String STAGE_BROKEN = "broken";

    public ChargerItem() {
        super(50, AmmoCategories.AMMO_CATEGORY_NONE, 0, Map.of(
            STAGE_DEFAULT, new GunStageBuilder<>()
                .barColor(stack -> ORANGE)
                .onPressShoot(view -> STAGE_CHARGING),

            STAGE_CHARGING, new GunStageBuilder<>(ticksFromSeconds(2.5f))
                .barColor(stack -> ORANGE)
                .barProgress(stack ->
                    stack.getItem() instanceof GunStagedItem<?> gun
                        ? (gun.getStageTicks(stack) / (gun.getMaxStageTicks(stack) == 0 ? 1f : gun.getMaxStageTicks(stack)))
                        : 1f)
                .onReleaseShoot(view -> STAGE_DEFAULT)
                .onPressReload(view -> STAGE_DEFAULT)
                .onUnselected(view -> STAGE_DEFAULT)
                .onLastTick(view -> STAGE_FIRING),

            STAGE_FIRING, new GunStageBuilder<ChargerItem>(ticksFromSeconds(5.0f)).tickWhileUnselected()
                .barColor(stack -> RED)
                .barProgress(stack ->
                    stack.getItem() instanceof GunStagedItem<?> gun
                        ? (1 - gun.getStageTicks(stack) / (gun.getMaxStageTicks(stack) == 0 ? 1f : gun.getMaxStageTicks(stack)))
                        : 1f)
                .onInit(doFire())
                .onLastTick(view -> view.item.isBroken(view.stack) ? STAGE_BROKEN : STAGE_DEFAULT),

            STAGE_BROKEN, new GunStageBuilder<>()
                .barColor(stack -> RED)
                .barProgress(stack -> 1.0f)
        ));
    }

    @Override
    public boolean shootGun(ItemStack itemStack, World world, Entity entity) {
        if (!(entity instanceof PlayerEntity player)) {
            return false;
        }

        boolean isCreative = player.getAbilities().creativeMode;

        if (!world.isClient) {
//            createBulletEntities(player, itemStack, ammoStack).forEach(world::spawnEntity);
            // spawnBulletParticlesFromServer(player, itemStack, ammoStack);

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

            Vector3f color = new Vector3f(1.0f, 0.0f, 0.0f);
            float scale = 1.0f;
            float x = (float) player.getX();
            float y = (float) player.getY();
            float z = (float) player.getZ();
            float speed = 0.5f;
//            System.out.println("pitch, yaw = " + player.getPitch() + ", " + player.getYaw());
            float pitch = (float) Math.toRadians(player.getPitch());
            float yaw = (float) Math.toRadians(player.getYaw());
            var normalizedDirection = Vec3d.fromPolar(pitch, yaw);
//            normalizedDirection.multiply(speed);
            float vx = (float) normalizedDirection.getX();
            float vy = (float) normalizedDirection.getY();
            float vz = (float) normalizedDirection.getZ();
            ((ServerWorld) world).spawnParticles(new DustParticleEffect(color, scale), x, y, z, 10, vx, vy, vz, speed);
//            world.addParticle(new DustParticleEffect(color, scale), true, x, y, z, vx, vy, vz);
        }

        player.incrementStat(Stats.USED.getOrCreateStat(this));

        return true;
    }

    @Override
    public float getAimingZoomModifier() {
        return 0.3f;
    }

    @Override
    public float getAimingTimeModifier() {
        return 60f;
    }

    @Override
    public Identifier getCrosshairTexture() {
        return CHARGER_CROSSHAIR;
    }

    @Override
    public int getCrosshairFrameIndex(float currentAccuracyValue, PlayerEntity player) {
        if (player.isUsingItem()) {
            final float timeInSeconds = 1.5f;
            final float timeInMilliseconds = 1000f * timeInSeconds;
            float time = (System.currentTimeMillis() % ((int) timeInMilliseconds)) / timeInMilliseconds;
            final int frameCount = 6;
            float continuousIndex = MathHelper.clamp(time, 0f, 1f);
            return 16 + (int) Math.ceil(continuousIndex * (frameCount - 1));
        }
        else {
            final int frameCount = 16;
            float continuousIndex = MathHelper.clamp(currentAccuracyValue, 0f, 1f);
            return (int) Math.ceil(continuousIndex * (frameCount - 1));
        }
    }
}
