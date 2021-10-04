package me.jellysquid.mods.phosphor.common.chunk.light;

import java.util.concurrent.CompletableFuture;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;

public interface ServerLightingProviderAccess {
    CompletableFuture<IChunk> setupLightmaps(IChunk chunk);
}
