package me.jellysquid.mods.sodium.mixin.features.chunk_rendering;

import me.jellysquid.mods.sodium.client.world.ClientWorldExtended;
import me.jellysquid.mods.sodium.client.world.SodiumChunkManager;
import net.minecraft.client.multiplayer.ClientChunkProvider;
//import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.play.ClientPlayNetHandler;
//import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.renderer.WorldRenderer;
//import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
//import net.minecraft.util.profiler.Profiler;
//import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
//import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld implements ClientWorldExtended {
    private long biomeSeed;

    /**
     * Captures the biome generation seed so that our own caches can make use of it.
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(ClientPlayNetHandler netHandler, ClientWorld.ClientWorldInfo properties, RegistryKey<World> worldKey,
                      DimensionType dimensionType, int loadDistance,
                      Supplier<IProfiler> profiler, WorldRenderer renderer, boolean debugWorld, long seed,
                      CallbackInfo ci) {
        this.biomeSeed = seed;
    }

    /**
     * Replace the client world chunk manager with our own implementation that is both faster and contains additional
     * features needed to pull off event-based rendering.
     */
    @Dynamic
    @Redirect(method = "<init>", at = @At(value = "NEW", target = "net/minecraft/client/multiplayer/ClientChunkProvider"))
    private static ClientChunkProvider redirectCreateChunkManager(ClientWorld world, int renderDistance) {
        return new SodiumChunkManager(world, renderDistance);
    }

    @Override
    public long getBiomeSeed() {
        return this.biomeSeed;
    }
}
