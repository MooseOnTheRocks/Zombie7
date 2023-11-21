package dev.foltz.entity.misc;

import dev.foltz.Zombie7;
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

public class CannonBallEntity extends Z7GrenadeEntity {
    public CannonBallEntity(EntityType<? extends Z7GrenadeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        if (!this.leftOwner) {
            this.leftOwner = this.shouldLeaveOwner();
        }

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
        HitResult hitResult = this.getWorld().raycast(new RaycastContext(vec3d3, vec3d2 = vec3d3.add(vec3d), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
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
        if (!this.isOnGround()) {
            if (this.getVelocity().length() >= 1.8f) {
                this.setVelocity(this.getVelocity().subtract(0, 0.0075, 0));
            }
            this.setVelocity(this.getVelocity().subtract(0, 0.025, 0));
        }
        else {
            this.setVelocity(this.getVelocity().multiply(0.8));
//            System.out.println("On ground: " + this.isOnGround());
//            System.out.println("Velocity: " + this.getVelocity().length());
            if (this.getVelocity().length() < 0.05 && this.isOnGround()) {
                this.pickupType = PickupPermission.ALLOWED;
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
        if (!entity.canBeHitByProjectile()) {
            return false;
        }
        Entity entity2 = this.getOwner();
        boolean b1 = entity2 == null || this.leftOwner;

        return b1 || entity instanceof CannonBallEntity;
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.getWorld().isClient || (!this.isOnGround() && !this.isNoClip())) {
            return;
        }
        if (this.tryPickup(player)) {
//            System.out.println("HELLO: " + this.tryPickup(player));
            player.sendPickup(this, 1);
            this.discard();
        }
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        return super.tryPickup(player);
//        return player.getInventory().insertStack(this.asItemStack());
//        return switch (this.pickupType) {
//            case ALLOWED -> player.getInventory().insertStack(this.asItemStack());
//            case CREATIVE_ONLY -> player.getAbilities().creativeMode;
//            case DISALLOWED -> false;
//        };
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (!getWorld().isClient) {
            this.setVelocity(this.getVelocity().multiply(0.75));
            if (entityHitResult.getEntity() instanceof CannonBallEntity other) {
                var diff = this.getPos().subtract(other.getPos()).normalize();
//                this.addVelocity(diff.normalize().multiply(0.3).multiply(other.getVelocity()));
//                other.addVelocity(diff.normalize().multiply(-0.1).multiply(this.getVelocity()));
//                this.addVelocity(diff.multiply(other.getVelocity().rotateY((float) (Math.random() * Math.PI * 2)).multiply(0.3)));
            }
            else if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
                var entities = getWorld().getOtherEntities(this, this.getBoundingBox().expand(1d),
                        Entity::isLiving);
//                        e -> e.isLiving() && !e.getUuid().equals(this.getOwner() != null ? this.getOwner().getUuid() : null));

//                if (!entities.isEmpty()) {
//                    System.out.println("Hit some entities: " + entities.size());
//                }

                for (var entity : entities) {
                    Vec3d vec3d = this.getVelocity();
                    if (vec3d.lengthSquared() > 0.0) {
                        float push = 5.0f;
                        ((LivingEntity) entity).addVelocity(push * vec3d.x, Math.min(getVelocity().length(), 2.0f) * 0.5, push * vec3d.z);
                        if (!this.getWorld().isClient) {
                            if (getVelocity().length() > 0.1) {
                                float damage = (float) MathHelper.lerp(Math.min(getVelocity().length(), 2.0f), 2f, 20f);
                                if (entityHitResult.getEntity().damage(this.getDamageSources().create(Zombie7.BULLET_DAMAGE_TYPE, this, this.getOwner() instanceof LivingEntity e ? e : null), damage)) {
                                    System.out.println("Dealing ranged " + damage + " damage, baseDamage = " + damage + ", traveled = " + distanceTraveled);
                                }
                            }
                        }
                    }
                }
            }

            var vec3d = this.getVelocity();
            double e = vec3d.x;
            double f = vec3d.y;
            double g = vec3d.z;
            int pcount = (int) MathHelper.map(Math.min(1, vec3d.length()), 0, 1, 2, 10);
            for (int i = 0; i < pcount; ++i) {
                this.getWorld().addParticle(ParticleTypes.CRIT, this.getX() + e * (double) i / 4.0, this.getY() + f * (double) i / 4.0, this.getZ() + g * (double) i / 4.0, -e, -f + 0.2, -g);
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.NEUTRAL, 0.5f, -4.0f);
            }
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
