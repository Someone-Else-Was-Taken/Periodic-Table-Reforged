package me.jellysquid.mods.lithium.mixin.world.block_entity_ticking.sleeping;

import me.jellysquid.mods.lithium.common.world.blockentity.BlockEntitySleepTracker;
//import net.minecraft.block.entity.BellBlockEntity;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.tileentity.BellTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
//import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BellTileEntity.class)
public class BellBlockEntityMixin extends TileEntity {

    @Shadow
    private boolean shouldReveal;

    @Shadow
    public boolean isRinging;

    public BellBlockEntityMixin(TileEntityType<?> type) {
        super(type);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void checkSleep(CallbackInfo ci) {
        if (!this.isRinging && !this.shouldReveal && this.world != null) {
            ((BlockEntitySleepTracker)this.world).setAwake(this, false);
        }
    }

    @Inject(method = "ring", at = @At("HEAD"))
    public void checkWakeUp(Direction direction, CallbackInfo ci) {
        if (!this.isRinging && this.world != null) {
            ((BlockEntitySleepTracker)this.world).setAwake(this, true);
        }
    }

    @Inject(method = "receiveClientEvent", at = @At("HEAD"))
    public void checkWakeUp(int type, int data, CallbackInfoReturnable<Boolean> cir) {
        if (!this.isRinging && type == 1 && this.world != null) {
            ((BlockEntitySleepTracker)this.world).setAwake(this, true);
        }
    }
}