package me.jellysquid.mods.lithium.mixin.gen.fast_island_noise;

import me.jellysquid.mods.lithium.common.world.noise.SimplexNoiseCache;
//import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.EndBiomeProvider;
//import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.gen.SimplexNoiseGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndBiomeProvider.class)
public class TheEndBiomeSourceMixin {
    @Shadow
    @Final
    private SimplexNoiseGenerator generator;
    private ThreadLocal<SimplexNoiseCache> tlCache;

    @Inject(method = "<init>(Lnet/minecraft/util/registry/Registry;JLnet/minecraft/world/biome/Biome;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/biome/Biome;)V",
            at = @At("RETURN"))
    private void hookConstructor(Registry<Biome> registry, long seed, Biome biome, Biome biome2, Biome biome3, Biome biome4, Biome biome5, CallbackInfo ci) {
        this.tlCache = ThreadLocal.withInitial(() -> new SimplexNoiseCache(this.generator));
    }

    /**
     * Use our fast cache instead of vanilla's uncached noise generation.
     */
    @Redirect(method = "getNoiseBiome", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/source/TheEndBiomeSource;getNoiseAt(Lnet/minecraft/util/math/noise/SimplexNoiseSampler;II)F"))
    private float handleNoiseSample(SimplexNoiseGenerator simplexNoiseSampler, int x, int z) {
        return this.tlCache.get().getNoiseAt(x, z);
    }
}
