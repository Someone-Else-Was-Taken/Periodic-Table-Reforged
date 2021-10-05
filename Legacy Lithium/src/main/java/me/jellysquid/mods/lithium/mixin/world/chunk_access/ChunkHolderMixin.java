package me.jellysquid.mods.lithium.mixin.world.chunk_access;

import com.mojang.datafixers.util.Either;
import me.jellysquid.mods.lithium.common.world.chunk.ChunkHolderExtended;
//import net.minecraft.server.world.ChunkHolder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ChunkHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReferenceArray;

@Mixin(ChunkHolder.class)
public class ChunkHolderMixin implements ChunkHolderExtended {
    @Shadow
    @Final
    private AtomicReferenceArray<CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>>> field_219312_g;

    private long lastRequestTime;

    @Override
    public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> getFutureByStatus(int index) {
        return this.field_219312_g.get(index);
    }

    /**
     * Updates the future for the status at ordinal {@param index}.
     *
     * @param index
     * @param future
     */
    @Override
    public void setFutureForStatus(int index, CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> future) {
        this.field_219312_g.set(index, future);
    }



    @Override
    public boolean updateLastAccessTime(long time) {
        long prev = this.lastRequestTime;
        this.lastRequestTime = time;

        return prev != time;
    }
}
