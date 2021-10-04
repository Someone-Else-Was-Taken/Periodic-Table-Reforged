package me.jellysquid.mods.phosphor.mixin.chunk.light;

import me.jellysquid.mods.phosphor.common.chunk.light.InitialLightingAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
//import net.minecraft.util.math.ChunkSectionPos;
//import net.minecraft.world.chunk.light.ChunkLightProvider;
//import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.lighting.LightEngine;
import net.minecraft.world.lighting.WorldLightManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldLightManager.class)
public abstract class MixinLightingProvider implements InitialLightingAccess
{
    @Shadow
    @Final
    private LightEngine<?, ?> blockLight;

    @Shadow
    @Final
    private LightEngine<?, ?> skyLight;

    @Shadow
    public void updateSectionStatus(SectionPos pos, boolean notReady) {
    }

    @Shadow
    public void enableLightSources(ChunkPos pos, boolean lightEnabled) {
    }

    @Shadow
    public void retainData(ChunkPos pos, boolean retainData) {
    }

    @Shadow
    public void onBlockEmissionIncrease(BlockPos pos, int level) {
    }

    @Override
    public void enableSourceLight(final long chunkPos) {
        if (this.blockLight != null) {
            ((InitialLightingAccess) this.blockLight).enableSourceLight(chunkPos);
        }

        if (this.skyLight != null) {
            ((InitialLightingAccess) this.skyLight).enableSourceLight(chunkPos);
        }
    }

    @Override
    public void enableLightUpdates(final long chunkPos) {
        if (this.blockLight != null) {
            ((InitialLightingAccess) this.blockLight).enableLightUpdates(chunkPos);
        }

        if (this.skyLight != null) {
            ((InitialLightingAccess) this.skyLight).enableLightUpdates(chunkPos);
        }
    }
}

