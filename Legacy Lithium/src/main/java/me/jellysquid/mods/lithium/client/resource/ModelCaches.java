package me.jellysquid.mods.lithium.client.resource;

import it.unimi.dsi.fastutil.ints.IntArrays;
import me.jellysquid.mods.lithium.common.LithiumMod;
import me.jellysquid.mods.lithium.common.dedup.DeduplicationCache;

public class ModelCaches {
    public static final DeduplicationCache<int[]> QUADS = new DeduplicationCache<>(IntArrays.HASH_STRATEGY);

    public static void printDebug() {
        LithiumMod.LOGGER.info("[[[ Deduplication statistics ]]]");
        LithiumMod.LOGGER.info("Baked quad cache: {}", QUADS);
    }

    public static void cleanCaches() {
        QUADS.clearCache();
    }
}
