package me.jellysquid.mods.sodium.client.util.math;

public interface FrustumExtended {
    default boolean preAabbTest(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return true;
    }

    boolean fastAabbTest(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);
}

