package me.jellysquid.mods.sodium.mixin.features.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeIngameGui.class)
public class MixinIngameGui {

    @Final
    private Minecraft mc = Minecraft.getInstance();

    @Inject(at = @At("TAIL"), method = "renderIngameGui")
    public void render(MatrixStack matrixStack, float partialTicks, CallbackInfo info) {
        if (!this.mc.gameSettings.showDebugInfo) {
            if (SodiumClientMod.options().experimental.displayFps) {
                String displayString = ((MixinMinecraftAccessor)this.mc).getDebugFPS() + " fps";
                float pos = SodiumClientMod.options().experimental.displayFpsPos;
                int alpha = 225;
                int textColor = ((alpha & 0xFF) << 24) | 0xEEEEEE;
                this.mc.fontRenderer.drawString(matrixStack, displayString, pos, pos, textColor);
            }

        }
    }
}