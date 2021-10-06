package net.coderbot.iris.layer;

import net.coderbot.batchedentityrendering.impl.WrappableRenderLayer;
import net.coderbot.iris.mixin.renderlayer.RenderLayerAccessor;
//import net.minecraft.client.render.RenderLayer;
//import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
//import org.jetbrains.annotations.Nullable;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class OuterWrappedRenderLayer extends RenderType implements WrappableRenderLayer {
	private final RenderState extra;
	private final RenderType wrapped;

	public OuterWrappedRenderLayer(String name, RenderType wrapped, RenderState extra) {
		super(name, wrapped.getVertexFormat(), wrapped.getDrawMode(), wrapped.getBufferSize(),
			wrapped.isUseDelegate(), isTranslucent(wrapped), wrapped::setupRenderState, wrapped::clearRenderState);

		this.extra = extra;
		this.wrapped = wrapped;
	}

	@Override
	public void setupRenderState() {
		extra.setupRenderState();

		super.setupRenderState();
	}

	@Override
	public void clearRenderState() {
		super.clearRenderState();

		extra.clearRenderState();
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

		OuterWrappedRenderLayer other = (OuterWrappedRenderLayer) object;

		return Objects.equals(this.wrapped, other.wrapped) && Objects.equals(this.extra, other.extra);
	}

	@Override
	public int hashCode() {
		// Add one so that we don't have the exact same hash as the wrapped object.
		// This means that we won't have a guaranteed collision if we're inserted to a map alongside the unwrapped object.
		return this.wrapped.hashCode() + 1;
	}

	@Override
	public String toString() {
		return "iris_wrapped:" + this.wrapped.toString();
	}

	private static boolean isTranslucent(RenderType layer) {
		return ((RenderLayerAccessor) layer).isTranslucent();
	}
}
