package me.jellysquid.mods.lithium.mixin.world.mob_spawning;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
//import net.minecraft.structure.StructurePiece;
//import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
//import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
//import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StructureManager.class)
public abstract class StructureAccessorMixin {
    @Shadow
    @Final
    private IWorld world;

    /**
     * @reason Avoid heavily nested stream code and object allocations where possible
     * @author JellySquid
     */
    @Overwrite
    public StructureStart<?> getStructureStart(BlockPos blockPos, boolean fine, Structure<?> feature) {
        IChunk originChunk = this.world.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.STRUCTURE_REFERENCES);

        LongSet references = originChunk.func_230346_b_(feature);
        LongIterator iterator = references.iterator();

        while (iterator.hasNext()) {
            long pos = iterator.nextLong();

            IChunk chunk = this.world.getChunk(ChunkPos.getX(pos), ChunkPos.getZ(pos), ChunkStatus.STRUCTURE_STARTS);
            StructureStart<?> structure = chunk.func_230342_a_(feature);

            if (structure == null || !structure.isRefCountBelowMax() || !structure.getBoundingBox().isVecInside(blockPos)) {
                continue;
            }

            if (!fine || this.anyPieceContainsPosition(structure, blockPos)) {
                return structure;
            }
        }

        return StructureStart.DUMMY;
    }

    private boolean anyPieceContainsPosition(StructureStart<?> structure, BlockPos blockPos) {
        for (StructurePiece piece : structure.getComponents()) {
            if (piece.getBoundingBox().isVecInside(blockPos)) {
                return true;
            }
        }

        return false;
    }
}