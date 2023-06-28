package dev.foltz.entity;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Z7GrenadeEntity extends ProjectileEntity {
    public BlockState hitBlockState;

    public Z7GrenadeEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.noClip = false;
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    public void tick() {
        super.tick();
        Vec3d vec3d = this.getVelocity();
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        this.onCollision(hitResult);
        if (this.world.getStatesInBox(this.getBoundingBox()).noneMatch(AbstractBlock.AbstractBlockState::isAir)) {
            this.discard();
            System.out.println("Killing (tick) grenade!");
            return;
        }
        if (this.hitBlockState == null) {
            double d = this.getX() + vec3d.x;
            double e = this.getY() + vec3d.y;
            double f = this.getZ() + vec3d.z;

            this.setVelocity(vec3d.multiply(0.99f));
            if (!this.hasNoGravity()) {
                this.setVelocity(this.getVelocity().add(0.0, -0.06f, 0.0));
            }

            this.setPosition(d, e, f);
            this.updateTrackedPosition(d, e, f);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (this.hitBlockState != null) {
            return;
        }

        super.onBlockHit(blockHitResult);
//        if (!this.world.isClient) {
        var vel = this.getVelocity();

        if (vel.length() <= 0.01 && blockHitResult.getSide() == Direction.UP) {
            System.out.println("Grenade is stopping: " + blockHitResult.getSide() + ", " + vel.length());
            this.hitBlockState = world.getBlockState(blockHitResult.getBlockPos());
//            this.setPosition(this.getPos().subtract(this.getVelocity()));
            var pos = this.getPos();
            this.updateTrackedPosition(pos.x, pos.y, pos.z);
            this.setVelocity(Vec3d.ZERO);
        }
        else {
            System.out.println("Grenade is bouncing!");

            this.setPosition(this.getPos().subtract(this.getVelocity()));
            var pos = this.getPos();
            this.updateTrackedPosition(pos.x, pos.y, pos.z);

            double vx = vel.x;
            double vy = vel.y;
            double vz = vel.z;

            switch (blockHitResult.getSide()) {
                case DOWN, UP -> this.setVelocity(vx, -vy, vz);
                case NORTH, SOUTH -> this.setVelocity(-vx, vy, vz);
                case EAST, WEST -> this.setVelocity(vx, vy, -vz);
            }
            this.setVelocity(this.getVelocity().multiply(0.9));
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        this.setVelocity(0, this.getVelocity().y, 0);
        System.out.println("Bouncing off of entity!");
//        Entity entity = this.getOwner();
//        if (entity instanceof LivingEntity) {
//            LivingEntity livingEntity = (LivingEntity)entity;
//            entityHitResult.getEntity().damage(this.getDamageSources().mobProjectile(this, livingEntity), 1.0f);
//        }
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        double d = packet.getVelocityX();
        double e = packet.getVelocityY();
        double f = packet.getVelocityZ();
        this.setVelocity(d, e, f);
    }
}
