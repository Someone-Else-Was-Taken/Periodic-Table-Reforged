package me.jellysquid.mods.sodium.mixin.features.block;

import com.mojang.blaze3d.vertex.DefaultColorVertexBuilder;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadViewMutable;
import me.jellysquid.mods.sodium.client.model.quad.sink.ModelQuadSink;
import me.jellysquid.mods.sodium.client.util.ModelQuadUtil;
//import net.minecraft.client.render.BufferBuilder;
//import net.minecraft.client.render.FixedColorVertexConsumer;
import net.minecraft.client.renderer.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.ByteBuffer;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder extends DefaultColorVertexBuilder implements ModelQuadSink {
    @Shadow
    private int nextElementBytes;

    @Shadow
    private ByteBuffer byteBuffer;

    @Shadow
    protected abstract void growBuffer(int size);

    @Shadow
    private int vertexCount;

    @Override
    public void write(ModelQuadViewMutable quad) {
        this.growBuffer(ModelQuadUtil.VERTEX_SIZE_BYTES);

        quad.copyInto(this.byteBuffer, this.nextElementBytes);

        this.nextElementBytes += ModelQuadUtil.VERTEX_SIZE_BYTES;
        this.vertexCount += 4;
    }
}
