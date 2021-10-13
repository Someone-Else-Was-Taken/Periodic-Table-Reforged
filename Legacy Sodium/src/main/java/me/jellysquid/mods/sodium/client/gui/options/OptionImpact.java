package me.jellysquid.mods.sodium.client.gui.options;

//import net.minecraft.util.Formatting;
import net.minecraft.util.text.TextFormatting;

public enum OptionImpact {
    LOW(TextFormatting.GREEN, "Low"),
    MEDIUM(TextFormatting.YELLOW, "Medium"),
    HIGH(TextFormatting.GOLD, "High"),
    EXTREME(TextFormatting.RED, "Extreme"),
    VARIES(TextFormatting.WHITE, "Varies");

    private final TextFormatting color;
    private final String text;

    OptionImpact(TextFormatting color, String text) {
        this.color = color;
        this.text = text;
    }

    public String toDisplayString() {
        return this.color + this.text;
    }
}
