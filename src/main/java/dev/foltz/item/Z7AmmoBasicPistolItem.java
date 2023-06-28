package dev.foltz.item;

import dev.foltz.Zombie7;
import dev.foltz.entity.Z7BulletEntity;
import dev.foltz.entity.Z7GrenadeEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class Z7AmmoBasicPistolItem extends Z7AmmoItem {
    public Z7AmmoBasicPistolItem() {
        super(new FabricItemSettings());
    }

    @Override
    public List<Z7BulletEntity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        Z7BulletEntity bullet = new Z7BulletEntity(Zombie7.BULLET_ENTITY, player.world);
        bullet.setPosition(player.getX(), player.getEyeY(), player.getZ());
        bullet.setOwner(player);
        bullet.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, 0.9f, 1.0f);
        bullet.setDamage(10);
        return List.of(bullet);
    }
}
