package me.jellysquid.mods.lithium.mixin.world.block_entity_ticking.sleeping;

import me.jellysquid.mods.lithium.common.world.blockentity.BlockEntitySleepTracker;
import me.jellysquid.mods.lithium.common.world.blockentity.SleepingBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.block.entity.BlockEntityType;
//import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkullTileEntity.class)
public class SkullBlockEntityMixin extends TileEntity implements SleepingBlockEntity {

    private BlockState lastState;

    public SkullBlockEntityMixin(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public boolean canTickOnSide(boolean isClient) {
        return isClient;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void checkSleep(CallbackInfo ci) {
        if (this.world != null) {
            BlockState blockState = this.getBlockState();
            if (blockState != this.lastState && !(this.lastState = blockState).matchesBlock(Blocks.DRAGON_HEAD) && !blockState.matchesBlock(Blocks.DRAGON_WALL_HEAD)) {
                ((BlockEntitySleepTracker) this.world).setAwake(this, false);
            }
        }
    }

    private void checkWakeUp() {
        if (this.world == null || !this.world.isRemote) {
            return;
        }
        BlockState blockState = this.getBlockState();
        if (this.world != null && (blockState.matchesBlock(Blocks.DRAGON_HEAD) || blockState.matchesBlock(Blocks.DRAGON_WALL_HEAD))) {
            ((BlockEntitySleepTracker)this.world).setAwake(this, true);
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        this.checkWakeUp();
    }

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        this.checkWakeUp();
    }
}