package me.jellysquid.mods.lithium.mixin.world.chunk_access;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
//import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Implement the interface members of {@link WorldView} and {@link CollisionView} directly to avoid complicated
 * method invocations between interface boundaries, helping the JVM to inline and optimize code.
 */
@Mixin(World.class)
public abstract class WorldMixin implements IWorld {
    /**
     * @reason Remove dynamic-dispatch and inline call
     * @author JellySquid
     */
    @Overwrite
    public Chunk getChunkAt(BlockPos pos) {
        return (Chunk) this.getChunk(pos);
    }

    @Override
    public IChunk getChunk(BlockPos pos) {
        return this.getChunkLithium(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, true);
    }

    /**
     * @reason Remove dynamic-dispatch and inline call
     * @author JellySquid
     */
    @Override
    @Overwrite
    public Chunk getChunk(int chunkX, int chunkZ) {
        return (Chunk) this.getChunkLithium(chunkX, chunkZ, ChunkStatus.FULL, true);
    }

    @Override
    public IChunk getChunk(int chunkX, int chunkZ, ChunkStatus status) {
        return this.getChunkLithium(chunkX, chunkZ, status, true);
    }

    @Override
    public IBlockReader getBlockReader(int chunkX, int chunkZ) {
        return this.getChunkLithium(chunkX, chunkZ, ChunkStatus.FULL, false);
    }

    private IChunk getChunkLithium(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        IChunk chunk = this.getChunkProvider().getChunk(chunkX, chunkZ, leastStatus, create);

        if (chunk == null && create) {
            throw new IllegalStateException("Should always be able to create a chunk!");
        } else {
            return chunk;
        }
    }
}
