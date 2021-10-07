package me.jellysquid.mods.lithium.mixin.client.model;

import me.jellysquid.mods.lithium.client.resource.ModelCaches;
//import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.renderer.model.BakedQuad;
//import net.minecraft.client.texture.Sprite;
//import net.minecraft.util.math.Direction;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BakedQuad.class)
public class MixinBakedQuad {
    @Mutable
    @Shadow
    @Final
    protected int[] vertexData;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void reinit(int[] vertexData, int colorIndex, Direction face, TextureAtlasSprite sprite, boolean shade, CallbackInfo ci) {
        this.vertexData = ModelCaches.QUADS.deduplicate(this.vertexData);
    }
}