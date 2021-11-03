package net.coderbot.iris.mixin;

import net.coderbot.iris.samplers.TextureAtlasTracker;
import net.coderbot.iris.texunits.TextureAtlasInterface;
//import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.Texture;
//import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.util.math.vector.Vector2f;
//import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AtlasTexture.class)
public abstract class MixinTextureAtlas extends Texture implements TextureAtlasInterface {
	@Unique
	private Vector2f atlasSize;

	@Override
	public int getId() {
		int id = super.getId();

		TextureAtlasTracker.INSTANCE.trackAtlas(id, (AtlasTexture) (Object) this);

		return id;
	}

	@Override
	public void setAtlasSize(int sizeX, int sizeY) {
		if (sizeX == 0 && sizeY == 0) {
			this.atlasSize = Vector2f.ZERO;
		} else {
			this.atlasSize = new Vector2f(sizeX, sizeY);
		}
	}

	@Override
	public Vector2f getAtlasSize() {
		return this.atlasSize;
	}
}

