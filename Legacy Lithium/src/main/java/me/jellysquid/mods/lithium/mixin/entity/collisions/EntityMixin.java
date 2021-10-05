package me.jellysquid.mods.lithium.mixin.entity.collisions;

import me.jellysquid.mods.lithium.common.entity.LithiumEntityCollisions;
import net.minecraft.entity.Entity;
//import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.AxisAlignedBB;
//import net.minecraft.util.math.Box;
//import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
//import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.stream.Stream;

/**
 * Replaces collision testing methods against the world border with faster checks.
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public World world;

    @Shadow
    public abstract AxisAlignedBB getBoundingBox();

    /**
     * Skips the matchesAnywhere evaluation, which is replaced with {@link EntityMixin#fastWorldBorderTest(Stream, Stream, Vec3d)}.
     */
    @Redirect(
            method = "getAllowedMovement(Lnet/minecraft/util/math/vector/Vector3d;)Lnet/minecraft/util/math/vector/Vector3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/shapes/VoxelShapes;compare(Lnet/minecraft/util/math/shapes/VoxelShape;Lnet/minecraft/util/math/shapes/VoxelShape;Lnet/minecraft/util/math/shapes/IBooleanFunction;)Z"
            )
    )
    private boolean skipWorldBorderMatchesAnywhere(VoxelShape borderShape, VoxelShape entityShape, IBooleanFunction func, Vector3d motion) {
        return false;
    }

    /**
     * Skip creation of unused cuboid shape
     */
    @Redirect(
            method = "getAllowedMovement(Lnet/minecraft/util/math/vector/Vector3d;)Lnet/minecraft/util/math/vector/Vector3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/shapes/VoxelShapes;create(Lnet/minecraft/util/math/AxisAlignedBB;)Lnet/minecraft/util/math/shapes/VoxelShape;",
                    ordinal = 0
            )
    )
    private VoxelShape skipCuboid(AxisAlignedBB box) {
        return null;
    }
    /**
     * Skip creation of unused stream
     */
    @Redirect(
            method = "getAllowedMovement(Lnet/minecraft/util/math/vector/Vector3d;)Lnet/minecraft/util/math/vector/Vector3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;empty()Ljava/util/stream/Stream;",
                    ordinal = 0
            )
    )
    private Stream<VoxelShape> skipStream() {
        return null;
    }

    /**
     * Uses a very quick check to determine if the player is outside the world border (which would disable collisions
     * against it). We also perform an additional check to see if the player can even collide with the world border in
     * this physics step, allowing us to remove it from collision testing in later code.
     *
     * @return The combined entity shapes and worldborder stream, or if the worldborder cannot be collided with the entity stream will be returned.
     */
    @Redirect(
            method = "getAllowedMovement(Lnet/minecraft/util/math/vector/Vector3d;)Lnet/minecraft/util/math/vector/Vector3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;concat(Ljava/util/stream/Stream;Ljava/util/stream/Stream;)Ljava/util/stream/Stream;"
            )
    )
    private Stream<VoxelShape> fastWorldBorderTest(Stream<VoxelShape> entityShapes, Stream<VoxelShape> emptyStream, Vector3d motion) {
        if (LithiumEntityCollisions.isWithinWorldBorder(this.world.getWorldBorder(), this.getBoundingBox().expand(motion)) ||
                !LithiumEntityCollisions.isWithinWorldBorder(this.world.getWorldBorder(), this.getBoundingBox().shrink(1.0E-7D))) {
            return entityShapes;
        }
        //the world border shape will only be collided with, if the entity is colliding with the world border after movement but is not
        //colliding with the world border already
        return Stream.concat(entityShapes, Stream.of(this.world.getWorldBorder().getShape()));
    }
}
