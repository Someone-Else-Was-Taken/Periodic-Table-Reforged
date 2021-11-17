package me.jellysquid.mods.sodium.client;

//import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
//import me.jellysquid.mods.sodium.client.util.UnsafeUtil;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import me.jellysquid.mods.sodium.client.util.UnsafeUtil;
import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;

@Mod("magnesium")
public class SodiumClientMod {
    public static boolean hasFlywheel;
    public static boolean hasCCBackport;
    public SodiumClientMod() {
        hasFlywheel = ModList.get().isLoaded("flywheel");
        hasCCBackport = ModList.get().isLoaded("cavesandcliffs");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private static SodiumGameOptions CONFIG;
    private static Logger LOGGER;

    private static String MOD_VERSION;

    public void setup(final FMLCommonSetupEvent event) {
        MOD_VERSION = ModList.get().getModContainerById("magnesium").get().getModInfo().getVersion().toString();

    }

    /*
    @Override
    public void onInitializeClient() {
        ModContainer mod = FabricLoader.getInstance()
                .getModContainer("sodium")
                .orElseThrow(NullPointerException::new);

        MOD_VERSION = mod.getMetadata()
                .getVersion()
                .getFriendlyString();
    }
     */

    public static SodiumGameOptions options() {
        if (CONFIG == null) {
            CONFIG = loadConfig();
        }

        return CONFIG;
    }



    public static Logger logger() {
        if (LOGGER == null) {
            LOGGER = LogManager.getLogger("Magnesium");
        }

        return LOGGER;
    }


    private static SodiumGameOptions loadConfig() {
        SodiumGameOptions config = SodiumGameOptions.load(Paths.get("config", "sodium-options.json"));onConfigChanged(config);

        return config;
    }

    public static void onConfigChanged(SodiumGameOptions options) {
        UnsafeUtil.setEnabled(options.advanced.allowDirectMemoryAccess);
    }



    public static String getVersion() {
        if (MOD_VERSION == null) {
            throw new NullPointerException("Mod version hasn't been populated yet");
        }

        return MOD_VERSION;
    }



}
