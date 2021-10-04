package me.jellysquid.mods.phosphor.mixin.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import me.jellysquid.mods.phosphor.common.chunk.light.IReadonly;
import me.jellysquid.mods.phosphor.common.chunk.light.LightInitializer;
import me.jellysquid.mods.phosphor.common.chunk.light.LightProviderUpdateTracker;
import me.jellysquid.mods.phosphor.common.chunk.light.LightStorageAccess;
import me.jellysquid.mods.phosphor.common.chunk.light.SharedLightStorageAccess;
import me.jellysquid.mods.phosphor.common.util.chunk.light.EmptyChunkNibbleArray;
import me.jellysquid.mods.phosphor.common.util.chunk.light.EmptyNibbleArray;
import net.minecraft.util.Direction;
import net.minecraft.util.SectionDistanceGraph;
import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.SectionPos;
//import net.minecraft.util.math.Direction;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
//import net.minecraft.world.SectionDistanceLevelPropagator;
//import net.minecraft.world.chunk.NibbleArray;
//import net.minecraft.world.chunk.ChunkProvider;
//import net.minecraft.world.chunk.ChunkToNibbleArrayMap;
//import net.minecraft.world.chunk.light.ChunkLightProvider;
//import net.minecraft.world.chunk.light.LightStorage;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.lighting.LightDataMap;
import net.minecraft.world.lighting.LightEngine;
import net.minecraft.world.lighting.SectionLightStorage;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.locks.StampedLock;

@Mixin(SectionLightStorage.class)
public abstract class MixinLightStorage<M extends LightDataMap<M>> extends SectionDistanceGraph implements SharedLightStorageAccess<M>, LightStorageAccess {
    protected MixinLightStorage() {
        super(0, 0, 0);
    }

    @Shadow
    @Final
    protected M cachedLightData;

    @Mutable
    @Shadow
    @Final
    protected LongSet dirtyCachedSections;

    @Mutable
    @Shadow
    @Final
    protected LongSet changedLightPositions;

    @Shadow
    protected abstract int getLevel(long id);

    @Mutable
    @Shadow
    @Final
    protected LongSet activeLightSections;

    @Mutable
    @Shadow
    @Final
    protected LongSet addedActiveLightSections;

    @Mutable
    @Shadow
    @Final
    protected LongSet addedEmptySections;

    @Shadow
    protected abstract void addSection(long blockPos);

    @SuppressWarnings("unused")
    @Shadow
    protected volatile boolean hasSectionsToUpdate;

    @Shadow
    protected volatile M uncachedLightData;

    @Shadow
    protected abstract NibbleArray getOrCreateArray(long pos);

    @Shadow
    @Final
    protected Long2ObjectMap<NibbleArray> newArrays;

    @Shadow
    protected abstract boolean hasSectionsToUpdate();

    @Shadow
    protected abstract void removeSection(long l);

    @Shadow
    @Final
    private static Direction[] DIRECTIONS;

    @Shadow
    protected abstract void cancelSectionUpdates(LightEngine<?, ?> storage, long blockChunkPos);

    @Shadow
    protected abstract NibbleArray getArray(long sectionPos, boolean cached);

    @Shadow
    @Final
    private IChunkLightProvider chunkProvider;

    @Shadow
    @Final
    private LightType type;

    @Shadow
    @Final
    private LongSet field_241536_n_;

    @Override
    @Invoker("getArray")
    public abstract NibbleArray callGetLightSection(final long sectionPos, final boolean cached);

    @Shadow
    protected int getSourceLevel(long id) {
        return 0;
    }

    private final StampedLock uncachedLightArraysLock = new StampedLock();

