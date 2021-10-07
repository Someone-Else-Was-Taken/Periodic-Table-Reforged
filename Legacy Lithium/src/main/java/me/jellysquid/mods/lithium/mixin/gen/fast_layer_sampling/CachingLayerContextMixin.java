package me.jellysquid.mods.lithium.mixin.gen.fast_layer_sampling;

import me.jellysquid.mods.lithium.common.world.layer.CachingLayerContextExtended;
import net.minecraft.util.FastRandom;
//import net.minecraft.world.biome.layer.util.CachingLayerContext;
//import net.minecraft.world.biome.source.SeedMixer;
import net.minecraft.world.gen.LazyAreaLayerContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LazyAreaLayerContext.class)
public class CachingLayerContextMixin implements CachingLayerContextExtended {
    @Shadow
    private long positionSeed;

    @Shadow
    @Final
    private long seed;

    @Override
    public void skipInt() {
        this.positionSeed = FastRandom.mix(this.positionSeed, this.seed);
    }
}