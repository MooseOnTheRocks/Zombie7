package dev.foltz.mixin;


import dev.foltz.Zombie7;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.chunk.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldPresets.Registrar.class)
public abstract class Z7WorldGenMixin {
//    private static final RegistryKey<WorldPreset> ZOMBIE7_WORLD = RegistryKey.of(RegistryKeys.WORLD_PRESET, new Identifier(Zombie7Mod.MODID, "zombie7_world"));
    @Shadow
    @Final
    private RegistryEntryLookup<ChunkGeneratorSettings> chunkGeneratorSettingsLookup;

    @Shadow
    @Final
    private RegistryEntryLookup<Biome> biomeLookup;

    @Shadow
    protected abstract void register(RegistryKey<WorldPreset> key, DimensionOptions dimensionOptions);

//    @Shadow
//    protected abstract DimensionOptions createOverworldOptions(ChunkGenerator chunkGenerator);

    @Shadow
    protected abstract DimensionOptions createOverworldOptions(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> chunkGeneratorSettings);

    @Inject(method="bootstrap()V", at=@At("RETURN"))
    private void addPresets(CallbackInfo cinfo) {
        RegistryEntry.Reference<ChunkGeneratorSettings> registryEntry = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.OVERWORLD);
        RegistryEntry.Reference<Biome> reference2 = this.biomeLookup.getOrThrow(BiomeKeys.PLAINS);
        this.register(Zombie7.ZOMBIE7_WORLD, this.createOverworldOptions(new FixedBiomeSource(reference2), registryEntry));
//        var biome = (RegistryEntry<Biome>) Registries.REGISTRIES.get(BiomeKeys.PLAINS.getRegistry()).get(BiomeKeys.PLAINS.getValue());
    }
}
