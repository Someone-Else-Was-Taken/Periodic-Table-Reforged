package me.jellysquid.mods.hydrogen.mixin.client.model;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
//import net.minecraft.client.color.block.BlockColors;
//import net.minecraft.client.render.model.BakedModel;
//import net.minecraft.client.render.model.ModelLoader;
//import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.AtlasTexture;
//import net.minecraft.client.texture.SpriteAtlasTexture;
//import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.profiler.IProfiler;
//import net.minecraft.resource.ResourceManager;
import net.minecraft.resources.IResourceManager;
//import net.minecraft.util.Identifier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
//import net.minecraft.util.profiler.Profiler;
import org.apache.commons.lang3.tuple.Triple;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(ModelBakery.class)
public class MixinModelLoader {
    @Mutable
    @Shadow
    @Final
    private Map<ResourceLocation, IUnbakedModel> unbakedModels;

    @Mutable
    @Shadow
    @Final
    private Map<Triple<ResourceLocation, TransformationMatrix, Boolean>, IBakedModel> bakedModels;

    @Mutable
    @Shadow
    @Final
    private Map<ResourceLocation, IBakedModel> topBakedModels;

    @Mutable
    @Shadow
    @Final
    private Map<ResourceLocation, IUnbakedModel> topUnbakedModels;

    @Mutable
    @Shadow
    @Final
    private Set<ResourceLocation> unbakedModelLoadingQueue;

    @Mutable
    @Shadow
    @Final
    private Map<ResourceLocation, Pair<AtlasTexture, AtlasTexture.SheetData>> sheetData;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void reinit(IResourceManager resourceManager, BlockColors blockColors, IProfiler profiler, int i, CallbackInfo ci) {
        this.unbakedModels = new Object2ObjectOpenHashMap<>(this.unbakedModels);
        this.bakedModels = new Object2ObjectOpenHashMap<>(this.bakedModels);
        this.topBakedModels = new Object2ObjectOpenHashMap<>(this.topBakedModels);
        this.topUnbakedModels = new Object2ObjectOpenHashMap<>(this.topUnbakedModels);
        this.unbakedModelLoadingQueue = new ObjectOpenHashSet<>(this.unbakedModelLoadingQueue);
        this.sheetData = new Object2ObjectOpenHashMap<>(this.sheetData);
    }
}
