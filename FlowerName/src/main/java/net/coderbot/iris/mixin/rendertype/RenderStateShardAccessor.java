package net.coderbot.iris.mixin.rendertype;

import net.minecraft.client.renderer.RenderState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.client.renderer.RenderStateShard;

@OnlyIn(Dist.CLIENT)
@Mixin(RenderState.class)
public interface RenderStateShardAccessor {
	@Accessor("name")
	String getName();

	@Accessor("TRANSLUCENT_TRANSPARENCY")
	static RenderState.TransparencyState getTranslucentTransparency() {
		return null;
	}
}
