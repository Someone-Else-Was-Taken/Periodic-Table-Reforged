package me.jellysquid.mods.sodium.client.gui.options.binding.compat;

import me.jellysquid.mods.sodium.client.gui.options.binding.OptionBinding;
import net.minecraft.client.GameSettings;
import net.minecraft.client.settings.BooleanOption;

public class VanillaBooleanOptionBinding implements OptionBinding<GameSettings, Boolean> {
    private final BooleanOption option;

    public VanillaBooleanOptionBinding(BooleanOption option) {
        this.option = option;
    }

    @Override
    public void setValue(GameSettings storage, Boolean value) {
        this.option.set(storage, value.toString());
    }

    @Override
    public Boolean getValue(GameSettings storage) {
        return this.option.get(storage);
    }
}
