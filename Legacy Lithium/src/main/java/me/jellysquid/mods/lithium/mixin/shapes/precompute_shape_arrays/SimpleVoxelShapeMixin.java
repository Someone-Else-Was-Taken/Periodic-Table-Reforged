package me.jellysquid.mods.lithium.mixin.shapes.precompute_shape_arrays;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.Direction;
//import net.minecraft.util.math.Direction;
import net.minecraft.util.math.shapes.DoubleRangeList;
import net.minecraft.util.math.shapes.VoxelShapeCube;
import net.minecraft.util.math.shapes.VoxelShapePart;
//import net.minecraft.util.shape.FractionalDoubleList;
//import net.minecraft.util.shape.SimpleVoxelShape;
//import net.minecraft.util.shape.VoxelSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VoxelShapeCube.class)
public class SimpleVoxelShapeMixin {
    private static final Direction.Axis[] AXIS = Direction.Axis.values();

    private DoubleList[] list;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructed(VoxelShapePart voxels, CallbackInfo ci) {
        this.list = new DoubleList[AXIS.length];

        for (Direction.Axis axis : AXIS) {
            this.list[axis.ordinal()] = new DoubleRangeList(voxels.getSize(axis));
        }
    }

    /**
     * @author JellySquid
     * @reason Use the cached array
     */
    @Overwrite
    public DoubleList getValues(Direction.Axis axis) {
        return this.list[axis.ordinal()];
    }

}
