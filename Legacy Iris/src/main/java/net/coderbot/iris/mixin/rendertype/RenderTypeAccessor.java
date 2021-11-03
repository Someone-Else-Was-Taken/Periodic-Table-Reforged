package net.coderbot.iris.mixin.rendertype;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;

@OnlyIn(Dist.CLIENT)
@Mixin(RenderType.class)
public interface RenderTypeAccessor {
	@Accessor("sortOnUpload")
	boolean shouldSortOnUpload();
}
