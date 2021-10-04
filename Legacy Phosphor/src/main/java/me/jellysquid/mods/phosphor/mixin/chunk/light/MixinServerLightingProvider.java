package me.jellysquid.mods.phosphor.mixin.chunk.light;

import me.jellysquid.mods.phosphor.common.chunk.light.ServerLightingProviderAccess;
import me.jellysquid.mods.phosphor.common.world.ThreadedAnvilChunkStorageAccess;
//import net.minecraft.server.world.ServerLightingProvider;
//import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
//import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerWorldLightManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.CompletableFuture;
import java.util.function.IntSupplier;

@Mixin(ServerWorldLightManager.class)
public abstract class MixinServerLightingProvider extends MixinLightingProvider implements ServerLightingProviderAccess {
    @Shadow
    protected abstract void func_215600_a(int x, int z, IntSupplier completedLevelSupplier, ServerWorldLightManager.Phase stage, Runnable task);

    @Shadow
    protected abstract void func_215586_a(int x, int z, ServerWorldLightManager.Phase stage, Runnable task);

    @Override
    public CompletableFuture<IChunk> setupLightmaps(final IChunk chunk) {
        final ChunkPos chunkPos = chunk.getPos();

        // This evaluates the non-empty subchunks concurrently on the lighting thread...
        this.func_215600_a(chunkPos.x, chunkPos.z, () -> 0, ServerWorldLightManager.Phase.PRE_UPDATE, Util.namedRunnable(() -> {
            final ChunkSection[] chunkSections = chunk.getSections();

            for (int i = 0; i < chunkSections.length; ++i) {
                if (!ChunkSection.isEmpty(chunkSections[i])) {
                    super.updateSectionStatus(SectionPos.from(chunkPos, i), false);
                }
            }

            if (chunk.hasLight()) {
                super.enableSourceLight(SectionPos.toSectionColumnPos(SectionPos.asLong(chunkPos.x, 0, chunkPos.z)));
            }

            super.enableLightUpdates(SectionPos.toSectionColumnPos(SectionPos.asLong(chunkPos.x, 0, chunkPos.z)));
        },
            () -> "setupLightmaps " + chunkPos
        ));

        return CompletableFuture.supplyAsync(() -> {
            super.retainData(chunkPos, false);
            return chunk;
        },
            (runnable) -> this.func_215600_a(chunkPos.x, chunkPos.z, () -> 0, ServerWorldLightManager.Phase.POST_UPDATE, runnable)
        );
    }

    @Shadow
    @Final
    private ChunkManager chunkManager;

    /**
     * @author PhiPro
     * @reason Move parts of the logic to {@link #setupLightmaps(IChunk)}
     */
    @Overwrite
    public CompletableFuture<IChunk> lightChunk(IChunk chunk, boolean excludeBlocks) {
        final ChunkPos chunkPos = chunk.getPos();

        this.func_215586_a(chunkPos.x, chunkPos.z, ServerWorldLightManager.Phase.PRE_UPDATE, Util.namedRunnable(() -> {
            if (!chunk.hasLight()) {
                super.enableSourceLight(SectionPos.toSectionColumnPos(SectionPos.asLong(chunkPos.x, 0, chunkPos.z)));
            }

            if (!excludeBlocks) {
                chunk.getLightSources().forEach((blockPos) -> {
                    super.onBlockEmissionIncrease(blockPos, chunk.getLightValue(blockPos));
                });
            }
        },
            () -> "lightChunk " + chunkPos + " " + excludeBlocks
        ));

        return CompletableFuture.supplyAsync(() -> {
            chunk.setLight(true);
            ((ThreadedAnvilChunkStorageAccess) this.chunkManager).invokeReleaseLightTicket(chunkPos);

            return chunk;
        },
            (runnable) -> this.func_215586_a(chunkPos.x, chunkPos.z, ServerWorldLightManager.Phase.POST_UPDATE, runnable)
        );
    }
}
