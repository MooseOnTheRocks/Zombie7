package dev.foltz.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Z7StickyGrenadeEntity extends Z7GrenadeEntity {
    private static final TrackedData<Integer> FUSE_TIME = DataTracker.registerData(Z7StickyGrenadeEntity.class, TrackedDataHandlerRegistry.INTEGER);

    protected Entity entityStuck = null;
    protected Vec3d stuckOffset = Vec3d.ZERO;

    public Z7StickyGrenadeEntity(EntityType<? extends Z7GrenadeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        if (entityStuck != null) {
            if (!entityStuck.isAlive()) {
                entityStuck = null;
                this.setNoGravity(false);
                this.setNoClip(false);
            }
            else  {
//                System.out.println("Updating position to stuck entity!");
                this.setVelocity(Vec3d.ZERO);
                this.setNoGravity(true);
                this.setNoClip(true);
                if (!world.isClient) {
                    this.setPosition(entityStuck.getPos().add(stuckOffset));
                }
            }
        }

        if (entityStuck == null){
            super.tick();
        }

        if (this.inGround || entityStuck != null) {
            int ticksToExplode = getFuseTime();
            if (ticksToExplode <= -1) {

            }
            else {
                ticksToExplode -= 1;

                if (!this.world.isClient) {
                    if (ticksToExplode <= 0) {
                        world.createExplosion(null, getX(), getY(), getZ(), 5f, World.ExplosionSourceType.NONE);
                        world.createExplosion(null, getX(), getY(), getZ(), 0.25f, World.ExplosionSourceType.TNT);
                        this.discard();
                    }
                    else {
                        setFuseTime(ticksToExplode);
                    }
                }
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (entityStuck == null && !inGround) {
            this.entityStuck = entityHitResult.getEntity();
            var box = entityStuck.getBoundingBox();
            Vec3d diff = entityHitResult.getPos().add(0, box.getYLength() / 2f, 0).subtract(this.getPos().add(0, getBoundingBox().getYLength() / 2f, 0));
//            System.out.println(box.minX + ", " + box.minY + ", " + box.minZ + " :: " + entityStuck.getX() + ", " + entityStuck.getY() + ", " + entityStuck.getZ());
            var dx = diff.x < 0 ? Math.max(diff.x, -box.getXLength() / 2f) : Math.min(diff.x, box.getXLength() / 2f);
            var dy = diff.y < 0 ? Math.max(diff.y, -box.getYLength() / 2f) : Math.min(diff.y, box.getYLength() / 2f);
            var dz = diff.z < 0 ? Math.max(diff.z, -box.getZLength() / 2f) : Math.min(diff.z, box.getZLength() / 2f);
            Vec3d offset = new Vec3d(
                -dx,
                -dy + getBoundingBox().getYLength(),
                -dz
            );
            this.stuckOffset = offset;
            this.setVelocity(Vec3d.ZERO);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (!inGround && entityStuck == null) {
            this.inBlockState = this.world.getBlockState(blockHitResult.getBlockPos());
            super.onBlockHit(blockHitResult);
            Vec3d vec3d = blockHitResult.getPos().subtract(this.getX(), this.getY(), this.getZ());
            this.setVelocity(vec3d);
            Vec3d vec3d2 = vec3d.normalize().multiply(0.05f);
            this.setPos(this.getX() - vec3d2.x, this.getY() - vec3d2.y, this.getZ() - vec3d2.z);
            this.playSound(this.getSound(), 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
            this.inGround = true;
            this.shake = 7;
            this.setCritical(false);
            this.setPierceLevel((byte)0);
            this.setSound(SoundEvents.ENTITY_ARROW_HIT);
            this.setShotFromCrossbow(false);
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(FUSE_TIME, 0);
    }

    public int getFuseTime() {
        return this.dataTracker.get(FUSE_TIME);
    }

    public void setFuseTime(int time) {
        dataTracker.set(FUSE_TIME, time);
    }

    @Override
    public boolean isActive() {
        return getFuseTime() > -1;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setFuseTime(nbt.getInt("TicksToExplode"));
//        if (nbt.contains("GrenadeItem", NbtElement.COMPOUND_TYPE)) {
//            this.grenadeStack = ItemStack.fromNbt(nbt.getCompound("GrenadeItem"));
//        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("TicksToExplode", getFuseTime());
//        nbt.put("GrenadeItem", this.grenadeStack.writeNbt(new NbtCompound()));
    }
}
