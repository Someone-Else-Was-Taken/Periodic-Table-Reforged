package me.jellysquid.mods.lithium.mixin.gen.fast_island_noise;

import me.jellysquid.mods.lithium.common.world.noise.SimplexNoiseCache;
//import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.biome.provider.BiomeProvider;
//import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.SimplexNoiseGenerator;
//import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
//import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin {
    @Shadow
    @Final
    private SimplexNoiseGenerator field_236083_v_;

    private ThreadLocal<SimplexNoiseCache> tlCache;

    @Inject(method = "<init>(Lnet/minecraft/world/biome/provider/BiomeProvider;Lnet/minecraft/world/biome/provider/BiomeProvider;JLjava/util/function/Supplier;)V", at = @At("RETURN"))
    private void hookConstructor(BiomeProvider biomeSource, BiomeProvider biomeSource2, long worldSeed, Supplier<DimensionSettings> supplier, CallbackInfo ci) {
        this.tlCache = ThreadLocal.withInitial(() -> new SimplexNoiseCache(this.field_236083_v_));
    }

    /**
     * Use our fast cache instead of vanilla's uncached noise generation.
     */
    @Redirect(
            method = "fillNoiseColumn([DII)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/biome/source/TheEndBiomeSource;getNoiseAt(Lnet/minecraft/util/math/noise/SimplexNoiseSampler;II)F"
            )
    )
    private float handleNoiseSample(SimplexNoiseGenerator simplexNoiseSampler, int x, int z) {
        return this.tlCache.get().getNoiseAt(x, z);
    }
}
