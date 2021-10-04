package me.jellysquid.mods.hydrogen.common;

import me.jellysquid.mods.hydrogen.common.jvm.ClassConstructors;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("hydrogen")
public class HydrogenMod {
    public static final Logger LOGGER = LogManager.getLogger("Hydrogen");
    public HydrogenMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;


    }
}
