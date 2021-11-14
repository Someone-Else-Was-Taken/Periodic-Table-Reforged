package net.coderbot.batchedentityrendering.mixin;

import net.minecraft.client.renderer.RenderState;
//import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderType.State.class)
public interface CompositeStateAccessor {
	@Accessor("transparencyState")
	RenderState.TransparencyState getTransparency();
}
