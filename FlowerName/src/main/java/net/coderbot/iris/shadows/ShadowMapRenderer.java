package net.coderbot.iris.shadows;

import net.coderbot.iris.mixin.LevelRendererAccessor;
//import net.minecraft.client.Camera;
import net.minecraft.client.renderer.ActiveRenderInfo;

import java.util.List;

public interface ShadowMapRenderer {
	void renderShadows(LevelRendererAccessor levelRenderer, ActiveRenderInfo playerCamera);
	void addDebugText(List<String> messages);
	int getDepthTextureId();
	int getDepthTextureNoTranslucentsId();
	// TODO: Support more shadow color textures as well as support there being no shadow color textures.
	int getColorTexture0Id();
	int getColorTexture1Id();
	void destroy();
}
