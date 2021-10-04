package me.jellysquid.mods.hydrogen.mixin.chunk;

import it.unimi.dsi.fastutil.objects.Reference2ShortMap;
import it.unimi.dsi.fastutil.objects.Reference2ShortOpenHashMap;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.util.collection.IndexedIterable;
//import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.BitArray;
import net.minecraft.util.IObjectIntIterable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
//import net.minecraft.world.biome.source.BiomeArray;
//import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeContainer.class)
public class MixinBiomeArray {
    @Mutable
    @Shadow
    @Final
    private Biome[] biomes;

    @Shadow
    @Final
    private IObjectIntIterable<Biome> biomeRegistry;

    @Shadow
    @Final
    private static int WIDTH_BITS;

    private Biome[] palette;
    private BitArray intArray;

    @Inject(method = "<init>(Lnet/minecraft/util/IObjectIntIterable;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/biome/provider/BiomeProvider;[I)V", at = @At("RETURN"))
    private void reinit4(IObjectIntIterable<Biome> indexedIterable, ChunkPos chunkPos, BiomeProvider biomeSource, int[] is, CallbackInfo ci) {
        this.createCompact();
    }

    @Inject(method = "<init>(Lnet/minecraft/util/IObjectIntIterable;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/biome/provider/BiomeProvider;)V", at = @At("RETURN"))
    private void reinit3(IObjectIntIterable<Biome> indexedIterable, ChunkPos chunkPos, BiomeProvider biomeSource, CallbackInfo ci) {
        this.createCompact();
    }

    @Inject(method = "<init>(Lnet/minecraft/util/IObjectIntIterable;[Lnet/minecraft/world/biome/Biome;)V", at = @At("RETURN"))
    private void reinit2(IObjectIntIterable<Biome> indexedIterable, Biome[] biomes, CallbackInfo ci) {
        this.createCompact();
    }

    @OnlyIn(Dist.CLIENT)
    @Inject(method = "<init>(Lnet/minecraft/util/IObjectIntIterable;[I)V", at = @At("RETURN"))
    private void reinit1(IObjectIntIterable<Biome> indexedIterable, int[] is, CallbackInfo ci) {
        this.createCompact();
    }

    private void createCompact() {
        if (this.intArray != null || this.biomes[0] == null) {
            return;
        }

        Reference2ShortOpenHashMap<Biome> paletteTable = this.createPalette();
        Biome[] paletteIndexed = new Biome[paletteTable.size()];

        for (Reference2ShortMap.Entry<Biome> entry : paletteTable.reference2ShortEntrySet()) {
            paletteIndexed[entry.getShortValue()] = entry.getKey();
        }

        int packedIntSize = Math.max(2, MathHelper.log2DeBruijn(paletteTable.size()));
        BitArray integerArray = new BitArray(packedIntSize, BiomeContainer.BIOMES_SIZE);

        Biome prevBiome = null;
        short prevId = -1;

        for (int i = 0; i < this.biomes.length; i++) {
            Biome biome = this.biomes[i];
            short id;

            if (prevBiome == biome) {
                id = prevId;
            } else {
                id = paletteTable.getShort(biome);

                if (id < 0) {
                    throw new IllegalStateException("Palette is missing entry: " + biome);
                }

                prevId = id;
                prevBiome = biome;
            }

            integerArray.setAt(i, id);
        }

        this.palette = paletteIndexed;
        this.intArray = integerArray;
        this.biomes = null;
    }

    private Reference2ShortOpenHashMap<Biome> createPalette() {
        Reference2ShortOpenHashMap<Biome> map = new Reference2ShortOpenHashMap<>();
        map.defaultReturnValue(Short.MIN_VALUE);

        Biome prevObj = null;
        short id = 0;

        for (Biome obj : this.biomes) {
            if (obj == prevObj) {
                continue;
            }

            if (map.getShort(obj) < 0) {
                map.put(obj, id++);
            }

            prevObj = obj;
        }

        return map;
    }

    /**
     * @author JellySquid
     * @reason Use paletted lookup
     */
    @Overwrite
    public int[] getBiomeIds() {
        int size = this.intArray.size();
        int[] array = new int[size];

        for(int i = 0; i < size; ++i) {
            array[i] = this.biomeRegistry.getId(this.palette[this.intArray.getAt(i)]);
        }

        return array;
    }

    /**
     * @author JellySquid
     * @reason Use paletted lookup
     */
    @Overwrite
    public Biome getNoiseBiome(int biomeX, int biomeY, int biomeZ) {
        int x = biomeX & BiomeContainer.HORIZONTAL_MASK;
        int y = MathHelper.clamp(biomeY, 0, BiomeContainer.VERTICAL_MASK);
        int z = biomeZ & BiomeContainer.HORIZONTAL_MASK;

        return this.palette[this.intArray.getAt(y << WIDTH_BITS + WIDTH_BITS | z << WIDTH_BITS | x)];
    }
}