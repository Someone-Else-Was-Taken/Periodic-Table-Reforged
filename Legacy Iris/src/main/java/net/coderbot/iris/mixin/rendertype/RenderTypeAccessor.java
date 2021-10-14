package net.coderbot.iris.mixin.rendertype;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.RenderType;

@OnlyIn(Dist.CLIENT)
@Mixin(RenderType.class)
public interface RenderTypeAccessor {
	@Accessor("sortOnUpload")
	boolean shouldSortOnUpload();
}