    /**
     * Replaces the two set of calls to unpack the XYZ coordinates from the input to just one, storing the result as local
     * variables.
     *
     * Additionally, this handles lookups for positions without an associated lightmap.
     *
     * @reason Use faster implementation
     * @author JellySquid
     */
    @Overwrite
    public int getLight(long blockPos) {
        int x = BlockPos.unpackX(blockPos);
        int y = BlockPos.unpackY(blockPos);
        int z = BlockPos.unpackZ(blockPos);

        long chunk = SectionPos.asLong(SectionPos.toChunk(x), SectionPos.toChunk(y), SectionPos.toChunk(z));

        NibbleArray array = this.getArray(chunk, true);

        if (array == null) {
            return this.getLightWithoutLightmap(blockPos);
        }

        return array.get(SectionPos.mask(x), SectionPos.mask(y), SectionPos.mask(z));
    }

    /**
     * An extremely important optimization is made here in regards to adding items to the pending notification set. The
     * original implementation attempts to add the coordinate of every chunk which contains a neighboring block position
     * even though a huge number of loop iterations will simply map to block positions within the same updating chunk.
     * <p>
     * Our implementation here avoids this by pre-calculating the min/max chunk coordinates so we can iterate over only
     * the relevant chunk positions once. This reduces what would always be 27 iterations to just 1-8 iterations.
     *
     * @reason Use faster implementation
     * @author JellySquid
     */
    @Overwrite
    public void setLight(long blockPos, int value) {
        int x = BlockPos.unpackX(blockPos);
        int y = BlockPos.unpackY(blockPos);
        int z = BlockPos.unpackZ(blockPos);

        long chunkPos = SectionPos.asLong(x >> 4, y >> 4, z >> 4);

        final NibbleArray lightmap = this.getOrAddLightmap(chunkPos);
        final int oldVal = lightmap.get(x & 15, y & 15, z & 15);

        this.beforeLightChange(blockPos, oldVal, value, lightmap);
        this.changeLightmapComplexity(chunkPos, this.getLightmapComplexityChange(blockPos, oldVal, value, lightmap));

        if (this.dirtyCachedSections.add(chunkPos)) {
            this.cachedLightData.copyArray(chunkPos);
        }

        NibbleArray nibble = this.getArray(chunkPos, true);
        nibble.set(x & 15, y & 15, z & 15, value);

        for (int z2 = (z - 1) >> 4; z2 <= (z + 1) >> 4; ++z2) {
            for (int x2 = (x - 1) >> 4; x2 <= (x + 1) >> 4; ++x2) {
                for (int y2 = (y - 1) >> 4; y2 <= (y + 1) >> 4; ++y2) {
                    this.changedLightPositions.add(SectionPos.asLong(x2, y2, z2));
                }
            }
        }
    }

    /**
     * @author PhiPro
     * @reason Move large parts of the logic to other methods
     */
    @Overwrite
    public void setLevel(long id, int level) {
        int oldLevel = this.getLevel(id);

        if (oldLevel != 0 && level == 0) {
            this.activeLightSections.add(id);
            this.addedActiveLightSections.remove(id);
        }

        if (oldLevel == 0 && level != 0) {
            this.activeLightSections.remove(id);
            this.addedEmptySections.remove(id);
        }

        if (oldLevel >= 2 && level < 2) {
            this.nonOptimizableSections.add(id);

            if (this.enabledChunks.contains(SectionPos.toSectionColumnPos(id)) && !this.vanillaLightmapsToRemove.remove(id) && this.getArray(id, true) == null) {
                this.cachedLightData.setArray(id, this.createTrivialVanillaLightmap(id));
                this.dirtyCachedSections.add(id);
                this.cachedLightData.invalidateCaches();
            }
        }

        if (oldLevel < 2 && level >= 2) {
            this.nonOptimizableSections.remove(id);

            if (this.enabledChunks.contains(id)) {
                final NibbleArray lightmap = this.getArray(id, true);

                if (lightmap != null && ((IReadonly) lightmap).isReadonly()) {
                    this.vanillaLightmapsToRemove.add(id);
                }
            }
        }
    }

    /**
     * @reason Drastically improve efficiency by making removals O(n) instead of O(16*16*16)
     * @author JellySquid
     */
    @Inject(method = "cancelSectionUpdates", at = @At("HEAD"), cancellable = true)
    protected void preRemoveSection(LightEngine<?, ?> provider, long pos, CallbackInfo ci) {
        if (provider instanceof LightProviderUpdateTracker) {
            ((LightProviderUpdateTracker) provider).cancelUpdatesForChunk(pos);

            ci.cancel();
        }
    }

