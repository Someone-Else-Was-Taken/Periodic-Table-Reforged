package me.jellysquid.mods.sodium.client.gui.options;

//import net.minecraft.util.Formatting;
import net.minecraft.util.text.TextFormatting;

public enum OptionImpact {
    LOW(TextFormatting.GREEN, "低"),
    MEDIUM(TextFormatting.YELLOW, "中"),
    HIGH(TextFormatting.GOLD, "高"),
    EXTREME(TextFormatting.RED, "极高"),
    VARIES(TextFormatting.WHITE, "视情况而定");

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
