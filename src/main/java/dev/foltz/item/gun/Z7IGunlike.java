package dev.foltz.item.gun;

import dev.foltz.item.Z7IReloadable;
import dev.foltz.item.Z7IShootable;
import dev.foltz.item.ammo.Z7AmmoItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public interface Z7IGunlike extends Z7IShootable, Z7IReloadable {
    int GLOBAL_STAGE_RELOADING = -2;
    int GLOBAL_STAGE_FIRING = -1;
    int GLOBAL_STAGE_DEFAULT = 0;

    String LAST_FIRED_TIME = "LastFiredTime";
    String AMMO_LIST = "AmmoList";
    String USAGE_STAGE = "UsageStage";
    String GUN_STAGE = "GunStage";

    int getTotalFiringTicks();
    int getAmmoCapacity();
    Z7AmmoItem.AmmoCategory getAmmoCategory();
    int getMaxUsageTicks(ItemStack stack);
    boolean shootGun(ItemStack itemStack, World world, LivingEntity entity);

    default float getModifiedBulletDamage(ItemStack gunStack, ItemStack bulletStack, float damage) {
        return damage;
    }
    default float getModifiedBulletSpeed(ItemStack gunStack, ItemStack bulletStack, float speed) {
        return speed;
    }
    default float getModifiedBulletBaseRange(ItemStack gunStack, ItemStack bulletStack, float range) {
        return range;
    }
    default float getModifiedBulletAccuracy(ItemStack gunStack, ItemStack bulletStack, float accuracy) {
        return accuracy;
    }

    default ItemStack loadOneBullet(ItemStack gun, ItemStack bulletStack, boolean takeFromBulletStack) {
        List<ItemStack> ammoInGun = new ArrayList<>(getAmmoInGun(gun));
        if (ammoInGun.size() >= getAmmoCapacity()) {
            return gun;
        }
        else {
            ItemStack bullet = takeFromBulletStack ? bulletStack.split(1) : bulletStack.copyWithCount(1);
            ammoInGun.add(bullet);
            NbtCompound nbt = gun.getOrCreateNbt();
            NbtList list = new NbtList();
            for (ItemStack stack : ammoInGun) {
                NbtCompound ammoNbt = new NbtCompound();
                stack.writeNbt(ammoNbt);
                list.add(ammoNbt);
            }
            nbt.put(AMMO_LIST, list);
            gun.setNbt(nbt);
            return gun;
        }
    }

    default ItemStack popNextBullet(ItemStack gun) {
        List<ItemStack> ammoInGun = new ArrayList<>(getAmmoInGun(gun));
        if (ammoInGun.isEmpty()) {
            return ItemStack.EMPTY;
        }
        else {
            ItemStack bullet = ammoInGun.get(0);
            ammoInGun.remove(0);
            NbtCompound nbt = gun.getOrCreateNbt();
            NbtList list = new NbtList();
            for (ItemStack stack : ammoInGun) {
                NbtCompound ammoNbt = new NbtCompound();
                stack.writeNbt(ammoNbt);
                list.add(ammoNbt);
            }
            nbt.put(AMMO_LIST, list);
            gun.setNbt(nbt);
            return bullet;
        }
    }

    default boolean isFiringCooldown(World world, ItemStack stack) {
        long lastTime = getLastFiredTime(world, stack);
        long diff = world.getTime() - lastTime;
        return diff <= getTotalFiringTicks();
    }

    default int getTotalFiringTicks(World world, ItemStack stack) {
        if (!isFiringCooldown(world, stack)) {
            return 0;
        }

        NbtCompound nbt = stack.getOrCreateNbt();
        long lastTime = nbt.contains(LAST_FIRED_TIME) ? nbt.getLong(LAST_FIRED_TIME) : 0;
        return (int) (world.getTime() - lastTime);
    }

    default long getLastFiredTime(World world, ItemStack stack) {
        return stack.getNbt() == null ? 0 : stack.getNbt().getLong(LAST_FIRED_TIME);
    }

    default ItemStack resetLastFiredTime(ItemStack stack, World world) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        nbt.putLong(LAST_FIRED_TIME, world.getTime());
        stack.setNbt(nbt);
        return stack;
    }

    default int getGunStage(ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        return nbt.contains(GUN_STAGE) ? nbt.getInt(GUN_STAGE) : 0;
    }

    default ItemStack setGunStage(ItemStack stack, int stage) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt(GUN_STAGE, stage);
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

    default boolean hasAmmoInInventory(PlayerEntity player) {
        return getAmmoCategory().findAmmoSlot(player) != -1;
    }

    default boolean hasAmmoInGun(ItemStack stack) {
        return !getAmmoInGun(stack).isEmpty();
    }

    default List<ItemStack> getAmmoInGun(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(AMMO_LIST)) {
            return List.of();
        }
        else {
            return nbt.getList(AMMO_LIST, NbtElement.COMPOUND_TYPE)
                .stream()
                .map(e -> (NbtCompound) e)
                .map(ItemStack::fromNbt).toList();
        }
    }

    default boolean isBroken(ItemStack stack) {
        return stack.getDamage() >= stack.getMaxDamage();
    }
}
