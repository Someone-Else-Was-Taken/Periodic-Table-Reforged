package me.jellysquid.mods.sodium.client.world.cloned.palette;

import net.minecraft.util.IObjectIntIterable;
//import net.minecraft.util.collection.IdList;

public class ClonedPaletteFallback<K> implements ClonedPalette<K> {
    private final IObjectIntIterable<K> idList;

    public ClonedPaletteFallback(IObjectIntIterable<K> idList) {
        this.idList = idList;
    }

    @Override
    public K get(int id) {
        return this.idList.getByValue(id);
    }
}
