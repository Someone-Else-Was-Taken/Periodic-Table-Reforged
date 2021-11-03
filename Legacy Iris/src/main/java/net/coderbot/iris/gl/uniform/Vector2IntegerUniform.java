package net.coderbot.iris.gl.uniform;


import net.minecraft.util.math.vector.Vector2f;
import org.lwjgl.opengl.GL20;

import java.util.function.Supplier;
//import net.minecraft.world.phys.Vec2;

public class Vector2IntegerUniform extends Uniform {
	private Vector2f cachedValue;
	private final Supplier<Vector2f> value;

	Vector2IntegerUniform(int location, Supplier<Vector2f> value) {
		super(location);

		this.cachedValue = null;
		this.value = value;
	}

	@Override
	public void update() {
		Vector2f newValue = value.get();

		if (cachedValue == null || !newValue.equals(cachedValue)) {
			cachedValue = newValue;
			GL20.glUniform2i(this.location, (int) newValue.x, (int) newValue.y);
		}
	}
}
