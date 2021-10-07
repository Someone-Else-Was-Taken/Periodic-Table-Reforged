package me.jellysquid.mods.lithium.common;

import me.jellysquid.mods.lithium.common.config.LithiumConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import net.fabricmc.api.ModInitializer;

@Mod("lithium")
public class LithiumMod {

    public static LithiumConfig CONFIG;

    public static final Logger LOGGER = LogManager.getLogger("Lithium");
    public LithiumMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;


    }

    public void onInitialize() {
        if (CONFIG == null) {
            throw new IllegalStateException("The mixin plugin did not initialize the config! Did it not load?");
        }
    }
}
