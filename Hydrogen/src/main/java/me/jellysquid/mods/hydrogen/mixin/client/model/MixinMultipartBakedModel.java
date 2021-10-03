package me.jellysquid.mods.hydrogen.mixin.client.model;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import me.jellysquid.mods.hydrogen.common.collections.ImmutablePairArrayList;
import net.minecraft.block.BlockState;
//import net.minecraft.client.render.model.BakedModel;
//import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.MultipartBakedModel;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Mixin(MultipartBakedModel.class)
public class MixinMultipartBakedModel {
    @Mutable
    @Shadow
    @Final
    private List<Pair<Predicate<BlockState>, IBakedModel>> selectors;

    @Mutable
    @Shadow
    @Final
    private Map<BlockState, BitSet> bitSetCache;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void reinit(List<Pair<Predicate<BlockState>, IBakedModel>> components, CallbackInfo ci) {
        this.bitSetCache = new Reference2ObjectOpenHashMap<>(this.bitSetCache);
        this.selectors = new ImmutablePairArrayList<>(this.selectors);
    }
}
