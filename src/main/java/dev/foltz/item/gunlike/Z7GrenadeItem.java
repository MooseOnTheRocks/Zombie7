package dev.foltz.item.gunlike;

import dev.foltz.Z7Util;
import dev.foltz.Zombie7;
import dev.foltz.entity.Z7GrenadeEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class Z7GrenadeItem extends Item implements Z7GunlikeItem {
    public static final String USAGE_STAGE = "UsageStage";
    public static final String GRENADE_STAGE = "GrenadeStage";

    public Z7GrenadeItem(Settings settings) {
        super(settings);
    }

    public abstract boolean cancelShoot(ItemStack stack, World world, LivingEntity entity);

    public ItemStack beginShoot(ItemStack stack, World world, LivingEntity entity) {
        int stage = getGrenadeStage(stack);
        return stack;
    }

    public abstract ItemStack tickShoot(ItemStack stack, World world, LivingEntity entity);

    public abstract ItemStack endShoot(ItemStack stack, World world, LivingEntity entity);

    @Override
    public boolean shoot(ItemStack itemStack, World world, LivingEntity entity) {
        return false;
    }

    @Override
    public ItemStack beginReload(ItemStack stack, World world, LivingEntity entity) {
        return stack;
    }

    @Override
    public abstract ItemStack tickReload(ItemStack stack, World world, LivingEntity entity);

    @Override
    public ItemStack endReload(ItemStack stack, World world, LivingEntity entity) {
        return stack;
    }

    @Override
    public boolean isReloading(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBroken(ItemStack stack) {
        return false;
    }

    @Override
    public boolean hasAmmoInGun(ItemStack stack) {
        return stack.getCount() > 0;
    }

    @Override
    public boolean canReload(ItemStack stack, PlayerEntity player) {
        return true;
    }

    @Override
    public abstract void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected);

    public abstract boolean isPrimed(ItemStack stack);

    public abstract boolean isActive(ItemStack stack);

    public int getGrenadeStage(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().contains(GRENADE_STAGE) ? stack.getNbt().getInt(GRENADE_STAGE) : 0;
    }

    public ItemStack setGrenadeStage(ItemStack stack, int stage) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt(GRENADE_STAGE, stage);
        stack.setNbt(nbt);
        return stack;
    }


    public abstract int getMaxUsageTicks(ItemStack stack);

    public int getUsageTicks(ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        return nbt.contains(USAGE_STAGE) ? nbt.getInt(USAGE_STAGE) : 0;
    }

    public ItemStack updateUsageTicks(ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        int v = nbt.contains(USAGE_STAGE) ? nbt.getInt(USAGE_STAGE) + 1 : 1;
        nbt.putInt(USAGE_STAGE, v);
        stack.setNbt(nbt);
        return stack;
    }

    public ItemStack resetUsageTicks(ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        nbt.putInt(USAGE_STAGE, 0);
        stack.setNbt(nbt);
        return stack;
    }

    @Override
    public abstract void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks);

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
    public abstract int getItemBarStep(ItemStack stack);

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().contains(GRENADE_STAGE);
    }

    @Override
    public abstract int getItemBarColor(ItemStack stack);

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }


    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }
}
