package net.coderbot.iris.gui.option;

import net.minecraft.client.GameSettings;
//import net.minecraft.client.Options;
//import net.minecraft.client.ProgressOption;
//import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.settings.SliderPercentageOption;
//import net.minecraft.network.chat.Component;
import net.minecraft.util.text.ITextComponent;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ShadowDistanceOption extends SliderPercentageOption {

	public ShadowDistanceOption(String string, double d, double e, float f, Function<GameSettings, Double> function, BiConsumer<GameSettings, Double> biConsumer, BiFunction<GameSettings, SliderPercentageOption, ITextComponent> biFunction) {
		super(string, d, e, f, function, biConsumer, biFunction);
	}

	@Override
	public Widget createButton(GameSettings options, int x, int y, int width) {
		Widget widget = new ShadowDistanceSliderButton(options, x, y, width, 20, this);

		widget.active = IrisVideoSettings.isShadowDistanceSliderEnabled();

		return widget;
	}
}
