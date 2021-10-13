package me.jellysquid.mods.sodium.client.render.chunk.passes.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jellysquid.mods.sodium.client.render.chunk.passes.BlockLayer;
import me.jellysquid.mods.sodium.client.render.chunk.passes.BlockRenderPass;
//import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.renderer.RenderType;
//import net.minecraft.util.Identifier;
import net.minecraft.util.ResourceLocation;

public class SolidRenderPass extends BlockRenderPass {
    public SolidRenderPass(int ordinal, ResourceLocation id, BlockLayer... layers) {
        super(ordinal, id, true, layers);
    }

    @Override
    public void beginRender() {
        RenderType.getSolid().setupRenderState();

        RenderSystem.enableAlphaTest();
    }

    @Override
    public void endRender() {
        RenderSystem.disableAlphaTest();

        RenderType.getSolid().clearRenderState();
    }
}
