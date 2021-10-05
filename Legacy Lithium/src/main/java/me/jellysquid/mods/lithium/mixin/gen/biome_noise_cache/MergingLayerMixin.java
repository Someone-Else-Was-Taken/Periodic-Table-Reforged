package me.jellysquid.mods.lithium.mixin.gen.biome_noise_cache;

import me.jellysquid.mods.lithium.common.world.layer.CachedLocalLayerFactory;
import me.jellysquid.mods.lithium.common.world.layer.CloneableContext;
//import net.minecraft.world.biome.layer.type.MergingLayer;
//import net.minecraft.world.biome.layer.util.LayerFactory;
//import net.minecraft.world.biome.layer.util.LayerSampleContext;
//import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Memoize the LayerFactory and make it produce thread-local copies for thread-safety purposes
 */
@Mixin(IAreaTransformer2.class)
public interface MergingLayerMixin extends IAreaTransformer2 {
    /**
     * @reason Replace with a memoized and thread-local layer factory
     * @author gegy1000
     */
    @Overwrite
    @SuppressWarnings("unchecked")
    default <R extends IArea> IAreaFactory<R> apply(IExtendedNoiseRandom<R> context, IAreaFactory<R> layer1, IAreaFactory<R> layer2) {
        return CachedLocalLayerFactory.createMerging(this, (CloneableContext<R>) context, layer1, layer2);
    }
}