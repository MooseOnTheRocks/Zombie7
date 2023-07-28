package dev.foltz.entity.bullet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

public class BulletLeadEntity extends Z7BulletEntity {
    public BulletLeadEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }
}
