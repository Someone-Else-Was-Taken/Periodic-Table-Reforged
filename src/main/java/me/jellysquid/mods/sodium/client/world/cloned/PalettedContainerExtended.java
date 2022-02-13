package me.jellysquid.mods.sodium.client.world.cloned;

import net.minecraft.util.BitArray;
//import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.palette.IPalette;
import net.minecraft.util.palette.PalettedContainer;
//import net.minecraft.world.chunk.Palette;
//import net.minecraft.world.chunk.PalettedContainer;

public interface PalettedContainerExtended<T> {
    @SuppressWarnings("unchecked")
    static <T> PalettedContainerExtended<T> cast(PalettedContainer<T> container) {
        return (PalettedContainerExtended<T>) container;
    }

    BitArray getDataArray();

    IPalette<T> getPalette();

    T getDefaultValue();

    int getPaletteSize();
}
