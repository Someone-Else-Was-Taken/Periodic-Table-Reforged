package me.jellysquid.mods.lithium.mixin.ai.poi.fast_init;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import me.jellysquid.mods.lithium.common.world.interests.PointOfInterestTypeHelper;
//import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
//import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.village.PointOfInterestData;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.storage.RegionSectionCache;
//import net.minecraft.world.poi.PointOfInterestSet;
//import net.minecraft.world.poi.PointOfInterestStorage;
//import net.minecraft.world.poi.PointOfInterestType;
//import net.minecraft.world.storage.SerializingRegionBasedStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Mixin(PointOfInterestManager.class)
public abstract class PointOfInterestStorageMixin extends RegionSectionCache<PointOfInterestData> {
    public PointOfInterestStorageMixin(File directory, Function<Runnable, Codec<PointOfInterestData>> function, Function<Runnable, PointOfInterestData> function2, DataFixer dataFixer, DefaultTypeReferences dataFixTypes, boolean bl) {
        super(directory, function, function2, dataFixer, dataFixTypes, bl);
    }

    @Shadow
    protected abstract void updateFromSelection(ChunkSection section, SectionPos sectionPos, BiConsumer<BlockPos, PointOfInterestType> entryConsumer);

    /**
     * @reason Avoid Stream API
     * @author Jellysquid
     */
    @Overwrite
    public void checkConsistencyWithBlocks(ChunkPos chunkPos_1, ChunkSection section) {
        SectionPos sectionPos = SectionPos.from(chunkPos_1, section.getYLocation() >> 4);

        PointOfInterestData set = this.func_219113_d(sectionPos.asLong()).orElse(null);

        if (set != null) {
            set.refresh((consumer) -> {
                if (PointOfInterestTypeHelper.shouldScan(section)) {
                    this.updateFromSelection(section, sectionPos, consumer);
                }
            });
        } else {
            if (PointOfInterestTypeHelper.shouldScan(section)) {
                set = this.func_235995_e_(sectionPos.asLong());

                this.updateFromSelection(section, sectionPos, set::add);
            }
        }
    }
}