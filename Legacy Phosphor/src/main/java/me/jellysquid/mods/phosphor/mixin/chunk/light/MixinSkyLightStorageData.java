package me.jellysquid.mods.phosphor.mixin.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import me.jellysquid.mods.phosphor.common.chunk.light.SharedNibbleArrayMap;
import me.jellysquid.mods.phosphor.common.chunk.light.SkyLightStorageDataAccess;
import me.jellysquid.mods.phosphor.common.chunk.light.SharedSkyLightData;
import me.jellysquid.mods.phosphor.common.util.collections.DoubleBufferedLong2IntHashMap;
//import net.minecraft.world.chunk.ChunkNibbleArray;
//import net.minecraft.world.chunk.ChunkToNibbleArrayMap;
import net.minecraft.world.chunk.NibbleArray;
//import net.minecraft.world.chunk.light.SkyLightStorage;
import net.minecraft.world.lighting.LightDataMap;
import net.minecraft.world.lighting.SkyLightStorage;
import org.spongepowered.asm.mixin.*;

@Mixin(SkyLightStorage.StorageMap.class)
public class MixinSkyLightStorageData extends LightDataMap<SkyLightStorage.StorageMap>
        implements SkyLightStorageDataAccess, SharedSkyLightData {
    @Shadow
    private int minY;

    @Mutable
    @Shadow
    @Final
    private Long2IntOpenHashMap surfaceSections;

    // Our new double-buffered collection
    private DoubleBufferedLong2IntHashMap topArraySectionYQueue;

    // Indicates whether or not the extended data structures have been initialized
    private boolean init;

    protected MixinSkyLightStorageData(Long2ObjectOpenHashMap<NibbleArray> arrays) {
        super(arrays);
    }

    @Override
    public void makeSharedCopy(Long2IntOpenHashMap map, DoubleBufferedLong2IntHashMap queue) {
        this.topArraySectionYQueue = queue;
        this.surfaceSections = map;

        // We need to immediately see all updates on the thread this is being copied to
        if (queue != null) {
            queue.flushChangesSync();
        }

        // Copies of this map should not re-initialize the data structures!
        this.init = true;
    }

    /**
     * @reason Avoid copying large data structures
     * @author JellySquid
     */
    @SuppressWarnings("ConstantConditions")
    @Overwrite
    public SkyLightStorage.StorageMap copy() {
        // This will be called immediately by LightStorage in the constructor
        // We can take advantage of this fact to initialize our extra properties here without additional hacks
        if (!this.init) {
            this.initialize();
        }

        SkyLightStorage.StorageMap data = new SkyLightStorage.StorageMap(this.arrays, this.surfaceSections, this.minY);
        ((SharedSkyLightData) (Object) data).makeSharedCopy(this.surfaceSections, this.topArraySectionYQueue);
        ((SharedNibbleArrayMap) (Object) data).makeSharedCopy((SharedNibbleArrayMap) this);

        return data;
    }

    private void initialize() {
        ((SharedNibbleArrayMap) this).init();

        this.topArraySectionYQueue = new DoubleBufferedLong2IntHashMap();
        this.surfaceSections = this.topArraySectionYQueue.createSyncView();

        this.init = true;
    }

    @Override
    public int getDefaultHeight() {
        return this.minY;
    }

    @Override
    public int getHeight(long pos) {
        return this.topArraySectionYQueue.getAsync(pos);
    }

    @Override
    public void updateMinHeight(final int y) {
        if (this.minY > y) {
            this.minY = y;
            this.surfaceSections.defaultReturnValue(this.minY);
        }
    }
}