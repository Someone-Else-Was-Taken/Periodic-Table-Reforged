package net.coderbot.iris.mixin.renderlayer;

import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//import net.minecraft.client.render.RenderLayer;

//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;

@OnlyIn(Dist.CLIENT)
@Mixin(RenderType.class)
public interface RenderLayerAccessor {
	@Accessor("needsSorting")
	boolean isTranslucent();
}
