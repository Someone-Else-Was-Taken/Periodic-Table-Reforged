package me.jellysquid.mods.lithium.mixin.world.block_entity_ticking.sleeping;

import me.jellysquid.mods.lithium.common.world.blockentity.SleepingBlockEntity;
//import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.tileentity.EnchantingTableTileEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnchantingTableTileEntity.class)
public class EnchantingTableBlockEntityMixin implements SleepingBlockEntity {
    @Override
    public boolean canTickOnSide(boolean isClient) {
        return isClient;
    }
}
