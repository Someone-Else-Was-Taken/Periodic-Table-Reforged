package net.coderbot.iris.mixin.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.coderbot.iris.Iris;
import net.coderbot.iris.gui.screen.HudHideable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
//import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.FontRenderer;
//import net.minecraft.client.gui.Gui;
//import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;

@Mixin(IngameGui.class)
public class MixinGui {
	@Shadow @Final private Minecraft minecraft;

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void iris$handleHudHidingScreens(MatrixStack poseStack, float tickDelta, CallbackInfo ci) {
		Screen screen = this.minecraft.screen;

		if (screen instanceof HudHideable) {
			ci.cancel();
		}
	}

	// TODO: Move this to a more appropriate mixin
	@Inject(method = "render", at = @At("RETURN"))
	public void iris$displayBigSodiumWarning(MatrixStack poseStack, float tickDelta, CallbackInfo ci) {
		if (Iris.isSodiumInstalled()
				|| Minecraft.getInstance().options.renderDebug
				|| !Iris.getCurrentPack().isPresent()) {
			return;
		}

		FontRenderer font = Minecraft.getInstance().font;

		List<String> warningLines = new ArrayList<>();
		warningLines.add("[Iris] Magnesium isn't installed; you will have poor performance.");
		warningLines.add("[Iris] Install Magnesium if you want to run benchmarks or get higher FPS!");

		for (int i = 0; i < warningLines.size(); ++i) {
			String string = warningLines.get(i);

			final int lineHeight = 9;
			final int lineWidth = font.width(string);
			final int y = 2 + lineHeight * i;

			AbstractGui.fill(poseStack, 1, y - 1, 2 + lineWidth + 1, y + lineHeight - 1, 0x9050504E);
			font.draw(poseStack, string, 2.0F, y, 0xFFFF55);
		}
	}
}
