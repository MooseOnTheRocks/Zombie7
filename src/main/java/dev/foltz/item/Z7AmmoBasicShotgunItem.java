package dev.foltz.item;

import dev.foltz.Zombie7;
import dev.foltz.entity.Z7BulletEntity;
import dev.foltz.entity.Z7GrenadeEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Z7AmmoBasicShotgunItem extends Z7AmmoItem {
    public Z7AmmoBasicShotgunItem() {
        super(new FabricItemSettings());
    }

    @Override
    public List<Z7BulletEntity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        ArrayList<Z7BulletEntity> bullets = new ArrayList<>();
        int n = 5;
        for (int i = 0; i < n; i++) {
            Z7BulletEntity bullet = new Z7BulletEntity(Zombie7.BULLET_ENTITY, player.world);
            bullet.setPosition(player.getX(), player.getEyeY(), player.getZ());
            bullet.setOwner(player);
            bullet.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, 0.9f, 10.0f);
            bullet.setDamage(5f);
            bullets.add(bullet);
        }
        return List.copyOf(bullets);
    }
}
