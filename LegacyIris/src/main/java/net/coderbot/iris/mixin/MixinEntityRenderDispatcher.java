package net.coderbot.iris.mixin;

//import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.coderbot.iris.Iris;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.level.LevelReader;

@OnlyIn(Dist.CLIENT)
@Mixin(EntityRendererManager.class)
public class MixinEntityRenderDispatcher {
	private static final String RENDER_SHADOW =
		"Lnet/minecraft/client/renderer/entity/EntityRendererManager;renderShadow(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/entity/Entity;FFLnet/minecraft/world/IWorldReader;F)V";

	@Inject(method = RENDER_SHADOW, at = @At("HEAD"), cancellable = true)
	private static void iris$maybeSuppressEntityShadow(MatrixStack poseStack, IRenderTypeBuffer bufferSource,
													   Entity entity, float opacity, float tickDelta, IWorldReader level,
													   float radius, CallbackInfo ci) {
		if (Iris.getPipelineManager().getPipeline().shouldDisableVanillaEntityShadows()) {
			ci.cancel();
		}
	}
}
