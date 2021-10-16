package net.coderbot.batchedentityrendering.mixin;

//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@OnlyIn(Dist.CLIENT)
@Mixin(RenderType.class)
public interface RenderTypeAccessor {
	@Accessor("sortOnUpload")
	boolean shouldSortOnUpload();
}
