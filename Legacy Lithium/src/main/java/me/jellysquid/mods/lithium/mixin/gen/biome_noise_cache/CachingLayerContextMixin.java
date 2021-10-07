package me.jellysquid.mods.lithium.mixin.gen.biome_noise_cache;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import me.jellysquid.mods.lithium.common.world.layer.CloneableContext;
import me.jellysquid.mods.lithium.common.world.layer.FastCachingLayerSampler;
//import net.minecraft.util.math.noise.PerlinNoiseSampler;
//import net.minecraft.world.biome.layer.util.CachingLayerContext;
//import net.minecraft.world.biome.layer.util.CachingLayerSampler;
//import net.minecraft.world.biome.layer.util.LayerOperator;
//import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.ImprovedNoiseGenerator;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LazyAreaLayerContext.class)
public class CachingLayerContextMixin implements CloneableContext<LazyArea> {
    @Shadow
    @Final
    @Mutable
    private long seed;

    @Shadow
    @Final
    @Mutable
    private ImprovedNoiseGenerator noise;

    @Shadow
    @Final
    @Mutable
    private Long2IntLinkedOpenHashMap cache;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(int cacheCapacity, long seed, long salt, CallbackInfo ci) {
        // We don't use this cache
        this.cache = null;
    }

    /**
     * @reason Replace with optimized cache implementation
     * @author gegy1000
     */
    @Overwrite
    public LazyArea makeArea(IPixelTransformer operator) {
        return new FastCachingLayerSampler(128, operator);
    }

    /**
     * @reason Replace with optimized cache implementation
     * @author gegy1000
     */
    @Overwrite
    public LazyArea makeArea(IPixelTransformer operator, LazyArea sampler) {
        return new FastCachingLayerSampler(512, operator);
    }

    /**
     * @reason Replace with optimized cache implementation
     * @author gegy1000
     */
    @Overwrite
    public LazyArea makeArea(IPixelTransformer operator, LazyArea left, LazyArea right) {
        return new FastCachingLayerSampler(512, operator);
    }

    @Override
    public IExtendedNoiseRandom<LazyArea> cloneContext() {
        LazyAreaLayerContext context = new LazyAreaLayerContext(0, 0, 0);

        CachingLayerContextMixin access = (CachingLayerContextMixin) (Object) context;
        access.seed = this.seed;
        access.noise = this.noise;

        return context;
    }
}
