package net.coderbot.iris.uniforms;

import static net.coderbot.iris.gl.uniform.UniformUpdateFrequency.ONCE;
import static net.coderbot.iris.gl.uniform.UniformUpdateFrequency.PER_FRAME;

import java.util.function.Supplier;

import net.coderbot.iris.gl.uniform.UniformHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3d;
//import net.minecraft.world.phys.Vec3;

/**
 * @see <a href="https://github.com/IrisShaders/ShaderDoc/blob/master/uniforms.md#camera">Uniforms: Camera</a>
 */
public class CameraUniforms {
	private static final Minecraft client = Minecraft.getInstance();

	private CameraUniforms() {
	}

	public static void addCameraUniforms(UniformHolder uniforms, FrameUpdateNotifier notifier) {
		uniforms
			.uniform1f(ONCE, "near", () -> 0.05)
			.uniform1f(PER_FRAME, "far", CameraUniforms::getRenderDistanceInBlocks)
			.uniform3d(PER_FRAME, "cameraPosition", CameraUniforms::getCameraPosition)
			.uniform3d(PER_FRAME, "previousCameraPosition", new PreviousCameraPosition(notifier));
	}

	private static int getRenderDistanceInBlocks() {
		return client.options.renderDistance * 16;
	}

	public static Vector3d getCameraPosition() {
		return client.gameRenderer.getMainCamera().getPosition();
	}

	private static class PreviousCameraPosition implements Supplier<Vector3d> {
		private Vector3d previousCameraPosition = new Vector3d(0.0, 0.0, 0.0);
		private Vector3d currentCameraPosition = new Vector3d(0.0, 0.0, 0.0);

		private PreviousCameraPosition(FrameUpdateNotifier notifier) {
			notifier.addListener(this::update);
		}

		private void update() {
			previousCameraPosition = currentCameraPosition;
			currentCameraPosition = getCameraPosition();
		}

		@Override
		public Vector3d get() {
			return previousCameraPosition;
		}
	}
}
