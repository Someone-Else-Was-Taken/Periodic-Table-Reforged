package me.jellysquid.mods.sodium.mixin.features.buffer_builder.intrinsics;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.DefaultColorVertexBuilder;
import me.jellysquid.mods.sodium.client.model.consumer.GlyphVertexConsumer;
import me.jellysquid.mods.sodium.client.model.consumer.ParticleVertexConsumer;
import me.jellysquid.mods.sodium.client.model.consumer.QuadVertexConsumer;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.util.Norm3b;
import me.jellysquid.mods.sodium.client.util.UnsafeUtil;
import me.jellysquid.mods.sodium.client.util.color.ColorABGR;
import me.jellysquid.mods.sodium.client.util.color.ColorU8;
import me.jellysquid.mods.sodium.client.util.math.Matrix4fExtended;
import me.jellysquid.mods.sodium.client.util.math.MatrixUtil;
//import net.minecraft.client.render.BufferBuilder;
//import net.minecraft.client.render.FixedColorVertexConsumer;
//import net.minecraft.client.render.VertexFormat;
//import net.minecraft.client.render.VertexFormats;
//import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.client.util.math.Vector4f;
//import net.minecraft.util.math.Matrix3f;
//import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector4f;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import sun.misc.Unsafe;

import java.nio.ByteBuffer;

