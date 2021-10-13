package me.jellysquid.mods.sodium.mixin.features.entity.shadow_clipping;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.math.vector.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRendererManager.class)
public class MixinEntityRenderDispatcher {
    @Redirect(method = "shadowVertex", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/IVertexBuilder;pos(Lnet/minecraft/util/math/vector/Matrix4f;FFF)Lcom/mojang/blaze3d/vertex/IVertexBuilder;"))
    private static IVertexBuilder preWriteVertex(IVertexBuilder vertexConsumer, Matrix4f matrix, float x, float y, float z) {
        // FIX: Render the shadow slightly above the block to fix clipping issues
        // This happens in vanilla too, but is exacerbated by the Compact Vertex Format option.
        return vertexConsumer.pos(matrix, x, y + 0.001f, z);
    }

}
