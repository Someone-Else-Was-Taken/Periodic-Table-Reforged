package net.coderbot.iris.mixin.rendertype;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.RenderStateShard;

@OnlyIn(Dist.CLIENT)
@Mixin(RenderStateShard.class)
public interface RenderStateShardAccessor {
	@Accessor("name")
	String getName();

	@Accessor("TRANSLUCENT_TRANSPARENCY")
	static RenderStateShard.TransparencyStateShard getTranslucentTransparency() {
		return null;
	}
}