    /**
     * @author PhiPro
     * @reason Re-implement completely
     */
    @Overwrite
    public void updateSections(LightEngine<M, ?> chunkLightProvider, boolean doSkylight, boolean skipEdgeLightPropagation) {
        if (!this.hasSectionsToUpdate()) {
            return;
        }

        this.initializeChunks();
        this.addQueuedLightmaps(chunkLightProvider);
        this.removeTrivialLightmaps(chunkLightProvider);
        this.removeVanillaLightmaps(chunkLightProvider);

        final LongIterator it;

        if (!skipEdgeLightPropagation) {
            it = this.newArrays.keySet().iterator();
        } else {
            it = this.field_241536_n_.iterator();
        }

        while (it.hasNext()) {
            func_241538_b_(chunkLightProvider, it.nextLong());
        }

        this.field_241536_n_.clear();
        this.newArrays.clear();

        // Vanilla would normally iterate back over the map of light arrays to remove those we worked on, but
        // that is unneeded now because we removed them earlier.

        this.hasSectionsToUpdate = false;
    }

    /**
     * @reason Avoid integer boxing, reduce map lookups and iteration as much as possible
     * @author JellySquid
     */
    @Overwrite
    private void func_241538_b_(LightEngine<M, ?> chunkLightProvider, long pos) {
        if (this.hasSection(pos)) {
            int x = SectionPos.toWorld(SectionPos.extractX(pos));
            int y = SectionPos.toWorld(SectionPos.extractX(pos));
            int z = SectionPos.toWorld(SectionPos.extractX(pos));

            for (Direction dir : DIRECTIONS) {
                long adjPos = SectionPos.withOffset(pos, dir);

                // Avoid updating initializing chunks unnecessarily
                if (this.newArrays.containsKey(adjPos)) {
                    continue;
                }

                // If there is no light data for this section yet, skip it
                if (!this.hasSection(adjPos)) {
                    continue;
                }

                for (int u1 = 0; u1 < 16; ++u1) {
                    for (int u2 = 0; u2 < 16; ++u2) {
                        long a;
                        long b;

                        switch (dir) {
                            case DOWN:
                                a = BlockPos.pack(x + u2, y, z + u1);
                                b = BlockPos.pack(x + u2, y - 1, z + u1);
                                break;
                            case UP:
                                a = BlockPos.pack(x + u2, y + 15, z + u1);
                                b = BlockPos.pack(x + u2, y + 16, z + u1);
                                break;
                            case NORTH:
                                a = BlockPos.pack(x + u1, y + u2, z);
                                b = BlockPos.pack(x + u1, y + u2, z - 1);
                                break;
                            case SOUTH:
                                a = BlockPos.pack(x + u1, y + u2, z + 15);
                                b = BlockPos.pack(x + u1, y + u2, z + 16);
                                break;
                            case WEST:
                                a = BlockPos.pack(x, y + u1, z + u2);
                                b = BlockPos.pack(x - 1, y + u1, z + u2);
                                break;
                            case EAST:
                                a = BlockPos.pack(x + 15, y + u1, z + u2);
                                b = BlockPos.pack(x + 16, y + u1, z + u2);
                                break;
                            default:
                                continue;
                        }

                        ((LightInitializer) chunkLightProvider).spreadLightInto(a, b);
                    }
                }
            }
        }
    }

