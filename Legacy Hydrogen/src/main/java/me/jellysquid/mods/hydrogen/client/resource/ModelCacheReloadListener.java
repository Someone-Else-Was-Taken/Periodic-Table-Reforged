package me.jellysquid.mods.hydrogen.client.resource;

import com.google.common.collect.Lists;
//import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
//import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.profiler.IProfiler;
//import net.minecraft.resource.ResourceManager;
import net.minecraft.resources.IResourceManager;
//import net.minecraft.util.Identifier;
import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.profiler.Profiler;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
/*
public class ModelCacheReloadListener implements SimpleResourceReloadListener<Void> {
    @Override
    public CompletableFuture<Void> load(IResourceManager manager, IProfiler profiler, Executor executor) {
        ModelCaches.cleanCaches();

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> apply(Void data, IResourceManager manager, IProfiler profiler, Executor executor) {
        ModelCaches.printDebug();

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation("hydrogen", "model_cache_stats");
    }

    @Override
    public Collection<ResourceLocation> getFabricDependencies() {
        return Lists.newArrayList(ResourceReloadListenerKeys.MODELS);
    }

}

 */