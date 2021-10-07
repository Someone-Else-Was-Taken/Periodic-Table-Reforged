package me.jellysquid.mods.lithium.mixin.gen.biome_noise_cache;

import me.jellysquid.mods.lithium.common.world.layer.CachedLocalLayerFactory;
import me.jellysquid.mods.lithium.common.world.layer.CloneableContext;
//import net.minecraft.world.biome.layer.type.ParentedLayer;
//import net.minecraft.world.biome.layer.util.LayerFactory;
//import net.minecraft.world.biome.layer.util.LayerSampleContext;
//import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Memoize the LayerFactory and make it produce thread-local copies for thread-safety purposes
 */
@Mixin(IAreaTransformer1.class)
public interface ParentedLayerMixin extends IAreaTransformer1 {
    /**
     * @reason Replace with a memoized and thread-local layer factory
     * @author gegy1000
     */
    @Overwrite
    @SuppressWarnings("unchecked")
    default <R extends IArea> IAreaFactory<R> apply(IExtendedNoiseRandom<R> context, IAreaFactory<R> parent) {
        return CachedLocalLayerFactory.createParented(this, (CloneableContext<R>) context, parent);
    }
}