package dev.foltz;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Predicate;

public abstract class Z7Util {
    public static final int TICKS_PER_SECOND = 20;

    public static int ticksFromSeconds(float seconds) {
        return (int) (seconds * TICKS_PER_SECOND);
    }

    public static int ticksFromMinutes(float minutes) {
        return (int) (minutes * 60 * TICKS_PER_SECOND);
    }

    public static Identifier identifier(String path) {
        return new Identifier(Zombie7.MODID, path);
    }

    public static Collection<BlockPos> getBlocksAround(double range, BlockPos pos, World world) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;

        HashSet<BlockPos> set = Sets.newHashSet();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                block2:
                for (int k = 0; k < 16; ++k) {
                    if (i != 0 && i != 15 && j != 0 && j != 15 && k != 0 && k != 15)
                        continue;
                    double d = (float)i / 15.0f * 2.0f - 1.0f;
                    double e = (float)j / 15.0f * 2.0f - 1.0f;
                    double f = (float)k / 15.0f * 2.0f - 1.0f;
                    double g = Math.sqrt(d * d + e * e + f * f);
                    d /= g;
                    e /= g;
                    f /= g;
                    double m = x;
                    double n = y;
                    double o = z;
                    float p = 0.3f;
                    for (float h = (float) range * (0.7f + world.random.nextFloat() * 0.6f); h > 0.0f; h -= 0.22500001f) {
                        BlockPos blockPos = BlockPos.ofFloored(m, n, o);
                        BlockState blockState = world.getBlockState(blockPos);
                        FluidState fluidState = world.getFluidState(blockPos);
                        if (!world.isInBuildLimit(blockPos))
                            continue block2;
                        Optional<Float> optional = getBlastResistance(blockState, fluidState);
                        if (optional.isPresent()) {
                            h -= (optional.get() + 0.3f) * 0.3f;
                        }
                        if (h > 0.0f) {
                            set.add(blockPos);
                        }
                        m += d * (double)0.3f;
                        n += e * (double)0.3f;
                        o += f * (double)0.3f;
                    }
                }
            }
        }

        return set;
    }

    public static List<BlockPos> randomWalkOnGround(int steps, BlockPos start, Collection<BlockPos> blocks, World world, float avoidBackTrackChance) {
        List<BlockPos> path = new ArrayList<>();
        BlockPos pos = start;

        for (int i = 0; i < steps; i++) {
            List<BlockPos> possibleNext = new ArrayList<>();

            for (int a = -1; a <= 1; a++) {
                for (int b = -1; b <= 1; b++) {
                    for (int c = -1; c <= 1; c++) {
                        BlockPos test = pos.add(a, b, c);
                        if (blocks.contains(test) && isAirWithFullFaceBelow(world, test)) {
                            possibleNext.add(test);
                        }
                    }
                }
            }

            if (possibleNext.isEmpty()) {
                continue;
            }


            var allowBackTrack = world.random.nextFloat() > avoidBackTrackChance;

            if (allowBackTrack) {
                int nextIndex = world.random.nextInt(possibleNext.size());
                pos = possibleNext.get(nextIndex);
                path.add(pos);
            }
            else {
                var unvisited = possibleNext.stream().filter(Predicate.not(path::contains)).toList();
                if (unvisited.isEmpty()) {
                    var visited = possibleNext.stream().filter(path::contains).toList();
                    int nextIndex = world.random.nextInt(visited.size());
                    pos = visited.get(nextIndex);
                    path.add(pos);
                }
                else {
                    int nextIndex = world.random.nextInt(unvisited.size());
                    pos = unvisited.get(nextIndex);
                    path.add(pos);
                }
            }
        }

        return path;
    }

    public static boolean isAirWithFullFaceBelow(World world, BlockPos blockPos) {
        return world.getBlockState(blockPos).isAir() && world.getBlockState(blockPos.down()).isOpaqueFullCube(world, blockPos.down());
    }

    public static Optional<Float> getBlastResistance(BlockState blockState, FluidState fluidState) {
        if (blockState.isAir() && fluidState.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Math.max(blockState.getBlock().getBlastResistance(), fluidState.getBlastResistance()));
    }

    public static boolean detectFuncStep(float p0, float p1, float pMax, int maxSize) {
//        int a = (int) Math.floor(maxSize * (Math.max(p0, 0) / (float) pMax));
//        int b = (int) Math.floor(maxSize * (Math.max(p1, 0) / (float) pMax));
        int a = (int) Math.ceil(MathHelper.map(Math.max(p0, 0), 0, pMax, 0, maxSize));
        int b = (int) Math.ceil(MathHelper.map(Math.max(p1, 0), 0, pMax, 0, maxSize));
        return a != b;
    }
}
