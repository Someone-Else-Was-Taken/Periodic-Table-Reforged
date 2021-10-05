package me.jellysquid.mods.lithium.mixin.world.chunk_ticking;

import me.jellysquid.mods.lithium.common.world.PlayerChunkWatchingManagerIterable;
import net.minecraft.entity.Entity;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.server.world.PlayerChunkWatchingManager;
//import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.PlayerGenerationTracker;
import net.minecraft.world.server.ChunkManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkManager.class)
public abstract class ThreadedAnvilChunkStorageMixin {
    @Shadow
    @Final
    private ChunkManager.ProxyTicketManager ticketManager;

    @Shadow
    @Final
    private PlayerGenerationTracker playerGenerationTracker;

    @Shadow
    private static double getDistanceSquaredToChunk(ChunkPos pos, Entity entity) {
        throw new UnsupportedOperationException();
    }

    /**
     * The usage of stream code here can be rather costly, as this method will be called for every loaded chunk each
     * tick in order to determine if a player is close enough to allow for mob spawning. This implementation avoids
     * object allocations and uses a traditional iterator based approach, providing a significant boost to how quickly
     * the game can tick chunks.
     *
     * @reason Use optimized implementation
     * @author JellySquid
     */
    @Overwrite
    @SuppressWarnings("ConstantConditions")
    public boolean isOutsideSpawningRadius(ChunkPos pos) {
        long key = pos.asLong();

        if (!this.ticketManager.isOutsideSpawningRadius(key)) {
            return true;
        }

        for (ServerPlayerEntity player : ((PlayerChunkWatchingManagerIterable) (Object) this.playerGenerationTracker).getPlayers()) {
            // [VanillaCopy] Only non-spectator players within 128 blocks of the chunk can enable mob spawning
            if (!player.isSpectator() && getDistanceSquaredToChunk(pos, player) < 16384.0D) {
                return false;
            }
        }

        // No matching players were nearby, so mobs cannot currently be spawned here
        return true;
    }
}
