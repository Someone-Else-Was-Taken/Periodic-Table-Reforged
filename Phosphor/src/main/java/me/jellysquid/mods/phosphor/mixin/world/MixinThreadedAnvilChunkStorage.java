package me.jellysquid.mods.phosphor.mixin.world;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;

import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.datafixers.util.Either;

import me.jellysquid.mods.phosphor.common.world.ThreadedAnvilChunkStorageAccess;
//import net.minecraft.server.world.ChunkHolder;
//import net.minecraft.server.world.ChunkHolder.Unloaded;
//import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
//import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

@Mixin(ChunkManager.class)
public abstract class MixinThreadedAnvilChunkStorage implements ThreadedAnvilChunkStorageAccess {
    @Shadow
    protected abstract CompletableFuture<Either<List<Chunk>, ChunkHolder.IChunkLoadingError>> func_219236_a(final ChunkPos centerChunk, final int margin, final IntFunction<ChunkStatus> distanceToStatus);

    @Override
    @Invoker("func_219209_c")
    public abstract void invokeReleaseLightTicket(ChunkPos pos);

    @Shadow
    @Final
    private ThreadTaskExecutor<Runnable> mainThread;

    @Redirect(
        method = "func_222961_b(Lnet/minecraft/world/server/ChunkHolder;)Ljava/util/concurrent/CompletableFuture;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/server/ChunkHolder;func_219276_a(Lnet/minecraft/world/chunk/ChunkStatus;Lnet/minecraft/world/server/ChunkManager;)Ljava/util/concurrent/CompletableFuture;"
        )
    )
    private CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> enforceNeighborsLoaded(final ChunkHolder holder, final ChunkStatus targetStatus, final ChunkManager chunkStorage) {
        return holder.func_219276_a(ChunkStatus.FULL, (ChunkManager) (Object) this).thenComposeAsync(
            either -> either.map(
                chunk -> this.func_219236_a(holder.getPosition(), 1, ChunkStatus::getStatus).thenApply(
                    either_ -> either_.mapLeft(list -> list.get(list.size() / 2))
                ),
                unloaded -> CompletableFuture.completedFuture(Either.right(unloaded))
            ),
            this.mainThread
        );
    }
}
