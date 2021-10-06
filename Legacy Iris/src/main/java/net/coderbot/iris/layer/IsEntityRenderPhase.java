package net.coderbot.iris.layer;

//import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.renderer.RenderState;

public class IsEntityRenderPhase extends RenderState {
	public static final IsEntityRenderPhase INSTANCE = new IsEntityRenderPhase();

	private IsEntityRenderPhase() {
		super("iris:is_entity", GbufferPrograms::beginEntities, GbufferPrograms::endEntities);
	}
}
