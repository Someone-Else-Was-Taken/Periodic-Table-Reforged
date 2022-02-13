package me.jellysquid.mods.sodium.mixin.features.options;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.minecraft.client.Minecraft;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Redirect(method = "renderRainSnow", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isFancyGraphicsEnabled()Z"))
    private boolean redirectGetFancyWeather() {
        return SodiumClientMod.options().quality.weatherQuality.isFancy(Minecraft.getInstance().gameSettings.graphicFanciness);
    }
}
