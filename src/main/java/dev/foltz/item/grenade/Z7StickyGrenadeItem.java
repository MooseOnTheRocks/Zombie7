package dev.foltz.item.grenade;

import dev.foltz.Z7Util;
import dev.foltz.entity.Z7Entities;
import dev.foltz.entity.Z7GrenadeEntity;
import dev.foltz.entity.Z7MolotovGrenadeEntity;
import dev.foltz.entity.Z7StickyGrenadeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class Z7StickyGrenadeItem extends Z7GrenadeItem {
    public Z7StickyGrenadeItem() {
        super(16, Map.of());
    }

    public List<? extends Z7GrenadeEntity> createGrenadeEntities(LivingEntity entity, ItemStack stack) {
        boolean isActive = getGrenadeStage(stack) == GLOBAL_STAGE_PRIMED;
        Z7StickyGrenadeEntity grenade = new Z7StickyGrenadeEntity(Z7Entities.STICKY_GRENADE_ENTITY, entity.world);
        if (isActive) {
            grenade.setFuseTime(Z7Util.ticksFromSeconds(5));
        }
        else {
            grenade.setFuseTime(-1);
        }
        return List.of(grenade);
    }

    @Override
    public boolean canReload(ItemStack stack, LivingEntity entity) {
        return getGrenadeStage(stack) == GLOBAL_STAGE_PRIMED;
    }

    @Override
    public ItemStack beginShoot(ItemStack stack, LivingEntity entity) {
        if (!(stack.getItem() instanceof Z7IGrenadelike grenadelike)) {
            return stack;
        }

        if (getGrenadeStage(stack) == GLOBAL_STAGE_DEFAULT) {
            setGrenadeStage(stack, GLOBAL_STAGE_PRIMED);
            resetUsageStage(stack);
        }

        return stack;
    }

    @Override
    public ItemStack endShoot(ItemStack stack, LivingEntity entity) {
        if (!(stack.getItem() instanceof Z7IGrenadelike grenadelike)) {
            return stack;
        }

        return stack;
    }

    @Override
    public void tickShootInventory(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!selected && getGrenadeStage(stack) == GLOBAL_STAGE_PRIMED) {
            setGrenadeStage(stack, GLOBAL_STAGE_DEFAULT);
        }
    }
}
