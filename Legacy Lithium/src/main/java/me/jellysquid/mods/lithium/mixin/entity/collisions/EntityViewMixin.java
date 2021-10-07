package me.jellysquid.mods.lithium.mixin.entity.collisions;

import me.jellysquid.mods.lithium.common.entity.LithiumEntityCollisions;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
//import net.minecraft.util.math.Box;
import net.minecraft.util.math.shapes.VoxelShape;
//import net.minecraft.util.shape.VoxelShape;
//import net.minecraft.world.EntityView;
import net.minecraft.world.IEntityReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Replaces collision testing methods with jumps to our own (faster) entity collision testing code.
 */
@Mixin(IEntityReader.class)
public interface EntityViewMixin {
    /**
     * @reason Avoid usage of heavy stream code
     * @author JellySquid
     */
    @Overwrite
    default Stream<VoxelShape> func_230318_c_(Entity entity, AxisAlignedBB box, Predicate<Entity> predicate) {
        return LithiumEntityCollisions.getEntityCollisions((IEntityReader) this, entity, box, predicate);
    }
}
