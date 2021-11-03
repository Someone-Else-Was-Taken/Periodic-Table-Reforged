package net.coderbot.iris.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Iterator;
import java.util.Objects;

import net.coderbot.iris.layer.GbufferProgram;
import net.coderbot.iris.layer.GbufferPrograms;
import net.coderbot.iris.pipeline.DeferredWorldRenderingPipeline;
import net.coderbot.iris.texunits.TextureUnit;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL15;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
//import net.minecraft.client.Camera;
import net.minecraft.client.particle.Particle;
//import net.minecraft.client.particle.ParticleEngine;
//import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;

@Mixin(ParticleManager.class)
@OnlyIn(Dist.CLIENT)
public class MixinParticleEngine {
	private static final String RENDER_PARTICLES = "Lnet/minecraft/client/particle/ParticleManager;render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/ActiveRenderInfo;F)V";
	private static final String DRAW = "Lnet/minecraft/client/particle/IParticleRenderType;end(Lnet/minecraft/client/renderer/Tessellator;)V";

	@Unique
	private IParticleRenderType lastSheet;

	@Inject(method = "renderParticles", at = @At("HEAD"), remap = false)
	private void iris$beginDrawingParticles(MatrixStack crashreportcategory, IRenderTypeBuffer.Impl throwable, LightTexture particle, ActiveRenderInfo tessellator, float bufferbuilder, ClippingHelper iterable, CallbackInfo ci) {
		GbufferPrograms.push(GbufferProgram.TEXTURED_LIT);
	}


	@ModifyConstant(method = "lambda$renderParticles$9", constant = @Constant(intValue = GL15.GL_TEXTURE2), require = 1)
	private static int iris$fixOverlayTextureUnit(int texUnit) {
		return TextureUnit.LIGHTMAP.getUnitId();
	}



	@Inject(method = "renderParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/IParticleRenderType;end(Lnet/minecraft/client/renderer/Tessellator;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void iris$preDrawParticleSheet(MatrixStack crashreportcategory, IRenderTypeBuffer.Impl throwable, LightTexture particle, ActiveRenderInfo camera, float bufferbuilder, ClippingHelper iterable, CallbackInfo ci, Runnable runnable, Iterator<IParticleRenderType> sheets, IParticleRenderType sheet, Iterable<Particle> particles) {
		GbufferPrograms.push(DeferredWorldRenderingPipeline.getProgramForSheet(sheet));

		if (lastSheet != null) {
			throw new IllegalStateException("Particle rendering in weird state: lastSheet != null, lastSheet = " + lastSheet);
		}

		lastSheet = sheet;
	}

	@Inject(method = "renderParticles", at = @At(value = "INVOKE", target = DRAW, shift = At.Shift.AFTER))
	private void iris$postDrawParticleSheet(MatrixStack crashreportcategory, IRenderTypeBuffer.Impl throwable, LightTexture particle, ActiveRenderInfo tessellator, float bufferbuilder, ClippingHelper iterable, CallbackInfo ci) {
		GbufferPrograms.pop(DeferredWorldRenderingPipeline.getProgramForSheet(Objects.requireNonNull(lastSheet)));
		lastSheet = null;
	}

	@Inject(method = "renderParticles", at = @At("RETURN"), remap = false)
	private void iris$finishDrawingParticles(MatrixStack crashreportcategory, IRenderTypeBuffer.Impl throwable, LightTexture particle, ActiveRenderInfo tessellator, float bufferbuilder, ClippingHelper iterable, CallbackInfo ci) {
		GbufferPrograms.pop(GbufferProgram.TEXTURED_LIT);
	}
}
