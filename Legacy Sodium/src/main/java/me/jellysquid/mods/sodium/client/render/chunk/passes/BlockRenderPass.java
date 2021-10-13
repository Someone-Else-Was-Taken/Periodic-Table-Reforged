package me.jellysquid.mods.sodium.client.render.chunk.passes;

//import net.minecraft.util.Identifier;
import net.minecraft.util.ResourceLocation;

public abstract class BlockRenderPass {
    private final ResourceLocation id;
    private final BlockLayer[] layers;
    private final boolean forward;
    private final int ordinal;

    public BlockRenderPass(int ordinal, ResourceLocation id, boolean forward, BlockLayer... layers) {
        this.ordinal = ordinal;
        this.id = id;
        this.layers = layers;
        this.forward = forward;
    }

    public abstract void beginRender();

    public abstract void endRender();

    public final boolean isForwardRendering() {
        return this.forward;
    }

    public final int ordinal() {
        return this.ordinal;
    }

    public final BlockLayer[] getLayers() {
        return this.layers;
    }

    public final ResourceLocation getId() {
        return this.id;
    }
}
