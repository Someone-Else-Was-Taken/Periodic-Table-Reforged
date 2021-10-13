package me.jellysquid.mods.sodium.client.render.chunk.tasks;

import me.jellysquid.mods.sodium.client.render.chunk.ChunkGraphicsState;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderBackend;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderContainer;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildResult;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkMeshData;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderBounds;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderData;
import me.jellysquid.mods.sodium.client.render.chunk.passes.BlockRenderPass;
import me.jellysquid.mods.sodium.client.render.pipeline.context.ChunkRenderContext;
import me.jellysquid.mods.sodium.client.util.task.CancellationSource;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.client.render.RenderLayer;
//import net.minecraft.client.render.RenderLayers;
//import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
//import net.minecraft.client.render.block.entity.BlockEntityRenderer;
//import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
//import net.minecraft.client.util.math.Vector3d;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.chunk.Chunk;
//import net.minecraft.world.chunk.WorldChunk;

/**
 * Rebuilds all the meshes of a chunk for each given render pass with non-occluded blocks. The result is then uploaded
 * to graphics memory on the main thread.
 *
 * This task takes a slice of the world from the thread it is created on. Since these slices require rather large
 * array allocations, they are pooled to ensure that the garbage collector doesn't become overloaded.
 */
public class ChunkRenderRebuildTask<T extends ChunkGraphicsState> extends ChunkRenderBuildTask<T> {
    private final ChunkRenderBackend<T> renderBackend;
    private final ChunkRenderContainer<T> render;
    private final ChunkBuilder<T> chunkBuilder;
    private final Vector3d camera;
    private final WorldSlice slice;
    private final BlockPos offset;

    public ChunkRenderRebuildTask(ChunkBuilder<T> chunkBuilder, ChunkRenderBackend<T> renderBackend, ChunkRenderContainer<T> render, WorldSlice slice) {
        this.renderBackend = renderBackend;
        this.chunkBuilder = chunkBuilder;
        this.render = render;
        this.camera = chunkBuilder.getCameraPosition();
        this.slice = slice;
        this.offset = render.getRenderOrigin();
    }

    @Override
    public ChunkBuildResult<T> performBuild(ChunkRenderContext pipeline, ChunkBuildBuffers buffers, CancellationSource cancellationSource) {
        ChunkRenderData.Builder renderData = new ChunkRenderData.Builder();
        VisGraph occluder = new VisGraph();
        ChunkRenderBounds.Builder bounds = new ChunkRenderBounds.Builder();

        pipeline.init(this.slice, this.slice.getBlockOffsetX(), this.slice.getBlockOffsetY(), this.slice.getBlockOffsetZ());
        buffers.init(renderData);

        int minX = this.render.getOriginX();
        int minY = this.render.getOriginY();
        int minZ = this.render.getOriginZ();

        int maxX = minX + 16;
        int maxY = minY + 16;
        int maxZ = minZ + 16;

        BlockPos.Mutable pos = new BlockPos.Mutable();
        BlockPos offset = this.offset;

        for (int y = minY; y < maxY; y++) {
            if (cancellationSource.isCancelled()) {
                return null;
            }

            for (int z = minZ; z < maxZ; z++) {
                for (int x = minX; x < maxX; x++) {
                    BlockState blockState = this.slice.getBlockState(x, y, z);
                    Block block = blockState.getBlock();

                    if (blockState.isAir()) {
                        continue;
                    }

                    pos.setPos(x, y, z);

                    if (block.getRenderType(blockState) == BlockRenderType.MODEL) {
                        RenderType layer = RenderTypeLookup.getChunkRenderType(blockState);

                        ChunkBuildBuffers.ChunkBuildBufferDelegate builder = buffers.get(layer);
                        builder.setOffset(x - offset.getX(), y - offset.getY(), z - offset.getZ());

                        if (pipeline.renderBlock(this.slice, blockState, pos, builder, true)) {
                            bounds.addBlock(x, y, z);
                        }
                    }

                    FluidState fluidState = block.getFluidState(blockState);

                    if (!fluidState.isEmpty()) {
                        RenderType layer = RenderTypeLookup.getRenderType(fluidState);

                        ChunkBuildBuffers.ChunkBuildBufferDelegate builder = buffers.get(layer);
                        builder.setOffset(x - offset.getX(), y - offset.getY(), z - offset.getZ());

                        if (pipeline.renderFluid(this.slice, fluidState, pos, builder)) {
                            bounds.addBlock(x, y, z);
                        }
                    }

                    if (block.isTileEntityProvider()) {
                        TileEntity entity = this.slice.getBlockEntity(pos, Chunk.CreateEntityType.CHECK);

                        if (entity != null) {
                            TileEntityRenderer<TileEntity> renderer = TileEntityRendererDispatcher.instance.getRenderer(entity);

                            if (renderer != null) {
                                renderData.addBlockEntity(entity, !renderer.isGlobalRenderer(entity));

                                bounds.addBlock(x, y, z);
                            }
                        }
                    }

                    if (blockState.isOpaqueCube(this.slice, pos)) {
                        occluder.setOpaqueCube(pos);
                    }
                }
            }
        }

        for (BlockRenderPass pass : this.renderBackend.getRenderPassManager().getSortedPasses()) {
            ChunkMeshData mesh = buffers.createMesh(pass);

            if (mesh != null) {
                renderData.setMesh(pass, mesh);
            }
        }

        renderData.setOcclusionData(occluder.computeVisibility());
        renderData.setBounds(bounds.build(this.render.getChunkPos()));

        return new ChunkBuildResult<>(this.render, renderData.build());
    }

    @Override
    public void releaseResources() {
        this.chunkBuilder.releaseWorldSlice(this.slice);
    }
}
