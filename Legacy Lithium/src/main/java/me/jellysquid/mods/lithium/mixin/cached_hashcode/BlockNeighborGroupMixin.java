package me.jellysquid.mods.lithium.mixin.cached_hashcode;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
//import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.RenderSideCacheKey.class)
public class BlockNeighborGroupMixin {
    @Shadow
    @Final
    private BlockState state;

    @Shadow
    @Final
    private BlockState adjacentState;

    @Shadow
    @Final
    private Direction side;

    private int hash;

    /**
     * @reason Initialize the object's hashcode and cache it
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void generateHash(BlockState blockState_1, BlockState blockState_2, Direction direction_1, CallbackInfo ci) {
        int hash = this.state.hashCode();
        hash = 31 * hash + this.adjacentState.hashCode();
        hash = 31 * hash + this.side.hashCode();

        this.hash = hash;
    }

    /**
     * @reason Uses the cached hashcode
     * @author JellySquid
     */
    @Overwrite
    public int hashCode() {
        return this.hash;
    }
}
