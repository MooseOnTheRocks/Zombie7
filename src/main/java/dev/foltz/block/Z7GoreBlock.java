package dev.foltz.block;

import net.minecraft.block.*;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

public class Z7GoreBlock extends FallingBlock implements LandingBlock {
    public static final int MAX_LAYERS = 8;
    public static final IntProperty LAYERS = Properties.LAYERS;

    protected static final VoxelShape[] LAYERS_TO_SHAPE = new VoxelShape[] {
            VoxelShapes.empty(),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    };

    public Z7GoreBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(LAYERS, 1));
    }

    //    @Override
//    public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
//        world.setBlockState(pos, Blocks.GOLD_BLOCK.getDefaultState());
//        onLandingSplat(world, pos);
//    }

    @Override
    public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
//        world.setBlockState(pos, Blocks.IRON_BLOCK.getDefaultState());
        // Find free space to splat, otherwise break.
        if (!world.getBlockState(pos).isAir()) {
            if (world.getBlockState(pos.up()).isAir()) {
                pos = pos.up();
            }
            else {
                return;
            }
        }


        BlockState state = fallingBlockEntity.getBlockState();
        int layers = state.get(LAYERS);

        // Divide between current position and 8 neighboring positions.
        int[] counts = new int[] {1, 0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 1; i < layers; i++) {
            int rand = ((int) (Math.random() * 9)) % 9;
            counts[rand] += 1;
        }

        // Handle neighbors
        for (int i = 1; i < counts.length; i++) {
            int count = counts[i];
            if (count == 0) {
                continue;
            }

            BlockPos splatPos = switch (i) {
                case 2 -> pos.east();
                case 3 -> pos.south();
                case 4 -> pos.west();
                case 5 -> pos.north().east();
                case 6 -> pos.north().west();
                case 7 -> pos.south().east();
                case 8 -> pos.south().west();
                default -> pos.north();
            };

            BlockState splatState = world.getBlockState(splatPos);

            if (splatState.isAir()) {
                world.setBlockState(splatPos, getDefaultState().with(LAYERS, count), Block.NOTIFY_ALL);
            }
            else if (splatState.getBlock() == Z7Blocks.GORE_BLOCK) {
                int layerCount = splatState.get(LAYERS) + count;
                if (layerCount <= MAX_LAYERS) {
                    world.setBlockState(splatPos, splatState.with(LAYERS, layerCount), Block.NOTIFY_ALL);
                }
                else {
                    world.setBlockState(splatPos, splatState.with(LAYERS, MAX_LAYERS), Block.NOTIFY_ALL);
                    counts[0] += count;
                }
            }
            else {
                counts[0] += count;
            }
        }

        // Handle self
        world.setBlockState(pos, getDefaultState().with(LAYERS, counts[0]), Block.NOTIFY_ALL);
    }

    @Override
    protected void configureFallingBlockEntity(FallingBlockEntity entity) {
        entity.setDestroyedOnLanding();
        entity.dropItem = false;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBlockState(pos.down()).getBlock() == Z7Blocks.GORE_BLOCK) {
            BlockState s = world.getBlockState(pos.down());
            int count = state.get(LAYERS);
            int layerCount = s.get(LAYERS) + count;
            if (layerCount <= MAX_LAYERS) {
                world.setBlockState(pos.down(), s.with(LAYERS, layerCount), Block.NOTIFY_ALL);
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            }
            else {
                int diff = layerCount - MAX_LAYERS;
                world.setBlockState(pos.down(), s.with(LAYERS, MAX_LAYERS), Block.NOTIFY_ALL);
                world.setBlockState(pos, state.with(LAYERS, diff), Block.NOTIFY_ALL);
            }
            return;
        }

        if (!FallingBlock.canFallThrough(world.getBlockState(pos.down())) || pos.getY() < world.getBottomY()) {
            return;
        }

        FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(world, pos, state);
        this.configureFallingBlockEntity(fallingBlockEntity);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (Math.random() > 0.98) {
            player.playSound(SoundEvents.ENTITY_ZOMBIE_AMBIENT, 1, 1);
        }
        else {
            player.playSound(SoundEvents.BLOCK_SLIME_BLOCK_PLACE, 1, 1);
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        if (type == NavigationType.LAND) {
            // Shorter than half block
            return state.get(LAYERS) < 5;
        }
        return false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return LAYERS_TO_SHAPE[state.get(LAYERS)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
//        if (state.get(LAYERS) == 1) {
//            return LAYERS_TO_SHAPE[0];
//        }
        return LAYERS_TO_SHAPE[state.get(LAYERS) - 1];
    }

    @Override
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return LAYERS_TO_SHAPE[state.get(LAYERS)];
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return LAYERS_TO_SHAPE[state.get(LAYERS)];
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return state.get(LAYERS) == 8 ? 0.2f : 1.0f;
    }

//    @Override
//    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
//        BlockState blockState = world.getBlockState(pos.down());
//        return Block.isFaceFullSquare(blockState.getCollisionShape(world, pos.down()), Direction.UP) || blockState.isOf(this) && blockState.get(LAYERS) == 8;
//    }

//    @Override
//    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
//        if (!state.canPlaceAt(world, pos)) {
//            return Blocks.AIR.getDefaultState();
//        }
//        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
//    }

//    @Override
//    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
//        if (world.getLightLevel(LightType.BLOCK, pos) > 11) {
//            SnowBlock.dropStacks(state, world, pos);
//            world.removeBlock(pos, false);
//        }
//    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        int i = state.get(LAYERS);
        if (context.getStack().isOf(this.asItem()) && i < 8) {
            if (context.canReplaceExisting()) {
                return context.getSide() == Direction.UP;
            }
            return true;
        }
        return i == 1;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf(this)) {
            int i = blockState.get(LAYERS);
            return blockState.with(LAYERS, Math.min(MAX_LAYERS, i + 1));
        }
        return super.getPlacementState(ctx);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LAYERS);
    }
}
