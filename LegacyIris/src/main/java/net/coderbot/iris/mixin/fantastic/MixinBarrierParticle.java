package net.coderbot.iris.mixin.fantastic;

import net.coderbot.iris.fantastic.IrisParticleRenderTypes;
//import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BarrierParticle;
import net.minecraft.client.particle.IParticleRenderType;
//import net.minecraft.client.particle.ParticleRenderType;
//import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.util.IItemProvider;
//import net.minecraft.world.item.BlockItem;
//import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BarrierParticle.class)
public class MixinBarrierParticle {
	@Unique
	private boolean isOpaque;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void iris$resolveTranslucency(ClientWorld level, double x, double y, double z, IItemProvider itemConvertible, CallbackInfo ci) {
		if (itemConvertible instanceof BlockItem) {
			BlockItem blockItem = (BlockItem) itemConvertible;

			RenderType type = RenderTypeLookup.getChunkRenderType(blockItem.getBlock().defaultBlockState());

			if (type == RenderType.solid() || type == RenderType.cutout() || type == RenderType.cutoutMipped()) {
				isOpaque = true;
			}
		}
	}

	@Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
	private void iris$overrideParticleRenderType(CallbackInfoReturnable<IParticleRenderType> cir) {
		if (isOpaque) {
			cir.setReturnValue(IrisParticleRenderTypes.OPAQUE_TERRAIN);
		}
	}
}
