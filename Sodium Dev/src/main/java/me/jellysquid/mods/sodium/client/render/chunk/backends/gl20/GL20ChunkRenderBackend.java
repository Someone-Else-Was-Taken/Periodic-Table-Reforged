package me.jellysquid.mods.sodium.client.render.chunk.backends.gl20;

import me.jellysquid.mods.sodium.client.gl.SodiumVertexFormats;
import me.jellysquid.mods.sodium.client.gl.attribute.GlVertexFormat;
import me.jellysquid.mods.sodium.client.gl.shader.GlProgram;
import me.jellysquid.mods.sodium.client.gl.shader.ShaderConstants;
import me.jellysquid.mods.sodium.client.gl.util.MemoryTracker;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderContainer;
import me.jellysquid.mods.sodium.client.render.chunk.oneshot.ChunkProgramOneshot;
import me.jellysquid.mods.sodium.client.render.chunk.oneshot.ChunkRenderBackendOneshot;
import me.jellysquid.mods.sodium.client.render.chunk.passes.BlockRenderPass;
import me.jellysquid.mods.sodium.client.render.chunk.passes.BlockRenderPassManager;
import me.jellysquid.mods.sodium.client.render.chunk.passes.impl.SingleTextureRenderPipeline;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkProgramComponentBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.shader.texture.ChunkProgramSingleTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

/**
 * A simple chunk rendering backend which mirrors that of vanilla's own pretty closely.
 */
public class GL20ChunkRenderBackend extends ChunkRenderBackendOneshot<GL20GraphicsState> {
    private final BlockRenderPassManager renderPassManager = SingleTextureRenderPipeline.create();

    public GL20ChunkRenderBackend(GlVertexFormat<SodiumVertexFormats.ChunkMeshAttribute> format) {
        super(format);
    }

    @Override
    protected void modifyProgram(GlProgram.Builder builder, ChunkProgramComponentBuilder components,
                                 GlVertexFormat<SodiumVertexFormats.ChunkMeshAttribute> format) {
        components.texture = ChunkProgramSingleTexture::new;
    }

    @Override
    protected ChunkProgramOneshot createShaderProgram(Identifier name, int handle, ChunkProgramComponentBuilder components) {
        return new ChunkProgramOneshot(name, handle, components);
    }

    @Override
    protected void addShaderConstants(ShaderConstants.Builder builder) {

    }

    @Override
    public void beginRender(MatrixStack matrixStack, BlockRenderPass pass) {
        super.beginRender(matrixStack, pass);

        this.vertexFormat.enableVertexAttributes();

        boolean mipped = pass.getId() == SingleTextureRenderPipeline.SOLID_MIPPED_PASS;
        this.activeProgram.texture.setMipmapping(mipped);
    }

    @Override
    public void endRender(MatrixStack matrixStack) {
        this.vertexFormat.disableVertexAttributes();
        GL20.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        super.endRender(matrixStack);
    }

    @Override
    public Class<GL20GraphicsState> getGraphicsStateType() {
        return GL20GraphicsState.class;
    }

    @Override
    public BlockRenderPassManager getRenderPassManager() {
        return this.renderPassManager;
    }

    @Override
    protected GL20GraphicsState createGraphicsState(MemoryTracker memoryTracker, ChunkRenderContainer<GL20GraphicsState> container) {
        return new GL20GraphicsState(memoryTracker, container);
    }

    public static boolean isSupported(boolean disableBlacklist) {
        return true;
    }

    @Override
    public String getRendererName() {
        return "Oneshot (GL 2.0)";
    }
}
