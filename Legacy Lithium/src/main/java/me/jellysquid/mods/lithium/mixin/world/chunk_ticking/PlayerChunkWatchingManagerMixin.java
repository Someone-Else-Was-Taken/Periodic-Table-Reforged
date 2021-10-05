package me.jellysquid.mods.lithium.mixin.world.chunk_ticking;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import me.jellysquid.mods.lithium.common.world.PlayerChunkWatchingManagerIterable;
import net.minecraft.entity.player.ServerPlayerEntity;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.server.world.PlayerChunkWatchingManager;
import net.minecraft.world.chunk.PlayerGenerationTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerGenerationTracker.class)
public class PlayerChunkWatchingManagerMixin implements PlayerChunkWatchingManagerIterable {
    @Shadow
    @Final
    private Object2BooleanMap<ServerPlayerEntity> generatingPlayers;

    @Override
    public Iterable<ServerPlayerEntity> getPlayers() {
        return this.generatingPlayers.keySet();
    }
}