@SuppressWarnings({ "SameParameterValue", "SuspiciousNameCombination" })
@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder extends DefaultColorVertexBuilder
        implements ParticleVertexConsumer, QuadVertexConsumer, GlyphVertexConsumer {
    @Shadow
    private VertexFormat vertexFormat;

    @Shadow
    private ByteBuffer byteBuffer;

    @Shadow
    private int nextElementBytes;

    @Shadow
    private int vertexCount;

    @Shadow
    private boolean fullFormat; // has overlay

    @Shadow
    private boolean fastFormat; // is baked quad format

    @Shadow
    protected abstract void growBuffer(int size);

    @Shadow
    public abstract void endVertex();

    @Override
    public void vertexParticle(float x, float y, float z, float u, float v, int color, int light) {
        if (this.vertexFormat != DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP) {
            this.vertexParticleFallback(x, y, z, u, v, color, light);
            return;
        }

        int size = this.vertexFormat.getSize();

        this.growBuffer(size);

        if (UnsafeUtil.isAvailable()) {
            this.vertexParticleUnsafe(x, y, z, u, v, color, light);
        } else {
            this.vertexParticleSafe(x, y, z, u, v, color, light);
        }

        this.nextElementBytes += size;
        this.vertexCount++;
    }

    private void vertexParticleFallback(float x, float y, float z, float u, float v, int color, int light) {
        this.pos(x, y, z);
        this.tex(u, v);
        this.color(ColorABGR.unpackRed(color), ColorABGR.unpackGreen(color), ColorABGR.unpackBlue(color),
                ColorABGR.unpackAlpha(color));
        this.lightmap(light);
        this.endVertex();
    }

    private void vertexParticleSafe(float x, float y, float z, float u, float v, int color, int light) {
        int i = this.nextElementBytes;

        ByteBuffer buffer = this.byteBuffer;
        buffer.putFloat(i, x);
        i += 4;

        buffer.putFloat(i, y);
        i += 4;

        buffer.putFloat(i, z);
        i += 4;

        buffer.putFloat(i, u);
        i += 4;

        buffer.putFloat(i, v);
        i += 4;

        buffer.putInt(i, color);
        i += 4;

        buffer.putInt(i, light);
        i += 4;
    }

    private void vertexParticleUnsafe(float x, float y, float z, float u, float v, int color, int light) {
        long i = MemoryUtil.memAddress(this.byteBuffer, this.nextElementBytes);

        Unsafe unsafe = UnsafeUtil.instance();
        unsafe.putFloat(i, x);
        i += 4;

        unsafe.putFloat(i, y);
        i += 4;

        unsafe.putFloat(i, z);
        i += 4;

        unsafe.putFloat(i, u);
        i += 4;

        unsafe.putFloat(i, v);
        i += 4;

        unsafe.putInt(i, color);
        i += 4;

        unsafe.putInt(i, light);
        i += 4;
    }

    @Override
    public void vertexQuad(float x, float y, float z, int color, float u, float v, int light, int overlay, int normal) {
        if (this.defaultColor) {
            throw new IllegalStateException();
        }

        if (!this.fastFormat) {
            this.vertexQuadFallback(x, y, z, color, u, v, overlay, light, normal);
            return;
        }

        int size = this.vertexFormat.getSize();

        this.growBuffer(size);

        if (UnsafeUtil.isAvailable()) {
            this.vertexQuadUnsafe(x, y, z, color, u, v, overlay, light, normal);
        } else {
            this.vertexQuadSafe(x, y, z, color, u, v, overlay, light, normal);
        }

        this.nextElementBytes += size;
        this.vertexCount++;
    }

    private void vertexQuadFallback(float x, float y, float z, int color, float u, float v, int overlay, int light, int normal) {
        this.pos(x, y, z);
        this.color(ColorABGR.unpackRed(color), ColorABGR.unpackGreen(color), ColorABGR.unpackBlue(color),
                ColorABGR.unpackAlpha(color));
        this.tex(u, v);

        if (this.fullFormat) {
            this.overlay(overlay);
        }

        this.lightmap(light);
        this.normal(Norm3b.unpackX(normal), Norm3b.unpackY(normal), Norm3b.unpackZ(normal));
        this.endVertex();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void vertexQuadUnsafe(float x, float y, float z, int color, float u, float v, int overlay, int light, int normal) {
        long i = MemoryUtil.memAddress(this.byteBuffer, this.nextElementBytes);

        Unsafe unsafe = UnsafeUtil.instance();
        unsafe.putFloat(i, x);
        i += 4;

        unsafe.putFloat(i, y);
        i += 4;

        unsafe.putFloat(i, z);
        i += 4;

        unsafe.putInt(i, color);
        i += 4;

        unsafe.putFloat(i, u);
        i += 4;

        unsafe.putFloat(i, v);
        i += 4;

        if (this.fullFormat) {
            unsafe.putInt(i, overlay);
            i += 4;
        }

        unsafe.putInt(i, light);
        i += 4;

        unsafe.putInt(i, normal);
    }

    private void vertexQuadSafe(float x, float y, float z, int color, float u, float v, int overlay, int light, int normal) {
        int i = this.nextElementBytes;

        ByteBuffer buffer = this.byteBuffer;
        buffer.putFloat(i, x);
        i += 4;

        buffer.putFloat(i, y);
        i += 4;

        buffer.putFloat(i, z);
        i += 4;

        buffer.putInt(i, color);
        i += 4;

        buffer.putFloat(i, u);
        i += 4;

        buffer.putFloat(i, v);
        i += 4;

        if (this.fullFormat) {
            buffer.putInt(i, overlay);
            i += 4;
        }

        buffer.putInt(i, light);
        i += 4;

        buffer.putInt(i, normal);
    }

    @Override
    public void addQuad(MatrixStack.Entry matrices, BakedQuad quad, float[] brightnessTable, float r, float g, float b, int[] light, int overlay, boolean colorize) {
        if (!this.fastFormat) {
            super.addQuad(matrices, quad, brightnessTable, r, g, b, light, overlay, colorize);

            return;
        }

        if (this.defaultColor) {
            throw new IllegalStateException();
        }

        ModelQuadView quadView = (ModelQuadView) quad;

        Matrix4f modelMatrix = matrices.getMatrix();
        Matrix3f normalMatrix = matrices.getNormal();

        int norm = MatrixUtil.computeNormal(normalMatrix, quad.getFace());

        for (int i = 0; i < 4; i++) {
            float x = quadView.getX(i);
            float y = quadView.getY(i);
            float z = quadView.getZ(i);

            float fR;
            float fG;
            float fB;

            float brightness = brightnessTable[i];

            if (colorize) {
                int color = quadView.getColor(i);

                float oR = ColorU8.normalize(ColorABGR.unpackRed(color));
                float oG = ColorU8.normalize(ColorABGR.unpackGreen(color));
                float oB = ColorU8.normalize(ColorABGR.unpackBlue(color));

                fR = oR * brightness * r;
                fG = oG * brightness * g;
                fB = oB * brightness * b;
            } else {
                fR = brightness * r;
                fG = brightness * g;
                fB = brightness * b;
            }

            float u = quadView.getTexU(i);
            float v = quadView.getTexV(i);

            int color = ColorABGR.pack(fR, fG, fB, 1.0F);

            Vector4f pos = new Vector4f(x, y, z, 1.0F);
            pos.transform(modelMatrix);

            this.vertexQuad(pos.getX(), pos.getY(), pos.getZ(), color, u, v, light[i], overlay, norm);
        }
    }

    @Override
    public void vertexGlyph(Matrix4f matrix, float x, float y, float z, int color, float u, float v, int light) {
        Matrix4fExtended matrixExt = MatrixUtil.getExtendedMatrix(matrix);

        float x2 = matrixExt.transformVecX(x, y, z);
        float y2 = matrixExt.transformVecY(x, y, z);
        float z2 = matrixExt.transformVecZ(x, y, z);

        if (this.vertexFormat != DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP) {
            this.vertexGlyphFallback(x2, y2, z2, color, u, v, light);
            return;
        }

        int size = this.vertexFormat.getSize();

        this.growBuffer(size);

        if (UnsafeUtil.isAvailable()) {
            this.vertexGlyphUnsafe(x2, y2, z2, color, u, v, light);
        } else {
            this.vertexGlyphSafe(x2, y2, z2, color, u, v, light);
        }

        this.nextElementBytes += size;
        this.vertexCount++;
    }

    private void vertexGlyphFallback(float x, float y, float z, int color, float u, float v, int light) {
        this.pos(x, y, z);
        this.color(ColorABGR.unpackRed(color), ColorABGR.unpackGreen(color), ColorABGR.unpackBlue(color),
                ColorABGR.unpackAlpha(color));
        this.tex(u, v);
        this.lightmap(light);
        this.endVertex();
    }

    private void vertexGlyphSafe(float x, float y, float z, int color, float u, float v, int light) {
        int i = this.nextElementBytes;

        ByteBuffer buffer = this.byteBuffer;
        buffer.putFloat(i, x);
        i += 4;

        buffer.putFloat(i, y);
        i += 4;

        buffer.putFloat(i, z);
        i += 4;

        buffer.putInt(i, color);
        i += 4;

        buffer.putFloat(i, u);
        i += 4;

        buffer.putFloat(i, v);
        i += 4;

        buffer.putInt(i, light);
        i += 4;
    }

    private void vertexGlyphUnsafe(float x, float y, float z, int color, float u, float v, int light) {
        long i = MemoryUtil.memAddress(this.byteBuffer, this.nextElementBytes);

        Unsafe unsafe = UnsafeUtil.instance();
        unsafe.putFloat(i, x);
        i += 4;

        unsafe.putFloat(i, y);
        i += 4;

        unsafe.putFloat(i, z);
        i += 4;

        unsafe.putInt(i, color);
        i += 4;

        unsafe.putFloat(i, u);
        i += 4;

        unsafe.putFloat(i, v);
        i += 4;

        unsafe.putInt(i, light);
        i += 4;
    }
}
