package net.coderbot.iris.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
//import com.mojang.blaze3d.vertex.PoseStack;
import net.coderbot.batchedentityrendering.impl.Groupable;
import net.coderbot.iris.layer.EntityColorRenderStateShard;
import net.coderbot.iris.layer.EntityColorMultiBufferSource;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
//import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.TNTMinecartRenderer;
//import net.minecraft.client.renderer.entity.TntMinecartRenderer;
//import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TNTMinecartRenderer.class)
public abstract class MixinTntMinecartRenderer {
	@ModifyVariable(method = "renderWhiteSolidBlock", at = @At("HEAD"))
	private static IRenderTypeBuffer iris$wrapProvider(IRenderTypeBuffer bufferSource, BlockState blockState,
													   MatrixStack poseStack, IRenderTypeBuffer bufferSourceArg, int light,
													   boolean drawFlash) {
		if (!(bufferSource instanceof Groupable)) {
			// Entity color is not supported in this context, no buffering available.
			return bufferSource;
		}

		if (drawFlash) {
			EntityColorRenderStateShard phase = new EntityColorRenderStateShard(false, 1.0F);
			return new EntityColorMultiBufferSource(bufferSource, phase);
		} else {
			return bufferSource;
		}
	}
}
