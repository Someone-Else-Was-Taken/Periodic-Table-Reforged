package me.jellysquid.mods.lithium.common.world.blockentity;

//import net.minecraft.block.entity.BlockEntity;
import net.minecraft.tileentity.TileEntity;

public interface BlockEntitySleepTracker {
    void setAwake(TileEntity blockEntity, boolean needsTicking);
}