    /**
     * @reason
     * @author JellySquid
     */
    @Overwrite
    public void updateAndNotify() {
        if (!this.dirtyCachedSections.isEmpty()) {
            // This could result in changes being flushed to various arrays, so write lock.
            long stamp = this.uncachedLightArraysLock.writeLock();

            try {
                // This only performs a shallow copy compared to before
                M map = this.cachedLightData.copy();
                map.disableCaching();

                this.uncachedLightData = map;
            } finally {
                this.uncachedLightArraysLock.unlockWrite(stamp);
            }

            this.dirtyCachedSections.clear();
        }

        if (!this.changedLightPositions.isEmpty()) {
            LongIterator it = this.changedLightPositions.iterator();

            while(it.hasNext()) {
                long pos = it.nextLong();

                this.chunkProvider.markLightChanged(this.type, SectionPos.from(pos));
            }

            this.changedLightPositions.clear();
        }
    }

    @Override
    public M getStorage() {
        return this.uncachedLightData;
    }

    @Override
    public StampedLock getStorageLock() {
        return this.uncachedLightArraysLock;
    }

    @Override
    public int getLightWithoutLightmap(final long blockPos) {
        return 0;
    }

    @Unique
    protected void beforeChunkEnabled(final long chunkPos) {
    }

    @Unique
    protected void afterChunkDisabled(final long chunkPos) {
    }

    @Unique
    protected final LongSet enabledChunks = new LongOpenHashSet();
    @Unique
    protected final Long2IntMap lightmapComplexities = setDefaultReturnValue(new Long2IntOpenHashMap(), -1);

    @Unique
    private final LongSet markedEnabledChunks = new LongOpenHashSet();
    @Unique
    private final LongSet trivialLightmaps = new LongOpenHashSet();
    @Unique
    private final LongSet vanillaLightmapsToRemove = new LongOpenHashSet();

    // This is put here since the relevant methods to overwrite are located in LightStorage
    @Unique
    protected LongSet nonOptimizableSections = new LongOpenHashSet();

    @Unique
    private static Long2IntMap setDefaultReturnValue(final Long2IntMap map, final int rv) {
        map.defaultReturnValue(rv);
        return map;
    }

    @Unique
    protected NibbleArray getOrAddLightmap(final long sectionPos) {
        NibbleArray lightmap = this.getArray(sectionPos, true);

        if (lightmap == null) {
            lightmap = this.getOrCreateArray(sectionPos);
        } else {
            if (((IReadonly) lightmap).isReadonly()) {
                lightmap = lightmap.copy();
                this.vanillaLightmapsToRemove.remove(sectionPos);
            } else {
                return lightmap;
            }
        }

        this.cachedLightData.setArray(sectionPos, lightmap);
        this.cachedLightData.invalidateCaches();
        this.dirtyCachedSections.add(sectionPos);

        this.addSection(sectionPos);
        this.setLightmapComplexity(sectionPos, 0);

        return lightmap;
    }

    @Unique
    protected void setLightmapComplexity(final long sectionPos, final int complexity) {
        int oldComplexity = this.lightmapComplexities.put(sectionPos, complexity);

        if (oldComplexity == 0) {
            this.trivialLightmaps.remove(sectionPos);
        }

        if (complexity == 0) {
            this.trivialLightmaps.add(sectionPos);
            this.markForLightUpdates();
        }
    }

    @Unique
    private void markForLightUpdates() {
        // Avoid volatile writes
        if (!this.hasSectionsToUpdate) {
            this.hasSectionsToUpdate = true;
        }
    }

    @Unique
    protected void changeLightmapComplexity(final long sectionPos, final int amount) {
        int complexity = this.lightmapComplexities.get(sectionPos);

        if (complexity == 0) {
            this.trivialLightmaps.remove(sectionPos);
        }

        complexity += amount;
        this.lightmapComplexities.put(sectionPos, complexity);

        if (complexity == 0) {
            this.trivialLightmaps.add(sectionPos);
            this.markForLightUpdates();
        }
    }

    @Unique
    protected NibbleArray getLightmap(final long sectionPos) {
        final NibbleArray lightmap = this.getArray(sectionPos, true);
        return lightmap == null || ((IReadonly) lightmap).isReadonly() ? null : lightmap;
    }

    @Unique
    protected boolean hasLightmap(final long sectionPos) {
        return this.getLightmap(sectionPos) != null;
    }

