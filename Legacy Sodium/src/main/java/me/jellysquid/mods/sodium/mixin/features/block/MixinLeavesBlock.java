package me.jellysquid.mods.sodium.mixin.features.block;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.Direction;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.AbstractBlock.Properties;

@Mixin(LeavesBlock.class)
public class MixinLeavesBlock extends Block {

    public MixinLeavesBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        if (SodiumClientMod.options().advanced.useLeavesCulling) {
            return adjacentBlockState.getBlock() instanceof LeavesBlock;
        } else {
            return super.skipRendering(state, adjacentBlockState, side);
        }
    }
}