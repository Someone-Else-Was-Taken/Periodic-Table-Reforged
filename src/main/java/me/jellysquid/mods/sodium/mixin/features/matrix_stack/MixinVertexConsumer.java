package me.jellysquid.mods.sodium.mixin.features.matrix_stack;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.jellysquid.mods.sodium.client.util.math.Matrix3fExtended;
import me.jellysquid.mods.sodium.client.util.math.Matrix4fExtended;
import me.jellysquid.mods.sodium.client.util.math.MatrixUtil;
//import net.minecraft.client.render.VertexConsumer;
//import net.minecraft.util.math.Matrix3f;
//import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IVertexBuilder.class)
public interface MixinVertexConsumer {
    @Shadow
    IVertexBuilder normal(float x, float y, float z);

    @Shadow
    IVertexBuilder pos(double x, double y, double z);

    /**
     * @reason Avoid allocations
     * @author JellySquid
     */
    @Overwrite
    default IVertexBuilder pos(Matrix4f matrix, float x, float y, float z) {
        Matrix4fExtended ext = MatrixUtil.getExtendedMatrix(matrix);
        float x2 = ext.transformVecX(x, y, z);
        float y2 = ext.transformVecY(x, y, z);
        float z2 = ext.transformVecZ(x, y, z);

        return this.pos(x2, y2, z2);
    }

    /**
     * @reason Avoid allocations
     * @author JellySquid
     */
    @Overwrite
    default IVertexBuilder normal(Matrix3f matrix, float x, float y, float z) {
        Matrix3fExtended ext = MatrixUtil.getExtendedMatrix(matrix);
        float x2 = ext.transformVecX(x, y, z);
        float y2 = ext.transformVecY(x, y, z);
        float z2 = ext.transformVecZ(x, y, z);

        return this.normal(x2, y2, z2);
    }
}
