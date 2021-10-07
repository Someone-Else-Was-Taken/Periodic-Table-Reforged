package me.jellysquid.mods.lithium.mixin.world.block_entity_ticking.sleeping;

import me.jellysquid.mods.lithium.common.world.blockentity.BlockEntitySleepTracker;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.block.entity.BlockEntityType;
//import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxTileEntity.class)
public abstract class ShulkerBoxBlockEntityMixin extends TileEntity {

    public ShulkerBoxBlockEntityMixin(TileEntityType<?> type) {
        super(type);
    }

    @Shadow
    public abstract ShulkerBoxTileEntity.AnimationStatus getAnimationStatus();

    @Shadow
    private float progressOld;

    @Shadow
    private float progress;

    @Inject(method = "tick", at = @At("RETURN"))
    private void checkSleep(CallbackInfo ci) {
        if (this.getAnimationStatus() == ShulkerBoxTileEntity.AnimationStatus.CLOSED && this.progressOld == 0f &&
                this.progress == 0f && this.world != null) {
            ((BlockEntitySleepTracker)this.world).setAwake(this, false);
        }
    }

    @Inject(method = "receiveClientEvent", at = @At("HEAD"))
    public void checkWakeUp(int type, int data, CallbackInfoReturnable<Boolean> cir) {
        if (this.world != null && type == 1) {
            ((BlockEntitySleepTracker)this.world).setAwake(this, true);
        }
    }
}
