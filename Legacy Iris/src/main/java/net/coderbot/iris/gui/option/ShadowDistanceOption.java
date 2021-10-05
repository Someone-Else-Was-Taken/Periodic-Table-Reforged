package net.coderbot.iris.gui.option;

//import net.coderbot.iris.Iris;
//import net.coderbot.iris.pipeline.WorldRenderingPipeline;
//import net.minecraft.client.gui.widget.AbstractButtonWidget;
//import net.minecraft.client.options.DoubleOption;
//import net.minecraft.client.options.GameOptions;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.ITextComponent;
//import net.minecraft.text.Text;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ShadowDistanceOption extends SliderPercentageOption {
	public ShadowDistanceOption(String key, double min, double max, float step, Function<GameSettings, Double> getter, BiConsumer<GameSettings, Double> setter, BiFunction<GameSettings, SliderPercentageOption, ITextComponent> displayStringGetter) {
		super(key, min, max, step, getter, setter, displayStringGetter);
	}

	@Override
	public ShadowDistanceSliderWidget createWidget(GameSettings options, int x, int y, int width) {
		ShadowDistanceSliderWidget widget = new ShadowDistanceSliderWidget(options, x, y, width, 20, this);

		widget.active = IrisVideoSettings.isShadowDistanceSliderEnabled();

		return widget;
	}
}
