package net.coderbot.iris.layer;

import net.minecraft.client.renderer.RenderState;
//import net.minecraft.client.renderer.RenderStateShard;

public class IsBlockEntityRenderStateShard extends RenderState {
	public static final IsBlockEntityRenderStateShard INSTANCE = new IsBlockEntityRenderStateShard();

	private IsBlockEntityRenderStateShard() {
		super("iris:is_block_entity", GbufferPrograms::beginBlockEntities, GbufferPrograms::endBlockEntities);
	}
}
