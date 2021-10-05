package net.coderbot.iris.fantastic;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
//import net.minecraft.client.particle.ParticleTextureSheet;
//import net.minecraft.client.render.BufferBuilder;
//import net.minecraft.client.render.Tessellator;
//import net.minecraft.client.render.VertexFormats;
//import net.minecraft.client.texture.SpriteAtlasTexture;
//import net.minecraft.client.texture.TextureManager;

public class IrisParticleTextureSheets {
	public static final IParticleRenderType OPAQUE_TERRAIN_SHEET = new IParticleRenderType() {
		public void beginRender(BufferBuilder bufferBuilder, TextureManager textureManager) {
			RenderSystem.disableBlend();
			RenderSystem.depthMask(true);
			RenderSystem.defaultAlphaFunc();
			textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
			bufferBuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		}

		public void finishRender(Tessellator tessellator) {
			tessellator.draw();
		}

		public String toString() {
			return "OPAQUE_TERRAIN_SHEET";
		}
	};
}
