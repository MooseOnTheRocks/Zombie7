package dev.foltz.entity.bullet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BulletGrapeshotEntity extends Z7BulletEntity {
    public BulletGrapeshotEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!this.getWorld().isClient && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            Vec3d vec3d = this.getVelocity().multiply(1.0, 0.0, 1.0).normalize().multiply(MathHelper.map(getVelocity().length(), 0, 0.8, 1, 4));
            if (vec3d.lengthSquared() > 0.0) {
                float f = 0.05f;
                livingEntity.addVelocity(f * vec3d.x, f, f * vec3d.z);
            }
        }
    }
}
