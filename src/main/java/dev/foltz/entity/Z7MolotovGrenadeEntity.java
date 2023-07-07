package dev.foltz.entity;

import dev.foltz.Z7Util;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class Z7MolotovGrenadeEntity extends Z7GrenadeEntity {
    private static final TrackedData<Boolean> LIT = DataTracker.registerData(Z7MolotovGrenadeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public Z7MolotovGrenadeEntity(EntityType<? extends Z7GrenadeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (!isActive()) {
            return;
        }

        double x = getX();
        double y = getY();
        double z = getZ();
        var vel = getVelocity();
        var len = vel.length();
        double vx = 0;
        double vy = 0;
        double vz = 0;
        if (len > 0) {
            var norm = vel.normalize();
            double speed = -0.1;
            vx = speed * norm.x;
            vy = speed * norm.y;
            vz = speed * norm.z;
        }

        int count = (int) Math.floor(1 + 3 * len * world.random.nextFloat());
        for (int i = 0; i < count; i++) {
            double p = i / (float) (count - 1);
            double xx = MathHelper.lerp(p, x, x - vel.x);
            double yy = MathHelper.lerp(p, y, y - vel.y);
            double zz = MathHelper.lerp(p, z, z - vel.z);
            world.addParticle(ParticleTypes.SMALL_FLAME, xx, yy, zz, vx, vy, vz);
        }
    }

    private void burstIntoFlames() {
        if (!this.world.isClient) {
            BlockPos start = getBlockPos();
            for (int i = 0; i < 2 && !Z7Util.isAirWithFullFaceBelow(world, start); i++) {
                start = start.down();
            }
            int n = 4;
            int range = 12;
            int steps = 16;
            var blocks = Z7Util.getBlocksAround(range, start, world);
            var paths = new ArrayList<List<BlockPos>>();
            for (int i = 0; i < n; i++) {
//                var thick = i == 0 || world.random.nextFloat() > 0.5;
//                thick = false;
                var backTrack = i % 2;
                var path = Z7Util.randomWalkOnGround(steps, start, blocks, world, backTrack);
                paths.add(path);
            }

            for (var path : paths) {
                for (BlockPos blockPos : path) {
                    if (Z7Util.isAirWithFullFaceBelow(world, blockPos) && world.random.nextFloat() > 0.15) {
                        this.world.setBlockState(blockPos, AbstractFireBlock.getState(this.world, blockPos));
                    }
                }
            }

            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!this.world.isClient && isActive()) {
            burstIntoFlames();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.world.isClient && isActive()) {
            burstIntoFlames();
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(LIT, false);
    }

    public boolean isLit() {
        return dataTracker.get(LIT);
    }

    public void setLit(boolean lit) {
        dataTracker.set(LIT, lit);
    }

    @Override
    public boolean isActive() {
        return isLit();
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setLit(nbt.getBoolean("Lit"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Lit", isLit());
    }
}