    /**
     * Set up lightmaps and adjust complexities as needed for the given light change.
     * Actions are only required for other affected positions, not for the given <code>blockPos</code> directly.
     */
    @Unique
    protected void beforeLightChange(final long blockPos, final int oldVal, final int newVal, final NibbleArray lightmap) {
    }

    @Unique
    protected int getLightmapComplexityChange(final long blockPos, final int oldVal, final int newVal, final NibbleArray lightmap) {
        return 0;
    }

    /**
     * Set up lightmaps and adjust complexities as needed for the given lightmap change.
     * Actions are only required for other affected sections, not for the given <code>sectionPos</code> directly.
     */
    @Unique
    protected void beforeLightmapChange(final long sectionPos, final NibbleArray oldLightmap, final NibbleArray newLightmap) {
    }

    @Unique
    protected int getInitialLightmapComplexity(final long sectionPos, final NibbleArray lightmap) {
        return 0;
    }

    /**
     * Determines whether light updates should be propagated into the given section.
     * @author PhiPro
     * @reason Method completely changed. Allow child mixins to properly extend this.
     */
    @Overwrite
    public boolean hasSection(final long sectionPos) {
        return this.enabledChunks.contains(SectionPos.toSectionColumnPos(sectionPos));
    }

    @Shadow
    protected abstract void setColumnEnabled(long columnPos, boolean enabled);

    @Override
    @Invoker("setColumnEnabled")
    public abstract void invokeSetColumnEnabled(final long chunkPos, final boolean enabled);

    @Override
    public void enableLightUpdates(final long chunkPos) {
        if (!this.enabledChunks.contains(chunkPos)){
            this.markedEnabledChunks.add(chunkPos);
            this.markForLightUpdates();
        }
    }

    @Unique
    private void initializeChunks() {
        this.cachedLightData.invalidateCaches();

        for (final LongIterator it = this.markedEnabledChunks.iterator(); it.hasNext(); ) {
            final long chunkPos = it.nextLong();

            this.beforeChunkEnabled(chunkPos);

            // First need to register all lightmaps via onLoadSection() as this data is needed for calculating the initial complexity

            for (int i = -1; i < 17; ++i) {
                final long sectionPos = SectionPos.asLong(SectionPos.extractX(chunkPos), i, SectionPos.extractZ(chunkPos));

                if (this.hasLightmap(sectionPos)) {
                    this.addSection(sectionPos);
                }
            }

            // Now the initial complexities can be computed

            for (int i = -1; i < 17; ++i) {
                final long sectionPos = SectionPos.asLong(SectionPos.extractX(chunkPos), i, SectionPos.extractZ(chunkPos));

                if (this.hasLightmap(sectionPos)) {
                    this.setLightmapComplexity(sectionPos, this.getInitialLightmapComplexity(sectionPos, this.getArray(sectionPos, true)));
                }
            }

            // Add lightmaps for vanilla compatibility and try to recover stripped data from vanilla saves

            for (int i = -1; i < 17; ++i) {
                final long sectionPos = SectionPos.asLong(SectionPos.extractX(chunkPos), i, SectionPos.extractZ(chunkPos));

                if (this.nonOptimizableSections.contains(sectionPos) && this.getArray(sectionPos, true) == null) {
                    this.cachedLightData.setArray(sectionPos, this.createInitialVanillaLightmap(sectionPos));
                    this.dirtyCachedSections.add(sectionPos);
                }
            }

            this.enabledChunks.add(chunkPos);
        }

        this.cachedLightData.invalidateCaches();

        this.markedEnabledChunks.clear();
    }

    @Unique
    protected NibbleArray createInitialVanillaLightmap(final long sectionPos) {
        return this.createTrivialVanillaLightmap(sectionPos);
    }

    @Unique
    protected NibbleArray createTrivialVanillaLightmap(final long sectionPos) {
        return new EmptyChunkNibbleArray();
    }

