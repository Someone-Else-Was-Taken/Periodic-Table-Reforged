package me.jellysquid.mods.lithium.mixin.gen.fast_layer_sampling;

import me.jellysquid.mods.lithium.common.world.layer.CachingLayerContextExtended;
//import net.minecraft.world.biome.layer.ScaleLayer;
//import net.minecraft.world.biome.layer.util.LayerSampleContext;
//import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.ZoomLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ZoomLayer.class)
public abstract class ScaleLayerMixin {
    @Shadow
    public abstract int getOffsetX(int x);

    @Shadow
    public abstract int getOffsetZ(int y);

    @Shadow
    protected abstract int pickZoomed(IExtendedNoiseRandom<?> ctx, int tl, int tr, int bl, int br);

    /**
     * @reason Replace with faster implementation.
     * @author gegy1000
     */
    @Overwrite
    public int apply(IExtendedNoiseRandom<?> ctx, IArea parent, int x, int z) {
        // [VanillaCopy] ScaleLayer#sample

        int tl = parent.getValue(this.getOffsetX(x), this.getOffsetZ(z));
        int ix = x & 1;
        int iz = z & 1;

        if (ix == 0 && iz == 0) {
            return tl;
        }

        ctx.setPosition(x & ~1, z & ~1);

        if (ix == 0) {
            int bl = parent.getValue(this.getOffsetX(x), this.getOffsetZ(z + 1));
            return ctx.pickRandom(tl, bl);
        }

        // Move `choose` into above if-statement: maintain rng parity
        ((CachingLayerContextExtended) ctx).skipInt();

        if (iz == 0) {
            int tr = parent.getValue(this.getOffsetX(x + 1), this.getOffsetZ(z));
            return ctx.pickRandom(tl, tr);
        }

        // Move `choose` into above if-statement: maintain rng parity
        ((CachingLayerContextExtended) ctx).skipInt();

        int bl = parent.getValue(this.getOffsetX(x), this.getOffsetZ(z + 1));
        int tr = parent.getValue(this.getOffsetX(x + 1), this.getOffsetZ(z));
        int br = parent.getValue(this.getOffsetX(x + 1), this.getOffsetZ(z + 1));

        return this.pickZoomed(ctx, tl, tr, bl, br);
    }
}