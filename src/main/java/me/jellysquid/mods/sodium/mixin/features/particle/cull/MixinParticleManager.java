package me.jellysquid.mods.sodium.mixin.features.particle.cull;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
//import net.minecraft.client.particle.ParticleTextureSheet;
//import net.minecraft.client.render.Camera;
//import net.minecraft.client.render.Frustum;
//import net.minecraft.client.render.LightmapTextureManager;
//import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.ClippingHelper;
//import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.AxisAlignedBB;
//import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

@Mixin(ParticleManager.class)
public class MixinParticleManager {
    @Shadow
    @Final
    private Map<IParticleRenderType, Queue<Particle>> byType;

    private final Queue<Particle> cachedQueue = new ArrayDeque<>();

    private ClippingHelper cullingFrustum;

    @Inject(method = "renderParticles(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/ActiveRenderInfo;FLnet/minecraft/client/renderer/culling/ClippingHelper;)V", at = @At("HEAD"), remap = false)
    private void preRenderParticles(MatrixStack matrixStack, IRenderTypeBuffer.Impl immediate, LightTexture lightmapTextureManager, ActiveRenderInfo camera, float f, ClippingHelper clippingHelper, CallbackInfo ci) {
        ClippingHelper frustum = SodiumWorldRenderer.getInstance().getFrustum();
        boolean useCulling = SodiumClientMod.options().advanced.useParticleCulling;

        // Setup the frustum state before rendering particles
        if (useCulling && frustum != null) {
            this.cullingFrustum = frustum;
        } else {
            this.cullingFrustum = null;
        }
    }

    @SuppressWarnings({ "SuspiciousMethodCalls", "unchecked" })
    @Redirect(method = "renderParticles(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/ActiveRenderInfo;FLnet/minecraft/client/renderer/culling/ClippingHelper;)V", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"), remap = false)
    private <V> V filterParticleList(Map<IParticleRenderType, Queue<Particle>> map, Object key, MatrixStack matrixStack, IRenderTypeBuffer.Impl immediate, LightTexture lightmapTextureManager, ActiveRenderInfo camera, float f, ClippingHelper clippingHelper) {
        Queue<Particle> queue = this.byType.get(key);

        if (queue == null || queue.isEmpty()) {
            return null;
        }

        // If the frustum isn't available (whether disabled or some other issue arose), simply return the queue as-is
        if (this.cullingFrustum == null) {
            return (V) queue;
        }

        // Filter particles which are not visible
        Queue<Particle> filtered = this.cachedQueue;
        filtered.clear();

        for (Particle particle : queue) {
            AxisAlignedBB box = particle.getBoundingBox();

            // Hack: Grow the particle's bounding box in order to work around mis-behaved particles
            if (this.cullingFrustum.isBoxInFrustum(box.minX - 1.0D, box.minY - 1.0D, box.minZ - 1.0D, box.maxX + 1.0D, box.maxY + 1.0D, box.maxZ + 1.0D)) {
                filtered.add(particle);
            }
        }

        return (V) filtered;
    }

    @Inject(method = "renderParticles", at = @At("RETURN"))
    private void postRenderParticles(MatrixStack matrixStack, IRenderTypeBuffer.Impl immediate, LightTexture lightmapTextureManager, ActiveRenderInfo camera, float f, CallbackInfo ci) {
        // Ensure particles don't linger in the temporary collection
        this.cachedQueue.clear();
    }
}
