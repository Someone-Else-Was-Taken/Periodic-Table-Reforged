package me.jellysquid.mods.lithium.common.world.layer;

//import net.minecraft.world.biome.layer.type.InitLayer;
//import net.minecraft.world.biome.layer.type.MergingLayer;
//import net.minecraft.world.biome.layer.type.ParentedLayer;
//import net.minecraft.world.biome.layer.util.LayerFactory;
//import net.minecraft.world.biome.layer.util.LayerSampleContext;
//import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;

public final class CachedLocalLayerFactory {
    public static <R extends IArea> IAreaFactory<R> createInit(IAreaTransformer0 layer, CloneableContext<R> context) {
        return createMemoized(() -> {
            IExtendedNoiseRandom<R> clonedContext = context.cloneContext();
            return clonedContext.makeArea((x, z) -> {
                clonedContext.setPosition(x, z);
                return layer.apply(clonedContext, x, z);
            });
        });
    }

    public static <R extends IArea> IAreaFactory<R> createParented(IAreaTransformer1 layer, CloneableContext<R> context, IAreaFactory<R> parent) {
        return createMemoized(() -> {
            IExtendedNoiseRandom<R> clonedContext = context.cloneContext();
            R parentSampler = parent.make();

            return clonedContext.makeArea((x, z) -> {
                clonedContext.setPosition(x, z);
                return layer.apply(clonedContext, parentSampler, x, z);
            }, parentSampler);
        });
    }

    public static <R extends IArea> IAreaFactory<R> createMerging(IAreaTransformer2 layer, CloneableContext<R> context, IAreaFactory<R> layer1, IAreaFactory<R> layer2) {
        return createMemoized(() -> {
            IExtendedNoiseRandom<R> clonedContext = context.cloneContext();
            R sampler1 = layer1.make();
            R sampler2 = layer2.make();

            return clonedContext.makeArea((x, z) -> {
                clonedContext.setPosition(x, z);
                return layer.apply(clonedContext, sampler1, sampler2, x, z);
            }, sampler1, sampler2);
        });
    }

    private static <R extends IArea> IAreaFactory<R> createMemoized(IAreaFactory<R> factory) {
        ThreadLocal<R> threadLocal = ThreadLocal.withInitial(factory::make);
        return threadLocal::get;
    }
}