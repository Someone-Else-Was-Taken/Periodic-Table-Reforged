package net.coderbot.iris.layer;

import java.util.Objects;
import java.util.Optional;

import net.coderbot.batchedentityrendering.impl.WrappableRenderLayer;
import net.coderbot.iris.mixin.renderlayer.RenderLayerAccessor;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.RenderLayer;

import javax.annotation.Nullable;

public class IrisRenderLayerWrapper extends RenderType implements WrappableRenderLayer {
	private final UseProgramRenderPhase useProgram;
	private final RenderType wrapped;

	public IrisRenderLayerWrapper(String name, RenderType wrapped, UseProgramRenderPhase useProgram) {
		super(name, wrapped.getVertexFormat(), wrapped.getDrawMode(), wrapped.getBufferSize(),
			wrapped.isUseDelegate(), isTranslucent(wrapped), wrapped::setupRenderState, wrapped::clearRenderState);

		this.useProgram = useProgram;
		this.wrapped = wrapped;
	}

	@Override
	public void setupRenderState() {
		super.setupRenderState();

		useProgram.setupRenderState();
	}

	@Override
	public void clearRenderState() {
		useProgram.clearRenderState();

		super.clearRenderState();
	}

	@Override
	public RenderType unwrap() {
		return this.wrapped;
	}

	@Override
	public Optional<RenderType> getOutline() {
		return this.wrapped.getOutline();
	}

	@Override
	public boolean isColoredOutlineBuffer() {
		return this.wrapped.isColoredOutlineBuffer();
	}

	@Override
	public boolean equals(@Nullable Object object) {
		if (object == null) {
			return false;
		}

		if (object.getClass() != this.getClass()) {
			return false;
		}

		IrisRenderLayerWrapper other = (IrisRenderLayerWrapper) object;

		return Objects.equals(this.wrapped, other.wrapped) && Objects.equals(this.useProgram, other.useProgram);
	}

	@Override
	public int hashCode() {
		// Add one so that we don't have the exact same hash as the wrapped object.
		// This means that we won't have a guaranteed collision if we're inserted to a map alongside the unwrapped object.
		return this.wrapped.hashCode() + 1;
	}

	@Override
	public String toString() {
		return "iris:" + this.wrapped.toString();
	}

	private static boolean isTranslucent(RenderType layer) {
		return ((RenderLayerAccessor) layer).isTranslucent();
	}
}
