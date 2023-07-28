package dev.foltz.entity.misc;

import dev.foltz.entity.grenade.Z7GrenadeEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class BowlingBallGrenadeEntity extends Z7GrenadeEntity {
    public BowlingBallGrenadeEntity(EntityType<? extends Z7GrenadeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        this.baseTick();
        var prevPos = this.getPos();


        Vec3d vec3d2;
        VoxelShape voxelShape;
        BlockPos blockPos;
        BlockState blockState;
//        super.tick();
        boolean bl = this.isNoClip();
        Vec3d vec3d = this.getVelocity();


        Vec3d vec3d3 = this.getPos();
        HitResult hitResult = this.world.raycast(new RaycastContext(vec3d3, vec3d2 = vec3d3.add(vec3d), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        if (hitResult.getType() != HitResult.Type.MISS) {
            vec3d2 = hitResult.getPos();
        }
        EntityHitResult entityHitResult = this.getEntityCollision(vec3d3, vec3d2);

        var box = getBoundingBox();
        var minX = box.minX;
        var minY = box.minY;
        var minZ = box.minZ;
        var maxX = box.maxX;
        var maxY = box.maxY;
        var maxZ = box.maxZ;
        for (int i = 0; i < 8; i++) {
            var x = i % 2 < 1 ? minX : maxX;
            var y = i % 4 < 2 ? minY : maxY;
            var z = i % 8 < 4 ? minZ : maxZ;
            if (entityHitResult != null) {
                break;
            }
            var point = new Vec3d(x, y, z);
            var nextPoint = point.add(this.getVelocity());
//            System.out.println(point + " -> " + nextPoint);
            entityHitResult = getEntityCollision(point, nextPoint);
        }

        if (entityHitResult != null) {
            hitResult = entityHitResult;
        }
        if (!bl && hitResult.getType() == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult) hitResult);
        }

//        this.checkBlockCollision();

        this.move(MovementType.SELF, this.getVelocity());
        if (!this.onGround) {
            this.setVelocity(this.getVelocity().subtract(0, 0.1, 0));
        }
        else {
            this.setVelocity(this.getVelocity().multiply(0.98));
            if (this.getVelocity().length() < 0.05 && this.onGround) {
//                System.out.println("Allowed to pickup");
                if (this.getOwner() != null && this.getOwner() instanceof PlayerEntity player && player.getAbilities().creativeMode) {
                    this.pickupType = PickupPermission.CREATIVE_ONLY;
                }
                else {
                    this.pickupType = PickupPermission.ALLOWED;
                }
            }
            else {
                this.pickupType = PickupPermission.DISALLOWED;
            }
        }

        this.checkBlockCollision();


        this.distanceTraveled += getPos().subtract(prevPos).length();
    }

    @Override
    protected boolean canHit(Entity entity) {
        return super.canHit(entity) || entity instanceof BowlingBallGrenadeEntity;
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.world.isClient || !this.onGround && !this.isNoClip()) {
            return;
        }
        if (this.tryPickup(player)) {
            player.sendPickup(this, 1);
            this.discard();
        }
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        switch (this.pickupType) {
            case ALLOWED: {
                return player.getInventory().insertStack(this.asItemStack());
            }
            case CREATIVE_ONLY: {
                return player.getAbilities().creativeMode;
            }
        }
        return false;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (!world.isClient) {
            this.setVelocity(this.getVelocity().multiply(0.90));
            if (entityHitResult.getEntity() instanceof BowlingBallGrenadeEntity other) {
                var diff = this.getPos().subtract(other.getPos()).normalize();
//                this.addVelocity(diff.normalize().multiply(0.3).multiply(other.getVelocity()));
//                other.addVelocity(diff.normalize().multiply(-0.1).multiply(this.getVelocity()));
//                this.addVelocity(diff.multiply(other.getVelocity().rotateY((float) (Math.random() * Math.PI * 2)).multiply(0.3)));
            }
            else if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
                var entities = world.getOtherEntities(this, this.getBoundingBox().expand(1d),
                        e -> e.isLiving() && !e.getUuid().equals(this.getOwner() != null ? this.getOwner().getUuid() : null));

//                if (entities.size() > 0) {
//                    System.out.println("Hit some entities: " + entities.size());
//                }

                for (var entity : entities) {
//                double d = Math.max(0.1, 1.0 - ((LivingEntity) entity).getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                    Vec3d vec3d = this.getVelocity().multiply(1.0, 0.0, 1.0).normalize().multiply(MathHelper.map(getVelocity().length(), 0, 0.8, 1, 4));
                    if (vec3d.lengthSquared() > 0.0) {
                        ((LivingEntity) entity).addVelocity(vec3d.x, -1, vec3d.z);
                    }
                }
            }

            var vec3d = this.getVelocity();
            double e = vec3d.x;
            double f = vec3d.y;
            double g = vec3d.z;
            int pcount = (int) MathHelper.map(Math.min(1, vec3d.length()), 0, 1, 2, 10);
            for (int i = 0; i < pcount; ++i) {
                this.world.addParticle(ParticleTypes.CRIT, this.getX() + e * (double) i / 4.0, this.getY() + f * (double) i / 4.0, this.getZ() + g * (double) i / 4.0, -e, -f + 0.2, -g);
                this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.NEUTRAL, 0.5f, -4.0f);
            }
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
