package net.coderbot.iris.mixin;

import net.coderbot.iris.Iris;
//import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.AlertScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
//import net.minecraft.client.gui.screens.AlertScreen;
//import net.minecraft.client.gui.screens.TitleScreen;
//import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MainMenuScreen.class)
public class MixinTitleScreen {
	@Inject(method = "init", at = @At("RETURN"))
	public void iris$showSodiumIncompatScreen(CallbackInfo ci) {
		if (Iris.isSodiumInvalid()) {
			Minecraft.getInstance().setScreen(new AlertScreen(
					Minecraft.getInstance()::stop,
					new TranslationTextComponent("iris.sodium.failure.title").withStyle(TextFormatting.RED),
					new TranslationTextComponent("iris.sodium.failure.reason"),
					new TranslationTextComponent("menu.quit")));
		}
	}
}
