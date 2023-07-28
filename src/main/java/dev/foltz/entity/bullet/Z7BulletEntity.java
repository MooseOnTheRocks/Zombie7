package dev.foltz.entity.bullet;

import dev.foltz.Zombie7;
import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class Z7BulletEntity extends ProjectileEntity {
    private float bulletDamage = 0.0f;
    private float baseDistance = 0.0f;
    private float hitBoxExpansion = 0.0f;

    public Z7BulletEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setHitBoxExpansion(float scale) {
        this.hitBoxExpansion = scale;
    }

    public void setDamage(float value) {
        this.bulletDamage = value;
    }

    public void setBaseDistance(float dist) {
        this.baseDistance = dist;
    }

    public float getDamage(float travelled) {
        float damage = bulletDamage;
        float p = travelled / baseDistance;
        if (p <= 0.15f) {
            damage *= 2f;
        }
        else if (p <= 0.33f) {
            damage *= 1.5f;
        }
        else if (p <= 0.5f) {
            damage *= 1.2;
        }
        else if (p <= 1f) {
            damage *= 1;
        }
        else if (p <= 2f) {
            damage *= 0.5f;
        }
        else if (p <= 3f) {
            damage /= 6f;
        }
        else if (p <= 4f) {
            damage /= 12f;
        }
        else {
            damage = 0f;
        }

        return (float) Math.floor(2f * damage) / 2f;
    }

    @Override
    protected void initDataTracker() {
    }

    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return ProjectileUtil.getEntityCollision(this.world, this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), this::canHit);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3d vec3d2 = this.getPos();
        Vec3d vec3d = this.getVelocity();

        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        this.onCollision(hitResult);

        if (hitResult.getType() == HitResult.Type.MISS) {
            var entities = this.world.getOtherEntities(this, this.getBoundingBox().expand(hitBoxExpansion));
            boolean entityHit = false;
            if (!entities.isEmpty()) {
                for (var entity : entities) {
                    if (canHit(entity)) {
                        this.onEntityHit(new EntityHitResult(entity));
                        entityHit = true;
                    }
                }
                if (entityHit && !this.world.isClient) {
                    this.discard();
                }
            }
        }
        else if (hitResult.getType() == HitResult.Type.BLOCK) {
            this.onBlockHit((BlockHitResult) hitResult);
        }
        else if (!this.world.isClient && hitResult.getType() == HitResult.Type.ENTITY) {
            this.discard();
        }

        if (!this.world.isClient && this.world.getStatesInBox(this.getBoundingBox()).noneMatch(AbstractBlock.AbstractBlockState::isAir)) {
            this.discard();
            return;
        }
        if (this.isInsideWaterOrBubbleColumn()) {
            this.discard();
            return;
        }
        this.setVelocity(vec3d.multiply(0.99f));
        if (!this.hasNoGravity() && this.distanceTraveled > baseDistance) {
            this.setVelocity(this.getVelocity().add(0.0, -0.06f, 0.0));
        }

        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;
        this.updateRotation();
        this.setPosition(d, e, f);
        if (!this.world.isClient) {
            this.distanceTraveled += Math.sqrt(vec3d.x * vec3d.x + vec3d.y * vec3d.y + vec3d.z * vec3d.z);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.world.isClient) {
//            System.out.println("Block hit! Discarding!");
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = this.getOwner();
        float edist = (float) entityHitResult.getEntity().getPos().subtract(this.getPos()).length();
        float boundingBoxSize = (float) (this.getBoundingBox().getAverageSideLength() + entityHitResult.getEntity().getBoundingBox().getAverageSideLength());

        edist -= boundingBoxSize;
        if (edist <= boundingBoxSize) {
            edist = 0;
        }

        float newDist = distanceTraveled + edist;

        if (!this.world.isClient && entity instanceof LivingEntity livingEntity) {
            float damage = getDamage(newDist);

            if (entityHitResult.getEntity().damage(this.getDamageSources().create(Zombie7.BULLET_DAMAGE_TYPE, this, this.getOwner() instanceof LivingEntity e ? e : null), damage)) {
                System.out.println("Dealing ranged " + damage + " damage, baseDamage = " + bulletDamage + ", traveled = " + distanceTraveled);
            }
        }
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
