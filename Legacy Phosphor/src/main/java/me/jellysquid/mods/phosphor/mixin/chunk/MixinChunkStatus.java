package me.jellysquid.mods.phosphor.mixin.chunk;

import com.mojang.datafixers.util.Either;
import me.jellysquid.mods.phosphor.common.chunk.light.ServerLightingProviderAccess;
//import net.minecraft.server.world.ChunkHolder;
//import net.minecraft.server.world.ServerLightingProvider;
//import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerWorldLightManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

@Mixin(ChunkStatus.class)
public class MixinChunkStatus {
    @Shadow
    private static ChunkStatus register(String id, ChunkStatus previous, int taskMargin, EnumSet<Heightmap.Type> heightMapTypes, ChunkStatus.Type chunkType, ChunkStatus.IGenerationWorker task, ChunkStatus.ILoadingWorker noGenTask) {
        return null;
    }

    @Shadow
    @Final
    private static ChunkStatus.ILoadingWorker NOOP_LOADING_WORKER;

    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(
        method = "<clinit>",
        slice = @Slice(
            from = @At(value = "CONSTANT", args = "stringValue=features")
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/ChunkStatus;register(Ljava/lang/String;Lnet/minecraft/world/chunk/ChunkStatus;ILjava/util/EnumSet;Lnet/minecraft/world/chunk/ChunkStatus$Type;Lnet/minecraft/world/chunk/ChunkStatus$IGenerationWorker;)Lnet/minecraft/world/chunk/ChunkStatus;",
            ordinal = 0
        )
    )
    private static ChunkStatus injectLightmapSetup(final String id, final ChunkStatus previous, final int taskMargin, final EnumSet<Heightmap.Type> heightMapTypes, final ChunkStatus.Type chunkType, final ChunkStatus.IGenerationWorker task) {
        return register(id, previous, taskMargin, heightMapTypes, chunkType,
            (status, world, generator, structureManager, lightingProvider, function, surroundingChunks, chunk) ->
                task.doWork(status, world, generator, structureManager, lightingProvider, function, surroundingChunks, chunk).thenCompose(
                    either -> getPreLightFuture(lightingProvider, either)
                ),
            (status, world, structureManager, lightingProvider, function, chunk) ->
                NOOP_LOADING_WORKER.doWork(status, world, structureManager, lightingProvider, function, chunk).thenCompose(
                    either -> getPreLightFuture(lightingProvider, either)
                )
            );
    }

    @Unique
    private static CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> getPreLightFuture(final ServerWorldLightManager lightingProvider, final Either<IChunk, ChunkHolder.IChunkLoadingError> either) {
        return either.map(
            chunk -> getPreLightFuture(lightingProvider, chunk),
            unloaded -> CompletableFuture.completedFuture(Either.right(unloaded))
        );
    }

    @Unique
    private static CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> getPreLightFuture(final ServerWorldLightManager lightingProvider, final IChunk chunk) {
        return ((ServerLightingProviderAccess) lightingProvider).setupLightmaps(chunk).thenApply(Either::left);
    }
}
