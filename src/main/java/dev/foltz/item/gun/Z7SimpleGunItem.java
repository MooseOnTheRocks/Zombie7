package dev.foltz.item.gun;

import dev.foltz.item.Z7IReloadable;
import dev.foltz.item.Z7IShootable;
import dev.foltz.item.ammo.Z7AmmoItem;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class Z7SimpleGunItem extends Z7GunItem {
    private final int ammoCapacity;
    private final Z7AmmoItem.AmmoCategory ammoCategory;
    protected final Map<Integer, Integer> maxUsagePerStage;

    public Z7SimpleGunItem(int maxDurability, int ammoCapacity, Z7AmmoItem.AmmoCategory ammoCategory, Map<Integer, Integer> maxUsagePerStage, Z7IReloadable reloadingBehavior, Z7IShootable shootingBehavior) {
        super(maxDurability, reloadingBehavior, shootingBehavior);
        this.ammoCapacity = ammoCapacity;
        this.ammoCategory = ammoCategory;
        this.maxUsagePerStage = Map.copyOf(maxUsagePerStage);
    }

    @Override
    public int getTotalFiringTicks() {
        return this.maxUsagePerStage.getOrDefault(Z7IGunlike.GLOBAL_STAGE_FIRING, 0);
    }

    @Override
    public int getAmmoCapacity() {
        return ammoCapacity;
    }

    @Override
    public Z7AmmoItem.AmmoCategory getAmmoCategory() {
        return ammoCategory;
    }

    @Override
    public int getMaxUsageTicks(ItemStack stack) {
        return maxUsagePerStage.getOrDefault(getGunStage(stack), 0);
    }
}
