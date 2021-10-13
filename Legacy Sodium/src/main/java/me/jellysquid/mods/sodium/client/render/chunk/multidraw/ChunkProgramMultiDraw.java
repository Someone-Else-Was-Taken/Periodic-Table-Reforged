package me.jellysquid.mods.sodium.client.render.chunk.multidraw;

import me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkProgram;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkProgramComponentBuilder;
//import net.minecraft.util.Identifier;
import net.minecraft.util.ResourceLocation;

public class ChunkProgramMultiDraw extends ChunkProgram {
    private final int dModelOffset;

    public ChunkProgramMultiDraw(ResourceLocation name, int handle, ChunkProgramComponentBuilder components) {
        super(name, handle, components);

        this.dModelOffset = this.getAttributeLocation("d_ModelOffset");
    }

    public int getModelOffsetAttributeLocation() {
        return this.dModelOffset;
    }
}
