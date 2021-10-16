package net.coderbot.iris.gui.option;

import net.coderbot.iris.gui.screen.ShaderPackScreen;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
//import net.minecraft.client.Option;
//import net.minecraft.client.Options;
//import net.minecraft.client.gui.components.AbstractWidget;
//import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
//import net.minecraft.network.chat.TranslatableComponent;
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
	public Widget createButton(GameSettings options, int x, int y, int width) {
		return new OptionButton(
				x, y, width, 20,
				this,
				new TranslationTextComponent("options.iris.shaderPackSelection"),
				button -> client.setScreen(new ShaderPackScreen(parent))
		);
	}
}
