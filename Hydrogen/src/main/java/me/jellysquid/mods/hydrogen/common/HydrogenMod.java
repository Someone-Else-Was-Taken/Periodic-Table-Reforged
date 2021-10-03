package me.jellysquid.mods.hydrogen.common;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
@Mod("hydrogen")
public class HydrogenMod {
    public static final Logger LOGGER = LogManager.getLogger("Hydrogen");

    private static String MOD_VERSION;
    public void setup(final FMLCommonSetupEvent event) {
        MOD_VERSION = ModList.get().getModContainerById("hydrogen").get().getModInfo().getVersion().toString();
    }
}
