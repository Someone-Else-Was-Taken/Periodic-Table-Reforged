package net.coderbot.iris.gui.option;

import net.coderbot.iris.gui.screen.ShaderPackScreen;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
//import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.client.gui.widget.AbstractButtonWidget;
//import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.OptionButton;
//import net.minecraft.client.options.GameOptions;
//import net.minecraft.client.options.Option;
//import net.minecraft.text.TranslatableText;
import net.minecraft.util.text.TranslationTextComponent;

public class ShaderPackSelectionButtonOption extends AbstractOption {
	private final Screen parent;
	private final Minecraft client;

	public ShaderPackSelectionButtonOption(Screen parent, Minecraft client) {
		super("options.iris.shaderPackSelection");
		this.parent = parent;
		this.client = client;
	}

	@Override
	public AbstractButton createWidget(GameSettings options, int x, int y, int width) {
		return new OptionButton(
				x, y, width, 20,
				this,
				new TranslationTextComponent("options.iris.shaderPackSelection"),
				button -> client.displayGuiScreen(new ShaderPackScreen(parent))
		);
	}
}
