package net.coderbot.iris.mixin.rendertype;

import net.minecraft.client.renderer.RenderState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

@OnlyIn(Dist.CLIENT)
@Mixin(RenderType.class)
public class MixinFixEyesTranslucency {
	// Minecraft interprets an alpha value of zero as a signal to disable the alpha test.
	// However, we actually want to reject all nonzero alpha values.
	//
	// Thus, Float.MIN_VALUE allows us to use such a ridiculously tiny value (1.4E-45F) that it is for all intents and
	// purposes zero, except when it comes to Minecraft's hardcoded `alpha > 0.0` check. Otherwise, it works just fine
	// for the alpha test.
	@Unique
	private static final RenderState.AlphaState REJECT_ZERO_ALPHA = new RenderState.AlphaState(Float.MIN_VALUE);

	@Redirect(method = "eyes", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/RenderType$State$Builder;setTransparencyState(Lnet/minecraft/client/renderer/RenderState$TransparencyState;)Lnet/minecraft/client/renderer/RenderType$State$Builder;"))
	private static RenderType.State.Builder iris$fixEyesTranslucency(RenderType.State.Builder instance, RenderState.TransparencyState ignored) {
		return instance.setTransparencyState(RenderStateShardAccessor.getTranslucentTransparency()).setAlphaState(REJECT_ZERO_ALPHA);
	}
}
