package me.jellysquid.mods.hydrogen.common;

import me.jellysquid.mods.hydrogen.client.HydrogenClientPreLaunch;
import me.jellysquid.mods.hydrogen.common.jvm.ClassConstructors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("hydrogen")
public class HydrogenMod {
    public static final Logger LOGGER = LogManager.getLogger("Hydrogen");


    public HydrogenMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::cliSetup);
    }


    public void setup(final FMLCommonSetupEvent event) {
        ClassConstructors.init();

    }
    @OnlyIn(Dist.CLIENT)
    public void cliSetup(final FMLCommonSetupEvent event) {
        HydrogenClientPreLaunch.init();

    }
}
