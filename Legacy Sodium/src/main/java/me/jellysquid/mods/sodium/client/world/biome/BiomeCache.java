package me.jellysquid.mods.sodium.client.world.biome;

import me.jellysquid.mods.sodium.common.util.pool.ReusableObject;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.IBiomeMagnifier;
//import net.minecraft.world.biome.source.BiomeAccess;
//import net.minecraft.world.biome.source.BiomeAccessType;

import java.util.Arrays;

public class BiomeCache extends ReusableObject {
    private final IBiomeMagnifier type;
    private final long seed;

    private final Biome[] biomes;

    public BiomeCache(IBiomeMagnifier type, long seed) {
        this.type = type;
        this.seed = seed;
        this.biomes = new Biome[16 * 16];
    }

    public Biome getBiome(BiomeManager.IBiomeReader storage, int x, int z) {
        int idx = ((z & 15) << 4) | (x & 15);

        Biome biome = this.biomes[idx];

        if (biome == null) {
            this.biomes[idx] = biome = this.type.getBiome(this.seed, x, 0, z, storage);
        }

        return biome;
    }

    @Override
    public void reset() {
        Arrays.fill(this.biomes, null);
    }
}
