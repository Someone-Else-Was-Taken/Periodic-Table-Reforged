package me.jellysquid.mods.sodium.client.world.biome;

import net.minecraft.block.BlockState;
//import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.renderer.color.IBlockColor;

public interface BlockColorsExtended {
    IBlockColor getColorProvider(BlockState state);
}
