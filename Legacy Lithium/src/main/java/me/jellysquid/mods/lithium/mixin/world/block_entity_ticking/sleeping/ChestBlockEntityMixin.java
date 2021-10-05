package me.jellysquid.mods.lithium.mixin.world.block_entity_ticking.sleeping;

import me.jellysquid.mods.lithium.common.world.blockentity.BlockEntitySleepTracker;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.block.entity.BlockEntityType;
//import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestTileEntity.class)
public class ChestBlockEntityMixin extends TileEntity {
    @Shadow
    protected int numPlayersUsing;

    @Shadow
    protected float lidAngle;

    @Shadow
    protected float prevLidAngle;

    @Shadow
    private int ticksSinceSync;

    private int lastTime;

    public ChestBlockEntityMixin(TileEntityType<?> type) {
        super(type);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void updateTicksOpen(CallbackInfo ci) {
        //noinspection ConstantConditions
        int time = (int) this.world.getGameTime();
        //ticksOpen == 0 implies most likely that this is the first tick. We don't want to update the value then.
        //overflow case is handled by not going to sleep when this.ticksOpen == 0
        if (this.ticksSinceSync != 0) {
            this.ticksSinceSync += time - this.lastTime - 1;
        }
        this.lastTime = time;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void checkSleep(CallbackInfo ci) {
        if (this.numPlayersUsing == 0 && this.lidAngle == 0.0F && this.prevLidAngle == 0 && this.ticksSinceSync != 0 && this.world != null) {
            ((BlockEntitySleepTracker)this.world).setAwake(this, false);
        }
    }

    @Inject(method = "closeInventory", at = @At("RETURN"))
    private void checkWakeUpOnClose(PlayerEntity player, CallbackInfo ci) {
        this.checkWakeUp();
    }
    @Inject(method = "openInventory", at = @At("RETURN"))
    private void checkWakeUpOnOpen(PlayerEntity player, CallbackInfo ci) {
        this.checkWakeUp();
    }
    @Inject(method = "receiveClientEvent", at = @At("RETURN"))
    private void checkWakeUpOnSyncedBlockEvent(int type, int data, CallbackInfoReturnable<Boolean> cir) {
        this.checkWakeUp();
    }

    private void checkWakeUp() {
        if ((this.numPlayersUsing != 0 || this.lidAngle != 0.0F || this.prevLidAngle != 0) && this.world != null) {
            ((BlockEntitySleepTracker)this.world).setAwake(this, true);
        }
    }
}
