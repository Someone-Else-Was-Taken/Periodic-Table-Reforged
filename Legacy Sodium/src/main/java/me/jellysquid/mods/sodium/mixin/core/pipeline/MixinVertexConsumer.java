package me.jellysquid.mods.sodium.mixin.core.pipeline;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.jellysquid.mods.sodium.client.model.consumer.GlyphVertexConsumer;
import me.jellysquid.mods.sodium.client.model.consumer.ParticleVertexConsumer;
import me.jellysquid.mods.sodium.client.model.consumer.QuadVertexConsumer;
import me.jellysquid.mods.sodium.client.util.Norm3b;
import me.jellysquid.mods.sodium.client.util.color.ColorABGR;
//import net.minecraft.client.render.VertexConsumer;
//import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.vector.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IVertexBuilder.class)
public interface MixinVertexConsumer extends ParticleVertexConsumer, QuadVertexConsumer, GlyphVertexConsumer {
    @Shadow
    IVertexBuilder pos(double x, double y, double z);

    @Shadow
    IVertexBuilder tex(float u, float v);

    @Shadow
    IVertexBuilder color(int red, int green, int blue, int alpha);

    @Shadow
    IVertexBuilder lightmap(int uv);

    @Shadow
    IVertexBuilder overlay(int uv);

    @Shadow
    IVertexBuilder normal(float x, float y, float z);

    @Shadow
    void endVertex();

    @Override
    default void vertexParticle(float x, float y, float z, float u, float v, int color, int light) {
        this.pos(x, y, z);
        this.tex(u, v);
        this.color(ColorABGR.unpackRed(color), ColorABGR.unpackGreen(color), ColorABGR.unpackBlue(color), ColorABGR.unpackAlpha(color));
        this.lightmap(light);
        this.endVertex();
    }

    @Override
    default void vertexQuad(float x, float y, float z, int color, float u, float v, int light, int overlay, int normal) {
        this.pos(x, y, z);
        this.color(ColorABGR.unpackRed(color), ColorABGR.unpackGreen(color), ColorABGR.unpackBlue(color), ColorABGR.unpackAlpha(color));
        this.tex(u, v);
        this.overlay(overlay);
        this.lightmap(light);
        this.normal(Norm3b.unpackX(normal), Norm3b.unpackY(normal), Norm3b.unpackZ(normal));
        this.endVertex();
    }

    @Override
    default void vertexGlyph(Matrix4f matrix, float x, float y, float z, int color, float u, float v, int light) {
        this.pos(x, y, z);
        this.color(ColorABGR.unpackRed(color), ColorABGR.unpackGreen(color), ColorABGR.unpackBlue(color), ColorABGR.unpackAlpha(color));
        this.tex(u, v);
        this.lightmap(light);
        this.endVertex();
    }
}