    @Override
    public void disableChunkLight(final long chunkPos, final LightEngine<?, ?> lightProvider) {
        if (this.markedEnabledChunks.remove(chunkPos) || !this.enabledChunks.contains(chunkPos)) {
            for (int i = -1; i < 17; ++i) {
                final long sectionPos = SectionPos.asLong(SectionPos.extractX(chunkPos), i, SectionPos.extractZ(chunkPos));

                if (this.cachedLightData.removeArray(sectionPos) != null) {
                    this.dirtyCachedSections.add(sectionPos);
                }
            }

            this.setColumnEnabled(chunkPos, false);
        } else {
            // First need to remove all pending light updates before changing any light value

            for (int i = -1; i < 17; ++i) {
                final long sectionPos = SectionPos.asLong(SectionPos.extractX(chunkPos), i, SectionPos.extractZ(chunkPos));

                if (this.hasSection(sectionPos)) {
                    this.cancelSectionUpdates(lightProvider, sectionPos);
                }
            }

            // Now the chunk can be disabled

            this.enabledChunks.remove(chunkPos);

            // Now lightmaps can be removed

            int sections = 0;

            for (int i = -1; i < 17; ++i) {
                final long sectionPos = SectionPos.asLong(SectionPos.extractX(chunkPos), i, SectionPos.extractZ(chunkPos));

                this.newArrays.remove(sectionPos);

                if (this.removeLightmap(sectionPos)) {
                    sections |= 1 << (i + 1);
                }
            }

            // Calling onUnloadSection() after removing all the lightmaps is slightly more efficient

            this.cachedLightData.invalidateCaches();

            for (int i = -1; i < 17; ++i) {
                if ((sections & (1 << (i + 1))) != 0) {
                    this.removeSection(SectionPos.asLong(SectionPos.extractX(chunkPos), i, SectionPos.extractZ(chunkPos)));
                }
            }

            this.setColumnEnabled(chunkPos, false);
            this.afterChunkDisabled(chunkPos);
        }
    }

    /**
     * Removes the lightmap associated to the provided <code>sectionPos</code>, but does not call {@link #removeSection(long)} or {@link LightDataMap#invalidateCaches()}
     * @return Whether a lightmap was removed
     */
    @Unique
    protected boolean removeLightmap(final long sectionPos) {
        if (this.cachedLightData.removeArray(sectionPos) == null) {
            return false;
        }

        this.dirtyCachedSections.add(sectionPos);

        if (this.lightmapComplexities.remove(sectionPos) == -1) {
            this.vanillaLightmapsToRemove.remove(sectionPos);
            return false;
        } else {
            this.trivialLightmaps.remove(sectionPos);
            return true;
        }
    }

    @Unique
    private void removeTrivialLightmaps(final LightEngine<?, ?> lightProvider) {
        for (final LongIterator it = this.trivialLightmaps.iterator(); it.hasNext(); ) {
            final long sectionPos = it.nextLong();

            this.cachedLightData.removeArray(sectionPos);
            this.lightmapComplexities.remove(sectionPos);
            this.dirtyCachedSections.add(sectionPos);
        }

        this.cachedLightData.invalidateCaches();

        // Calling onUnloadSection() after removing all the lightmaps is slightly more efficient

        for (final LongIterator it = this.trivialLightmaps.iterator(); it.hasNext(); ) {
            this.removeSection(it.nextLong());
        }

        // Add trivial lightmaps for vanilla compatibility

        for (final LongIterator it = this.trivialLightmaps.iterator(); it.hasNext(); ) {
            final long sectionPos = it.nextLong();

            if (this.nonOptimizableSections.contains(sectionPos)) {
                this.cachedLightData.setArray(sectionPos, this.createTrivialVanillaLightmap(sectionPos));
            }
        }

        this.cachedLightData.invalidateCaches();

        // Remove pending light updates for sections that no longer support light propagations

        for (final LongIterator it = this.trivialLightmaps.iterator(); it.hasNext(); ) {
            final long sectionPos = it.nextLong();

            if (!this.hasSection(sectionPos)) {
                this.cancelSectionUpdates(lightProvider, sectionPos);
            }
        }

        this.trivialLightmaps.clear();
    }

