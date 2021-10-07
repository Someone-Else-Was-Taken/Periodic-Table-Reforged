package me.jellysquid.mods.lithium.mixin.chunk.serialization;

import me.jellysquid.mods.lithium.common.world.chunk.CompactingPackedIntegerArray;
import net.minecraft.util.BitArray;
//import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.palette.IPalette;
//import net.minecraft.world.chunk.Palette;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Extends {@link BitArray} with a special compaction method defined in {@link CompactingPackedIntegerArray}.
 */
@Mixin(BitArray.class)
public class PackedIntegerArrayMixin implements CompactingPackedIntegerArray {
    @Shadow
    @Final
    private long[] longArray;

    @Shadow
    @Final
    private int arraySize;

    @Shadow
    @Final
    private int bitsPerEntry;

    @Shadow
    @Final
    private long maxEntryValue;

    @Shadow
    @Final
    private int field_232982_f_;

    @Override
    public <T> void compact(IPalette<T> srcPalette, IPalette<T> dstPalette, short[] out) {
        if (this.arraySize >= Short.MAX_VALUE) {
            throw new IllegalStateException("Array too large");
        }

        if (this.arraySize != out.length) {
            throw new IllegalStateException("Array size mismatch");
        }

        short[] mappings = new short[(int) (this.maxEntryValue + 1)];

        int idx = 0;

        for (long word : this.longArray) {
            long bits = word;

            for (int elementIdx = 0; elementIdx < this.field_232982_f_; ++elementIdx) {
                int value = (int) (bits & this.maxEntryValue);
                int remappedId = mappings[value];

                if (remappedId == 0) {
                    remappedId = dstPalette.idFor(srcPalette.get(value)) + 1;
                    mappings[value] = (short) remappedId;
                }

                out[idx] = (short) (remappedId - 1);
                bits >>= this.bitsPerEntry;

                ++idx;

                if (idx >= this.arraySize) {
                    return;
                }
            }
        }
    }
}
