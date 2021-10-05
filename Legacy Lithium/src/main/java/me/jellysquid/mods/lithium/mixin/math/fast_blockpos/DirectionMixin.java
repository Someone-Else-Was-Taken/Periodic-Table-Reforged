package me.jellysquid.mods.lithium.mixin.math.fast_blockpos;

import net.minecraft.util.Direction;
//import net.minecraft.util.math.Direction;
//import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.vector.Vector3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The vector of each Direction is usually stored inside another object, which introduces indirection and makes things
 * harder for the JVM to optimize. This patch simply hoists the offset fields to the Direction enum itself.
 */
@Mixin(Direction.class)
public class DirectionMixin {
    private int offsetX, offsetY, offsetZ;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void reinit(String enumName, int ordinal, int id, int idOpposite, int idHorizontal, String name, Direction.AxisDirection direction, Direction.Axis axis, Vector3i vector, CallbackInfo ci) {
        this.offsetX = vector.getX();
        this.offsetY = vector.getY();
        this.offsetZ = vector.getZ();
    }

    /**
     * @reason Avoid indirection to aid inlining
     * @author JellySquid
     */
    @Overwrite
    public int getXOffset() {
        return this.offsetX;
    }

    /**
     * @reason Avoid indirection to aid inlining
     * @author JellySquid
     */
    @Overwrite
    public int getYOffset() {
        return this.offsetY;
    }

    /**
     * @reason Avoid indirection to aid inlining
     * @author JellySquid
     */
    @Overwrite
    public int getZOffset() {
        return this.offsetZ;
    }
}
