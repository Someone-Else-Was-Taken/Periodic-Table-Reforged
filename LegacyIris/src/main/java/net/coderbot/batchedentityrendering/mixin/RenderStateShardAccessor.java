package net.coderbot.batchedentityrendering.mixin;

import net.minecraft.client.renderer.RenderState;
//import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderState.class)
public interface RenderStateShardAccessor {
	@Accessor("NO_TRANSPARENCY")
	static RenderState.TransparencyState getNO_TRANSPARENCY() {
		throw new AssertionError();
	}

	@Accessor("GLINT_TRANSPARENCY")
	static RenderState.TransparencyState getGLINT_TRANSPARENCY() {
		throw new AssertionError();
	}

	@Accessor("CRUMBLING_TRANSPARENCY")
	static RenderState.TransparencyState getCRUMBLING_TRANSPARENCY() {
		throw new AssertionError();
	}
}
