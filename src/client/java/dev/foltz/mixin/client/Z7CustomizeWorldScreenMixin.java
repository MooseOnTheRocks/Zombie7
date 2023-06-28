package dev.foltz.mixin.client;

import dev.foltz.Zombie7;
import dev.foltz.gui.Z7CustomizeWorldScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)

@Mixin(WorldCreator.class)
public abstract class Z7CustomizeWorldScreenMixin {
    @Shadow
    public abstract WorldCreator.WorldType getWorldType();

    @Inject(at = @At("RETURN"), method = "getLevelScreenProvider", cancellable = true)
    public void applyWorldCustomizationOption(CallbackInfoReturnable<LevelScreenProvider> cir) {
        RegistryEntry<WorldPreset> registryEntry = this.getWorldType().preset();
        if (cir.getReturnValue() == null && registryEntry != null && registryEntry.getKey().isPresent() && registryEntry.getKey().get() == Zombie7.ZOMBIE7_WORLD) {
            System.out.println("Zombie7 :: applyWorldCustomizationOption");
            cir.setReturnValue((parent, generatorOptionsHolder) ->
                new Z7CustomizeWorldScreen(
                    parent,
                    generatorOptionsHolder,
                    b -> parent.getWorldCreator().applyModifier(createModifier((RegistryEntry<Biome>) b))
                )
            );
        }
    }

    private static GeneratorOptionsHolder.RegistryAwareModifier createModifier(RegistryEntry<Biome> biomeEntry) {
        return (dynamicRegistryManager, dimensionsRegistryHolder) -> {
            Registry registry = dynamicRegistryManager.get(RegistryKeys.CHUNK_GENERATOR_SETTINGS);
            RegistryEntry.Reference registryEntry2 = registry.entryOf(ChunkGeneratorSettings.OVERWORLD);
            FixedBiomeSource biomeSource = new FixedBiomeSource(biomeEntry);
            NoiseChunkGenerator chunkGenerator = new NoiseChunkGenerator((BiomeSource)biomeSource, (RegistryEntry)registryEntry2);
            return dimensionsRegistryHolder.with((DynamicRegistryManager)dynamicRegistryManager, (ChunkGenerator)chunkGenerator);
        };
    }
}