    @Unique
    private void removeVanillaLightmaps(final LightEngine<?, ?> lightProvider) {
        for (final LongIterator it = this.vanillaLightmapsToRemove.iterator(); it.hasNext(); ) {
            final long sectionPos = it.nextLong();

            this.cachedLightData.removeArray(sectionPos);
            this.dirtyCachedSections.add(sectionPos);
        }

        this.cachedLightData.invalidateCaches();

        // Remove pending light updates for sections that no longer support light propagations

        for (final LongIterator it = this.vanillaLightmapsToRemove.iterator(); it.hasNext(); ) {
            final long sectionPos = it.nextLong();

            if (!this.hasSection(sectionPos)) {
                this.cancelSectionUpdates(lightProvider, sectionPos);
            }
        }

        this.vanillaLightmapsToRemove.clear();
    }

    @Unique
    private void addQueuedLightmaps(final LightEngine<?, ?> lightProvider) {
        for (final ObjectIterator<Long2ObjectMap.Entry<NibbleArray>> it = Long2ObjectMaps.fastIterator(this.newArrays); it.hasNext(); ) {
            final Long2ObjectMap.Entry<NibbleArray> entry = it.next();

            final long sectionPos = entry.getLongKey();
            final NibbleArray lightmap = entry.getValue();

            final NibbleArray oldLightmap = this.getLightmap(sectionPos);

            if (lightmap != oldLightmap) {
                this.cancelSectionUpdates(lightProvider, sectionPos);

                this.beforeLightmapChange(sectionPos, oldLightmap, lightmap);

                this.cachedLightData.setArray(sectionPos, lightmap);
                this.cachedLightData.invalidateCaches();
                this.dirtyCachedSections.add(sectionPos);

                if (oldLightmap == null) {
                    this.addSection(sectionPos);
                }

                this.vanillaLightmapsToRemove.remove(sectionPos);
                this.setLightmapComplexity(sectionPos, this.getInitialLightmapComplexity(sectionPos, lightmap));
            }
        }
    }

    /**
     * @author PhiPro
     * @reason Add lightmaps for disabled chunks directly to the world
     */
    @Overwrite
    public void setData(final long sectionPos, final NibbleArray array, final boolean bl) {
        final boolean chunkEnabled = this.enabledChunks.contains(SectionPos.toSectionColumnPos(sectionPos));

        if (array != null) {
            if (chunkEnabled) {
                this.newArrays.put(sectionPos, array);
                this.markForLightUpdates();
            } else {
                this.cachedLightData.setArray(sectionPos, array);
                this.dirtyCachedSections.add(sectionPos);
            }

            if (!bl) {
                this.field_241536_n_.add(sectionPos);
            }
        } else {
            if (chunkEnabled) {
                this.newArrays.remove(sectionPos);
            } else {
                this.cachedLightData.removeArray(sectionPos);
                this.dirtyCachedSections.add(sectionPos);
            }
        }
    }

    // Queued lightmaps are only added to the world via updateLightmaps()
    @Redirect(
        method = "getOrCreateArray(J)Lnet/minecraft/world/chunk/NibbleArray;",
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/minecraft/world/lighting/SectionLightStorage;newArrays:Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;",
                opcode = Opcodes.GETFIELD
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;get(J)Ljava/lang/Object;",
            ordinal = 0,
            remap = false
        )
    )
    private Object cancelLightmapLookupFromQueue(final Long2ObjectMap<NibbleArray> lightmapArray, final long pos) {
        return null;
    }

    @Redirect(
        method = "getLevel(J)I",
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/minecraft/world/lighting/SectionLightStorage;cachedLightData:Lnet/minecraft/world/lighting/LightDataMap;",
                opcode = Opcodes.GETFIELD
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/lighting/LightDataMap;hasArray(J)Z",
            ordinal = 0
        )
    )
    private boolean isNonOptimizable(final LightDataMap<?> lightmapArray, final long sectionPos) {
        return this.nonOptimizableSections.contains(sectionPos);
    }
}
