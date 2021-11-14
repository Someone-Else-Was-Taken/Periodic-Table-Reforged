package net.coderbot.iris.fantastic;

import com.mojang.blaze3d.systems.RenderSystem;
//import com.mojang.blaze3d.vertex.BufferBuilder;
//import com.mojang.blaze3d.vertex.DefaultVertexFormat;
//import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.particle.IParticleRenderType;
//import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
//import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class IrisParticleRenderTypes {
	public static final IParticleRenderType OPAQUE_TERRAIN = new IParticleRenderType() {
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			RenderSystem.disableBlend();
			RenderSystem.depthMask(true);
			RenderSystem.defaultAlphaFunc();
			textureManager.bind(AtlasTexture.LOCATION_BLOCKS);
			bufferBuilder.begin(7, DefaultVertexFormats.PARTICLE);
		}

		public void end(Tessellator tesselator) {
			tesselator.end();
		}

		public String toString() {
			return "OPAQUE_TERRAIN_SHEET";
		}
	};
}
