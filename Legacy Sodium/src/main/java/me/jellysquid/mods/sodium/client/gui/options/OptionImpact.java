package me.jellysquid.mods.sodium.client.gui.options;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public enum OptionImpact {
    LOW(TextFormatting.GREEN, "sodium.option_impact.low"),
    MEDIUM(TextFormatting.YELLOW, "sodium.option_impact.medium"),
    HIGH(TextFormatting.GOLD, "sodium.option_impact.high"),
    EXTREME(TextFormatting.RED, "sodium.option_impact.extreme"),
    VARIES(TextFormatting.WHITE, "sodium.option_impact.varies");

    private final TextFormatting color;
    private final String text;

    OptionImpact(TextFormatting color, String text) {
        this.color = color;
        this.text = text;
    }

    public String toDisplayString() {
        return this.color + I18n.format(this.text);
    }
}
