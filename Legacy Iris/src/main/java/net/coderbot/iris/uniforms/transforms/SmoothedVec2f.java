package net.coderbot.iris.uniforms.transforms;

import net.coderbot.iris.uniforms.FrameUpdateNotifier;
import net.minecraft.util.math.vector.Vector2f;
//import net.minecraft.world.phys.Vec2;
import java.util.function.Supplier;

public class SmoothedVec2f implements Supplier<Vector2f> {
	private final SmoothedFloat x;
	private final SmoothedFloat y;

	public SmoothedVec2f(float halfLife, Supplier<Vector2f> unsmoothed, FrameUpdateNotifier updateNotifier) {
		x = new SmoothedFloat(halfLife, () -> unsmoothed.get().x, updateNotifier);
		y = new SmoothedFloat(halfLife, () -> unsmoothed.get().y, updateNotifier);
	}

	@Override
	public Vector2f get() {
		return new Vector2f(x.getAsFloat(), y.getAsFloat());
	}
}
