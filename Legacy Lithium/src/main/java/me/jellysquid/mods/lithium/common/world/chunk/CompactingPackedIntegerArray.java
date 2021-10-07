package me.jellysquid.mods.lithium.common.world.chunk;

import net.minecraft.util.palette.IPalette;
//import net.minecraft.world.chunk.Palette;

public interface CompactingPackedIntegerArray {
    /**
     * Copies the data out of this array into a new non-packed array. The returned array contains a copy of this array
     * re-mapped using {@param destPalette}.
     */
    <T> void compact(IPalette<T> srcPalette, IPalette<T> dstPalette, short[] out);
}
