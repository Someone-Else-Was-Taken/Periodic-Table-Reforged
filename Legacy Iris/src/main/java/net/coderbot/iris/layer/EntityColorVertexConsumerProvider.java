package net.coderbot.iris.layer;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.coderbot.batchedentityrendering.impl.Groupable;
//import net.minecraft.client.render.BufferBuilder;
//import net.minecraft.client.render.RenderLayer;
//import net.minecraft.client.render.VertexConsumer;
//import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;

import java.util.Collections;

public class EntityColorVertexConsumerProvider extends IRenderTypeBuffer.Impl implements Groupable {
	private final IRenderTypeBuffer wrapped;
	private final IRenderTypeBuffer.Impl wrappedImmediate;
	private final Groupable groupable;
	private final EntityColorRenderPhase phase;

	public EntityColorVertexConsumerProvider(IRenderTypeBuffer wrapped, EntityColorRenderPhase phase) {
		super(new BufferBuilder(0), Collections.emptyMap());

		this.wrapped = wrapped;

		if (wrapped instanceof Impl) {
			this.wrappedImmediate = (Impl) wrapped;
		} else {
			this.wrappedImmediate = null;
		}

		if (wrapped instanceof Groupable) {
			this.groupable = (Groupable) wrapped;
		} else {
			this.groupable = null;
		}

		this.phase = phase;
	}

	@Override
	public IVertexBuilder getBuffer(RenderType renderLayer) {
		return wrapped.getBuffer(new InnerWrappedRenderLayer("iris_entity_color", renderLayer, phase));
	}

	@Override
	public void finish() {
		if (wrappedImmediate != null) {
			wrappedImmediate.finish();
		}
	}

	@Override
	public void finish(RenderType layer) {
		if (wrappedImmediate != null) {
			wrappedImmediate.finish(layer);
		}
	}

	@Override
	public void startGroup() {
		if (groupable != null) {
			groupable.startGroup();
		}
	}

	@Override
	public boolean maybeStartGroup() {
		if (groupable != null) {
			return groupable.maybeStartGroup();
		}

		return false;
	}

	@Override
	public void endGroup() {
		if (groupable != null) {
			groupable.endGroup();
		}
	}
}
