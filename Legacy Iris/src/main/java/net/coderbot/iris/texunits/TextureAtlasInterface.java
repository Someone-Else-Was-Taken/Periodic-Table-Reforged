package net.coderbot.iris.texunits;

import net.minecraft.util.math.vector.Vector2f;
//import net.minecraft.world.phys.Vec2;

public interface TextureAtlasInterface {
	void setAtlasSize(int sizeX, int sizeY);
	Vector2f getAtlasSize();
}
