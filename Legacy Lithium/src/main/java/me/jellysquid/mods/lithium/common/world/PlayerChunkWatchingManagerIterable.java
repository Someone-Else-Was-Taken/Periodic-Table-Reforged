package me.jellysquid.mods.lithium.common.world;

import net.minecraft.entity.player.ServerPlayerEntity;
//import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerChunkWatchingManagerIterable {
    /**
     * See {@link net.minecraft.world.chunk.PlayerGenerationTracker#getGeneratingPlayers(long)}. The position
     * variant is actually never used (presumably because it's not yet implemented?)
     * <p>
     * TODO: Use an index to avoid iterating over all players on the server
     */
    Iterable<ServerPlayerEntity> getPlayers();
}
