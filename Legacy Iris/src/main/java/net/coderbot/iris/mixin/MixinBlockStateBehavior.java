package net.coderbot.iris.mixin;

import net.coderbot.iris.block_rendering.BlockRenderingSettings;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
//import net.minecraft.core.BlockPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
//import net.minecraft.world.level.BlockGetter;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.state.BlockBehaviour;
//import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class MixinBlockStateBehavior {
	@Shadow
	public abstract Block getBlock();

	@Shadow
	protected abstract BlockState asState();

	/**
	 * @author IMS
	 * @reason ambientOcclusionLevel support
	 */
	@OnlyIn(Dist.CLIENT)
	@Deprecated
	@Overwrite
	public float getShadeBrightness(IBlockReader blockGetter, BlockPos blockPos) {
		float originalValue = this.getBlock().getShadeBrightness(this.asState(), blockGetter, blockPos);
		float aoLightValue = BlockRenderingSettings.INSTANCE.getAmbientOcclusionLevel();
		return 1.0F - aoLightValue * (1.0F - originalValue);
	}
}
