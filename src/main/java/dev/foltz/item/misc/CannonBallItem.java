package dev.foltz.item.misc;

import dev.foltz.Z7Util;
import dev.foltz.entity.Z7Entities;
import dev.foltz.entity.bullet.BulletBronzeEntity;
import dev.foltz.entity.bullet.Z7BulletEntity;
import dev.foltz.entity.misc.BowlingBallGrenadeEntity;
import dev.foltz.entity.misc.CannonBallEntity;
import dev.foltz.item.ammo.AmmoItem;
import dev.foltz.item.gun.GunStagedItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CannonBallItem extends Item implements AmmoItem {
    public CannonBallItem() {
        super(new FabricItemSettings().maxCount(8));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(MutableText.of(Text.of("\"eat my ball\"").getContent()).formatted(Formatting.GRAY, Formatting.ITALIC).append(Text.of(" - Me, right now")));
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity entity, int remainingUseTicks) {
        int usedTime = getMaxUseTime(stack) - remainingUseTicks;
        if (usedTime < Z7Util.ticksFromSeconds(0.25f)) {
            return;
        }

        final int fullThrowTime = Z7Util.ticksFromSeconds(2f);
        float speed = MathHelper.map(Math.min(usedTime, fullThrowTime), 0, fullThrowTime, 0.1f, 0.8f);

        CannonBallEntity g = new CannonBallEntity(Z7Entities.CANNON_BALL_ENTITY, entity.getWorld());
        var grenades = List.of(g);
        for (var grenade : grenades) {
            ItemStack throwStack = new ItemStack(stack.getItem());
            grenade.setItemStack(throwStack);
            grenade.setOwner(entity);
            grenade.setPosition(entity.getX(), entity.getEyeY() - grenade.getHeight() / 2f, entity.getZ());
            grenade.setVelocity(entity, entity.getPitch(), entity.getYaw(), 0.0f, speed);
            world.spawnEntity(grenade);
        }

        entity.playSound(SoundEvents.ENTITY_ENDER_PEARL_THROW, 1.0f, -3f);

        if (!(entity instanceof PlayerEntity player && player.getAbilities().creativeMode)) {
            stack.decrement(1);
        }
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public float getBaseDamage(ItemStack itemStack) {
        return 10f;
    }

    @Override
    public float getBaseSpeed(ItemStack itemStack) {
        return 2f;
    }

    @Override
    public float getBaseRange(ItemStack itemStack) {
        return 32f;
    }

    @Override
    public float getBaseAccuracy(ItemStack itemStack) {
        return 0.9f;
    }

    @Override
    public List<? extends Entity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        CannonBallEntity bullet = new CannonBallEntity(Z7Entities.CANNON_BALL_ENTITY, player.getWorld());
//        bullet = modifyBulletEntity(bullet, player, gunStack, ammoStack);
        bullet.setPosition(player.getX(), player.getEyeY() - bullet.getHeight() / 2f, player.getZ());
        bullet.setOwner(player);

        float totalDamage = getBaseDamage(ammoStack);
        float totalSpeed = getBaseSpeed(ammoStack);
        float baseDistance = getBaseRange(ammoStack);
        float totalAccuracy = getBaseAccuracy(ammoStack);
        if (gunStack.getItem() instanceof GunStagedItem gun) {
            totalDamage = gun.getModifiedBulletDamage(gunStack, ammoStack, totalDamage);
            totalSpeed = gun.getModifiedBulletSpeed(gunStack, ammoStack, totalSpeed);
            baseDistance = gun.getModifiedBulletBaseRange(gunStack, ammoStack, baseDistance);
            totalAccuracy = gun.getModifiedBulletAccuracy(gunStack, ammoStack, totalAccuracy);
        }
        bullet.setDamage(totalDamage);
//        float divergence = determineDivergence(totalAccuracy);

        bullet.setVelocity(player, player.getPitch(), player.getYaw(), 0f, totalSpeed, 1.0f);
//        bullet.setBaseDistance(baseDistance);

        return List.of(bullet);
    }
}
