package me.jellysquid.mods.lithium.mixin.world.block_entity_ticking.sleeping;

import me.jellysquid.mods.lithium.common.world.blockentity.SleepingBlockEntity;
//import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.tileentity.DaylightDetectorTileEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DaylightDetectorTileEntity.class)
public class DaylightDetectorBlockEntityMixin implements SleepingBlockEntity {
    @Override
    public boolean canTickOnSide(boolean isClient) {
        return !isClient;
    }
}