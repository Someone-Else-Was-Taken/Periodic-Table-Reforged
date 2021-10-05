package me.jellysquid.mods.lithium.mixin.world.block_entity_ticking.support_cache;

import me.jellysquid.mods.lithium.common.world.blockentity.SupportCache;
import net.minecraft.block.BlockState;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TileEntity.class)
public abstract class BlockEntityMixin implements SupportCache {
    @Shadow
    public abstract BlockState getBlockState();

    @Shadow
    public abstract TileEntityType<?> getType();

    private BlockState supportTestState;
    private boolean supportTestResult;

    @Override
    public boolean isSupported() {
        BlockState cachedState = this.getBlockState();
        if (this.supportTestState == cachedState) {
            return this.supportTestResult;
        }
        return this.supportTestResult = this.getType().isValidBlock((this.supportTestState = cachedState).getBlock());
    }
}
