package me.jellysquid.mods.sodium.mixin.features.options;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.minecraft.client.gui.IngameGui;
//import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(IngameGui.class)
public class MixinInGameHud {
    @Redirect(method = "renderIngameGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isFancyGraphicsEnabled()Z"))
    private boolean redirectFancyGraphicsVignette() {
        return SodiumClientMod.options().quality.enableVignette;
    }
}