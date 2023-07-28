package dev.foltz.entity.bullet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

public class BulletBronzeEntity extends Z7BulletEntity {
    public BulletBronzeEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }
}
