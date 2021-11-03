package net.coderbot.iris.mixin.gui;

import net.coderbot.iris.gui.option.IrisVideoSettings;
import net.coderbot.iris.gui.option.ShaderPackSelectionButtonOption;
import net.minecraft.client.AbstractOption;
//import net.minecraft.client.Option;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.VideoSettingsScreen;
//import net.minecraft.client.gui.screens.Screen;
//import net.minecraft.client.gui.screens.VideoSettingsScreen;

//import net.minecraft.network.chat.Component;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(VideoSettingsScreen.class)
public abstract class MixinVideoSettingsScreen extends Screen {
	protected MixinVideoSettingsScreen(ITextComponent title) {
		super(title);
	}

	@ModifyArg(
			method = "init",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/widget/list/OptionsRowList;addSmall([Lnet/minecraft/client/AbstractOption;)V"
			),
			index = 0
	)
	private AbstractOption[] iris$addShaderPackScreenButton(AbstractOption[] old) {
		AbstractOption[] options = new AbstractOption[old.length + 2];
		System.arraycopy(old, 0, options, 0, old.length);
		options[options.length - 2] = new ShaderPackSelectionButtonOption((VideoSettingsScreen)(Object)this, this.minecraft);
		options[options.length - 1] = IrisVideoSettings.RENDER_DISTANCE;
		return options;
	}
}
