package dev.foltz.mixin;

import dev.foltz.block.Z7GoreBlock;
import dev.foltz.block.Z7Blocks;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Mixin(MinecraftServer.class)
@Mixin(LivingEntity.class)
public abstract class Z7EntityCreateGoreBlockMixin {
//    onKilledBy(@Nullable LivingEntity adversary)
	@Inject(at = @At("TAIL"), method = "onDeath")
	private void createGoreBlockOnDeath(DamageSource damageSource, CallbackInfo info) {
        var self = (Object) this;
        if (!(self instanceof ZombieEntity)) {
            return;
        }
        var entity = (ZombieEntity)(Object)this;
        var world = entity.getWorld();
        var pos = entity.getBlockPos();
        if (!(world instanceof ServerWorld)) {
            return;
        }

        if (!world.getBlockState(pos).isAir()) {
            if (world.getBlockState(new BlockPos(pos.getX(), (int) (entity.getBlockY() + entity.getStandingEyeHeight()), pos.getZ())).isAir()) {
                pos = new BlockPos(pos.getX(), (int) (entity.getBlockY() + entity.getStandingEyeHeight()), pos.getZ());
            }
            else if (world.getBlockState(pos.up()).isAir()) {
                pos = pos.up();
            }
            else if (world.getBlockState(pos.up().up()).isAir()) {
                pos = pos.up().up();
            }
            else {
                return;
            }
        }

        int hi = Z7GoreBlock.MAX_LAYERS;
        int lo = 3;
        if (entity.isBaby()) {
            hi = 2;
            lo = 1;
        }
        int layers = lo + (int) (Math.random() * (hi - lo));
        world.setBlockState(pos, Z7Blocks.GORE_BLOCK.getDefaultState().with(Properties.LAYERS, layers), Block.NOTIFY_ALL);
	}
}