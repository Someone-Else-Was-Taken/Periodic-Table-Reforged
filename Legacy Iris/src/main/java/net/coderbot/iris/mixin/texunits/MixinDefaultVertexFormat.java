package net.coderbot.iris.mixin.texunits;

//import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.coderbot.iris.texunits.TextureUnit;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;

@Mixin(DefaultVertexFormats.class)
@OnlyIn(Dist.CLIENT)
public class MixinDefaultVertexFormat {
	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 1), require = 1, slice = @Slice(
		from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/vertex/DefaultVertexFormats;ELEMENT_UV0:Lnet/minecraft/client/renderer/vertex/VertexFormatElement;"),
		to = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/vertex/DefaultVertexFormats;ELEMENT_UV1:Lnet/minecraft/client/renderer/vertex/VertexFormatElement;")
	))
	private static int iris$fixOverlayTextureUnit(int samplerId) {
		return TextureUnit.OVERLAY.getSamplerId();
	}

	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 2, ordinal = 0), require = 1, allow = 1, slice = @Slice(
		from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/vertex/DefaultVertexFormats;ELEMENT_UV1:Lnet/minecraft/client/renderer/vertex/VertexFormatElement;"),
		to = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/vertex/DefaultVertexFormats;ELEMENT_UV2:Lnet/minecraft/client/renderer/vertex/VertexFormatElement;")
	))
	private static int iris$fixLightmapTextureUnit(int samplerId) {
		return TextureUnit.LIGHTMAP.getSamplerId();
	}
}
