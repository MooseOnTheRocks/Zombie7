package dev.foltz.block;

import dev.foltz.Zombie7;
import dev.foltz.item.Z7Items;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import java.util.HashMap;
import java.util.Map;

public abstract class Z7Blocks {
    private static final Map<String, Block> ALL_BLOCKS = new HashMap<>();

    private static <A> boolean always(BlockState var1, BlockView var2, BlockPos var3, A var4) {
        return true;
    }

    private static boolean always(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return true;
    }

    public static final Block GORE_BLOCK = registerBlock("gore_block", new GoreBlock(FabricBlockSettings.create()
            .velocityMultiplier(0.75f)
            .allowsSpawning(Z7Blocks::always)
            .solidBlock(Z7Blocks::always)
            .blockVision(Z7Blocks::always)
            .suffocates(Z7Blocks::always)
            .slipperiness(0.8f)
            .strength(0.4f, 1.0f)
            .sounds(BlockSoundGroup.SLIME).nonOpaque()));

    public static Block registerBlock(String name, Block block) {
        ALL_BLOCKS.put(name, block);
        return block;
    }


    public static void registerAllBlocks() {
        for (var entry : ALL_BLOCKS.entrySet()) {
            var name = entry.getKey();
            var block = entry.getValue();
            var identifier = new Identifier(Zombie7.MODID, name);
            Registry.register(Registries.BLOCK, identifier, block);
            Z7Items.registerBlockItem(name, block);
        }
    }
}
