package dev.foltz.item;

import dev.foltz.entity.Z7BulletEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class Z7AmmoItem extends Item {
    public Z7AmmoItem(Settings settings) {
        super(settings);
    }

    public abstract List<Z7BulletEntity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack);
}
