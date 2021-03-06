package me.jellysquid.mods.phosphor.common.chunk.light;

//import net.minecraft.world.chunk.ChunkToNibbleArrayMap;
import net.minecraft.world.lighting.LightDataMap;

import java.util.concurrent.locks.StampedLock;

public interface SharedLightStorageAccess<M extends LightDataMap<M>> {
    /**
     * Bridge method to LightStorage#getStorageUncached().
     */
    M getStorage();

    /**
     * Returns the lock which wraps the {@link SharedLightStorageAccess#getStorage()}. Locking should always be
     * performed when accessing values in the aforementioned storage.
     */
    StampedLock getStorageLock();
}
