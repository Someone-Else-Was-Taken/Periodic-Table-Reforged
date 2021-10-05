package net.coderbot.iris.layer;

import net.coderbot.iris.uniforms.CapturedRenderingState;
//import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.renderer.RenderState;

public final class BlockEntityRenderPhase extends RenderState {
	private static final BlockEntityRenderPhase UNIDENTIFIED = new BlockEntityRenderPhase(-1);

	private final int entityId;

	private BlockEntityRenderPhase(int entityId) {
		super("iris:is_block_entity", () -> {
			CapturedRenderingState.INSTANCE.setCurrentBlockEntity(entityId);
			GbufferPrograms.beginBlockEntities();
		}, () -> {
			CapturedRenderingState.INSTANCE.setCurrentBlockEntity(-1);
			GbufferPrograms.endBlockEntities();
		});

		this.entityId = entityId;
	}

	public static BlockEntityRenderPhase forId(int entityId) {
		if (entityId == -1) {
			return UNIDENTIFIED;
		} else {
			// TODO: Cache all created render phases to avoid allocations?
			return new BlockEntityRenderPhase(entityId);
		}
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || object.getClass() != this.getClass()) {
			return false;
		}

		BlockEntityRenderPhase other = (BlockEntityRenderPhase) object;

		return this.entityId == other.entityId;
	}
}
