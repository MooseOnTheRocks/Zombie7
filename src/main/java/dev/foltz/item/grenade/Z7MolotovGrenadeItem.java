package dev.foltz.item.grenade;

import dev.foltz.Z7Util;
import dev.foltz.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class Z7MolotovGrenadeItem extends Z7GrenadeItem {
    public Z7MolotovGrenadeItem() {
        super(16, Map.of());
    }

    public List<? extends Z7GrenadeEntity> createGrenadeEntities(LivingEntity entity, ItemStack stack) {
        boolean isActive = getGrenadeStage(stack) == GLOBAL_STAGE_PRIMED;
        Z7MolotovGrenadeEntity grenade = new Z7MolotovGrenadeEntity(Z7Entities.MOLOTOV_GRENADE_ENTITY, entity.world);
        grenade.setLit(isActive);
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
