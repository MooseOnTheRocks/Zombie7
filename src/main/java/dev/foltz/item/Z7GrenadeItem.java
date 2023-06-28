package dev.foltz.item;

import dev.foltz.Z7Util;
import dev.foltz.Zombie7;
import dev.foltz.entity.Z7GrenadeEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class Z7GrenadeItem extends Item {
    public Z7GrenadeItem() {
        super(new FabricItemSettings().maxCount(16));
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int usedTime = getMaxUseTime(stack) - remainingUseTicks;
        if (usedTime < Z7Util.ticksFromSeconds(0.5f)) {
            return;
        }

        System.out.println("Throwing grenade: " + usedTime);
        if (!world.isClient) {
            Z7GrenadeEntity grenade = new Z7GrenadeEntity(Zombie7.GRENADE_ENTITY, world);
            grenade.setPosition(user.getX(), user.getEyeY(), user.getZ());
            grenade.setOwner(user);
            grenade.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 0.25f, 1.0f);
            world.spawnEntity(grenade);
        }

        user.playSound(SoundEvents.ENTITY_ENDER_PEARL_THROW, 1.0f, -3f);

        if (!(user instanceof PlayerEntity player && player.getAbilities().creativeMode)) {
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
}
