package dev.foltz.item.gunlike;

import dev.foltz.Z7Util;
import dev.foltz.Zombie7;
import dev.foltz.entity.Z7Entities;
import dev.foltz.entity.Z7FragGrenadeEntity;
import dev.foltz.entity.Z7GrenadeEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class Z7FragGrenadeItem extends Z7GrenadeItem {
    public static final String FUSE_STAGE = "FuseStage";
    public static final int FUSE_TIME = Z7Util.ticksFromSeconds(7.0f);

    public Z7FragGrenadeItem() {
        super(new FabricItemSettings().maxCount(16));
    }

    public boolean cancelShoot(ItemStack stack, World world, LivingEntity entity) {
        if (getGrenadeStage(stack) == 1) {
            stack.setNbt(null);
            return true;
        }
        return false;
    }

    public ItemStack beginShoot(ItemStack stack, World world, LivingEntity entity) {
        int stage = getGrenadeStage(stack);

        return stack;
    }

    public ItemStack tickShoot(ItemStack stack, World world, LivingEntity entity) {
        int stage = getGrenadeStage(stack);
        if (stage == 0) {
            if (getUsageTicks(stack) >= getMaxUsageTicks(stack)) {
                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.5f, 3.0f);
                return resetUsageTicks(setGrenadeStage(stack, 1));
            }
            else {
                return updateUsageTicks(stack);
            }
        }
        else if (stage == 1) {
            return updateUsageTicks(stack);
        }
        else {
            return stack;
        }
    }

    public ItemStack endShoot(ItemStack stack, World world, LivingEntity entity) {
        int stage = getGrenadeStage(stack);

        if (stage == 1) {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
            entity.swingHand(Hand.MAIN_HAND);
            return resetUsageTicks(setGrenadeStage(stack, 2));
        }

        return stack;
    }

    @Override
    public ItemStack beginReload(ItemStack stack, World world, LivingEntity entity) {
        return stack;
    }

    @Override
    public ItemStack tickReload(ItemStack stack, World world, LivingEntity entity) {
        System.out.println("Ticking reload");
        if (getGrenadeStage(stack) == 1) {
            return resetUsageTicks(setGrenadeStage(stack, 0));
        }
        return stack;
    }

    @Override
    public ItemStack endReload(ItemStack stack, World world, LivingEntity entity) {
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient) {
            if (getGrenadeStage(stack) == 2) {
                if (getFuseTicks(stack) == FUSE_TIME) {
                    if (entity instanceof PlayerEntity player && selected) {
                        player.stopUsingItem();
                    }
                    world.createExplosion(null, entity.getX(), entity.getY(), entity.getZ(), 2.0f, World.ExplosionSourceType.TNT);
                    stack.setNbt(null);
                    stack.decrement(1);
                }
                else {
                    updateFuseTicks(stack);
                }
            }
        }
    }

    @Override
    public boolean isPrimed(ItemStack stack) {
        return getGrenadeStage(stack) == 1;
    }

    @Override
    public boolean isActive(ItemStack stack) {
        return getGrenadeStage(stack) == 2;
    }

    public ItemStack updateFuseTicks(ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        int v = nbt.contains(FUSE_STAGE) ? nbt.getInt(FUSE_STAGE) + 1 : 1;
        nbt.putInt(FUSE_STAGE, v);
        stack.setNbt(nbt);
        return stack;
    }

    public int getFuseTicks(ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        return nbt.contains(FUSE_STAGE) ? nbt.getInt(FUSE_STAGE) : 0;
    }


    public int getMaxUsageTicks(ItemStack stack) {
        return getGrenadeStage(stack) == 0 ? 2 : 0;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int usedTime = getMaxUseTime(stack) - remainingUseTicks;
        if (usedTime < Z7Util.ticksFromSeconds(0.5f)) {
            return;
        }

        System.out.println("Throwing grenade: " + usedTime);
        final int fullThrowTime = Z7Util.ticksFromSeconds(3f);
        int usedTicks = getMaxUseTime(stack) - remainingUseTicks;
        float throwSpeed = MathHelper.map(Math.min(usedTicks, fullThrowTime), 0, fullThrowTime, 0.25f, 1.8f);

        if (!world.isClient) {
            ItemStack throwStack = new ItemStack(stack.getItem());
            boolean isActive = getGrenadeStage(stack) == 2 || getGrenadeStage(stack) == 1;
            Z7FragGrenadeEntity grenade = new Z7FragGrenadeEntity(Z7Entities.FRAG_GRENADE_ENTITY, world);
            grenade.setItemStack(throwStack);
            grenade.setOwner(user);
            grenade.setPosition(user.getX(), user.getEyeY(), user.getZ());
            grenade.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, throwSpeed);
            if (isActive) {
                System.out.println("Throwing a live grenade!");
                grenade.setFuseTime(FUSE_TIME - getFuseTicks(stack));
            }
            else {
                System.out.println("Throwing an unlit grenade!");
                grenade.setFuseTime(-1);
            }
            world.spawnEntity(grenade);
        }

        user.playSound(SoundEvents.ENTITY_ENDER_PEARL_THROW, 1.0f, -3f);

        setGrenadeStage(stack, 0);
        resetUsageTicks(stack);
        stack.setNbt(null);

        if (!(user instanceof PlayerEntity player && player.getAbilities().creativeMode)) {
            stack.decrement(1);
        }
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        if (getGrenadeStage(stack) == 2) {
            return Math.round(13f * (1 - getFuseTicks(stack) / (float) FUSE_TIME));
        }
        return 13;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        final float red = 0.0f;
        final float green = 0.33f;
        final float yellow = 0.2f;
        final float orange = 0.1f;

        int stage = getGrenadeStage(stack);

        if (stage == 1) {
            return MathHelper.hsvToRgb(yellow, 1.0f, 1.0f);
        }
        else if (stage == 2) {
            float p = getFuseTicks(stack) / (float) FUSE_TIME;
            float h = MathHelper.map(p, 1, 0, yellow, red);
            return MathHelper.hsvToRgb(h, 1.0f, 1.0f);
        }
        else {
            return MathHelper.hsvToRgb(green, 1.0f, 1.0f);
        }
    }
}
