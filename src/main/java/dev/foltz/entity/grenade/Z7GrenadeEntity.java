package dev.foltz.entity.grenade;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class Z7GrenadeEntity extends PersistentProjectileEntity {
    public ItemStack grenadeStack = ItemStack.EMPTY;

    public boolean isMoving() {
        return !inGround;
    }


    public Z7GrenadeEntity(EntityType<? extends Z7GrenadeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        if (isActive()) {
            return false;
        }

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

    public void setItemStack(ItemStack itemStack) {
        this.grenadeStack = itemStack;
    }


    public abstract boolean isActive();

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
//        super.onEntityHit(entityHitResult);
        if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            double d = Math.max(0.0, 1.0 - livingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
            Vec3d vec3d = this.getVelocity().multiply(1.0, 0.0, 1.0).normalize().multiply((double) 1 * 0.6 * d);
            if (vec3d.lengthSquared() > 0.0) {
//                System.out.println("Smacking a living entity!");
                livingEntity.addVelocity(vec3d.x, 0.1, vec3d.z);
            }
        }
        var evel = this.getVelocity();
        this.setVelocity(-evel.x * 0.05f, evel.y * 0.2f, -evel.z * 0.05f);
        var epos = entityHitResult.getEntity().getPos();
        this.setPosition((this.getX() + epos.x) / 2f, this.getY(), (this.getZ() + epos.z) / 2f);
//        System.out.println("Grenade hit entity: bouncing grenade!");
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        var prevVelocity = this.getVelocity();
        Vec3d vec3d = blockHitResult.getPos().subtract(this.getX(), this.getY(), this.getZ());
        Vec3d vec3d2 = vec3d.normalize().multiply(0.05f);
        this.setPos(this.getX() - vec3d2.x, this.getY() - vec3d2.y, this.getZ() - vec3d2.z);

        this.inGround = false;
        prevVelocity = prevVelocity.multiply(0.8);
//        System.out.println("Bouncing off of side: " + blockHitResult.getSide());
        switch (blockHitResult.getSide()) {
            case DOWN -> setVelocity(prevVelocity.x, -prevVelocity.y * 0.5, prevVelocity.z);
            case NORTH, SOUTH -> setVelocity(prevVelocity.x, prevVelocity.y, -prevVelocity.z * 0.5);
            case EAST, WEST -> setVelocity(-prevVelocity.x * 0.5, prevVelocity.y, prevVelocity.z);
            case UP -> {
//                System.out.println("prevVel: " + prevVelocity);
                if (prevVelocity.length() <= 0.2f) {
                    this.inBlockState = this.world.getBlockState(blockHitResult.getBlockPos());
                    BlockState blockState = this.world.getBlockState(blockHitResult.getBlockPos());
                    blockState.onProjectileHit(this.world, blockState, blockHitResult, this);
                    this.playSound(this.getSound(), 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
                    this.inGround = true;
                    this.setVelocity(Vec3d.ZERO);
                    super.onBlockHit(blockHitResult);
                }
                else {
                    setVelocity(prevVelocity.x, -prevVelocity.y * 0.5, prevVelocity.z);
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
//        this.dataTracker.startTracking(FUSE_TIME, 0);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
//        setFuseTime(nbt.getInt("TicksToExplode"));
        if (nbt.contains("GrenadeItem", NbtElement.COMPOUND_TYPE)) {
            this.grenadeStack = ItemStack.fromNbt(nbt.getCompound("GrenadeItem"));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
//        nbt.putInt("TicksToExplode", getFuseTime());
        nbt.put("GrenadeItem", this.grenadeStack.writeNbt(new NbtCompound()));
    }

    @Override
    protected ItemStack asItemStack() {
        var itemStack = new ItemStack(grenadeStack.getItem(), 1);
        return itemStack;
    }

    public void setVelocity(Entity shooter, float pitch, float yaw, float roll, float speed) {
        float f = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        float g = -MathHelper.sin((pitch + roll) * ((float)Math.PI / 180));
        float h = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        this.setVelocity(speed * f, speed * g, speed * h);
        Vec3d vec3d = shooter.getVelocity();
        this.setVelocity(this.getVelocity().add(vec3d.x, shooter.isOnGround() ? 0.0 : vec3d.y, vec3d.z));
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
