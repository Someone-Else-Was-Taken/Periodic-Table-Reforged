package me.jellysquid.mods.lithium.mixin.gen.biome_noise_cache;

//import net.minecraft.world.biome.layer.util.CachingLayerSampler;
//import net.minecraft.world.biome.layer.util.LayerFactory;
//import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.Layer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Layer.class)
public abstract class BiomeLayerSamplerMixin {
    private ThreadLocal<LazyArea> tlSampler;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(IAreaFactory<LazyArea> factory, CallbackInfo ci) {
        this.tlSampler = ThreadLocal.withInitial(factory::make);
    }

    /**
     * @reason Replace with implementation that accesses the thread-local sampler
     * @author gegy1000
     *
     * original implementation by gegy1000, 2No2Name replaced @Overwrite with @Redirect
     */
    @Redirect(
            method = "func_242936_a",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/area/LazyArea;getValue(II)I"
            )
    )
    private int sampleThreadLocal(LazyArea cachingLayerSampler, int i, int j) {
        return this.tlSampler.get().getValue(i, j);
    }
}
