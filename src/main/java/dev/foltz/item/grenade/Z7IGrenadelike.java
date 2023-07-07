package dev.foltz.item.grenade;

import dev.foltz.item.Z7IReloadable;
import dev.foltz.item.Z7IShootable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public interface Z7IGrenadelike extends Z7IShootable, Z7IReloadable {
    String GRENADE_STAGE = "GrenadeStage";
    String USAGE_STAGE = "UsageStage";

    int getMaxUsageTicks(ItemStack stack);
    boolean throwGrenade(ItemStack itemStack, World world, LivingEntity entity, float speed);


    default int getGrenadeStage(ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        return nbt.contains(GRENADE_STAGE) ? nbt.getInt(GRENADE_STAGE) : 0;
    }

    default ItemStack setGrenadeStage(ItemStack stack, int stage) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt(GRENADE_STAGE, stage);
        stack.setNbt(nbt);
        return stack;
    }

    default int getUsageStage(ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        return nbt.contains(USAGE_STAGE) ? nbt.getInt(USAGE_STAGE) : 0;
    }

    default ItemStack updateUsageStage(ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        int v = nbt.contains(USAGE_STAGE) ? nbt.getInt(USAGE_STAGE) + 1 : 1;
        nbt.putInt(USAGE_STAGE, v);
        stack.setNbt(nbt);
        return stack;
    }

    default ItemStack resetUsageStage(ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        nbt.putInt(USAGE_STAGE, 0);
        stack.setNbt(nbt);
        return stack;
    }

    default ItemStack setUsageStage(ItemStack stack, int stage) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt(USAGE_STAGE, stage);
        stack.setNbt(nbt);
        return stack;
    }
}
