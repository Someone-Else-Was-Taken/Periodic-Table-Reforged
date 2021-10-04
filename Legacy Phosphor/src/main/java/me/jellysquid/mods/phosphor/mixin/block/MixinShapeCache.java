package me.jellysquid.mods.phosphor.mixin.block;

import me.jellysquid.mods.phosphor.common.block.BlockStateLightInfo;
import net.minecraft.block.AbstractBlock;
import net.minecraft.util.math.shapes.VoxelShape;
//import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractBlock.AbstractBlockState.Cache.class)
public class MixinShapeCache implements BlockStateLightInfo {
    @Shadow
    @Final
    private VoxelShape[] renderShapes;

    @Shadow
    @Final
    private int opacity;

    @Override
    public VoxelShape[] getExtrudedFaces() {
        return this.renderShapes;
    }

    @Override
    public int getLightSubtracted() {
        return this.opacity;
    }

}
