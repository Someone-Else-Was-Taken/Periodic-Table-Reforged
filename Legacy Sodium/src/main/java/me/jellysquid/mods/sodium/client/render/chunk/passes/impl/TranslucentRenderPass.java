package me.jellysquid.mods.sodium.client.render.chunk.passes.impl;

import me.jellysquid.mods.sodium.client.render.chunk.passes.BlockLayer;
import me.jellysquid.mods.sodium.client.render.chunk.passes.BlockRenderPass;
//import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.renderer.RenderType;
//import net.minecraft.util.Identifier;
import net.minecraft.util.ResourceLocation;

public class TranslucentRenderPass extends BlockRenderPass {
    public TranslucentRenderPass(int ordinal, ResourceLocation id, BlockLayer... layers) {
        super(ordinal, id, false, layers);
    }

    @Override
    public void beginRender() {
        RenderType.getTranslucent().setupRenderState();
    }

    @Override
    public void endRender() {
        RenderType.getTranslucent().clearRenderState();
    }
}
