package net.coderbot.iris.mixin.texunits;

import net.coderbot.iris.texunits.TextureUnit;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.lwjgl.opengl.GL15;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen {
	@ModifyConstant(method = "render", constant = @Constant(intValue = GL15.GL_TEXTURE2), require = 1)
	private static int iris$fixLightmapTextureUnit(int texUnit) {
		return TextureUnit.LIGHTMAP.getUnitId();
	}
}
