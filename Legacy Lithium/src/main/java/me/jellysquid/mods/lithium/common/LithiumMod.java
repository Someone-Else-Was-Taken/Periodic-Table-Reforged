package me.jellysquid.mods.lithium.common;

import me.jellysquid.mods.lithium.common.config.LithiumConfig;
import net.minecraftforge.fml.common.Mod;
//import net.fabricmc.api.ModInitializer;

@Mod("lithium")
public class LithiumMod {

    public static LithiumConfig CONFIG;

    public void onInitialize() {
        if (CONFIG == null) {
            throw new IllegalStateException("The mixin plugin did not initialize the config! Did it not load?");
        }
    }
}
