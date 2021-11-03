package net.coderbot.iris.layer;

import net.minecraft.client.renderer.RenderState;
//import net.minecraft.client.renderer.RenderStateShard;

public class IsEntityRenderStateShard extends RenderState {
	public static final IsEntityRenderStateShard INSTANCE = new IsEntityRenderStateShard();

	private IsEntityRenderStateShard() {
		super("iris:is_entity", GbufferPrograms::beginEntities, GbufferPrograms::endEntities);
	}
}
