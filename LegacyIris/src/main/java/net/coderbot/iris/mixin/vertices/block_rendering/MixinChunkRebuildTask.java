package net.coderbot.iris.mixin.vertices.block_rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.coderbot.iris.block_rendering.BlockRenderingSettings;
import net.coderbot.iris.shaderpack.IdMap;
import net.coderbot.iris.vertices.BlockSensitiveBufferBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
//import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.RenderType;
//import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
//import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
//import net.minecraft.core.BlockPos;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
//import com.mojang.blaze3d.vertex.BufferBuilder;
//import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Captures and tracks the current block being rendered.
 *
 * Uses a priority of 999 so that we apply before Indigo's mixins.
 */
@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$ChunkRender$RebuildTask", priority = 999, remap = true)
public class MixinChunkRebuildTask {
	private static final String RENDER = "compile(FFFLnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;Lnet/minecraft/client/renderer/RegionRenderCacheBuilder;)Ljava/util/Set;";

	@Unique
	private BlockSensitiveBufferBuilder lastBufferBuilder;

	// Resolve the ID map on the main thread to avoid thread safety issues
	@Unique
	private final IdMap idMap = getIdMap();

	@Unique
	private IdMap getIdMap() {
		return BlockRenderingSettings.INSTANCE.getIdMap();
	}

	@Unique
	private short resolveBlockId(BlockState state) {
		if (idMap == null) {
			return -1;
		}

		return (short) (int) idMap.getBlockProperties().getOrDefault(state, -1);
	}

	@Inject(method = "compile(FFFLnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;Lnet/minecraft/client/renderer/RegionRenderCacheBuilder;)Ljava/util/Set;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;renderLiquid(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockDisplayReader;Lcom/mojang/blaze3d/vertex/IVertexBuilder;Lnet/minecraft/fluid/FluidState;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, remap = true)
	private void iris$onRenderLiquid(float cameraX, float cameraY, float cameraZ, ChunkRenderDispatcher.CompiledChunk data, RegionRenderCacheBuilder buffers, CallbackInfoReturnable<Set> cir, int i, BlockPos blockPos, BlockPos blockPos2, VisGraph chunkOcclusionDataBuilder, Set set, ChunkRenderCache chunkRendererRegion, MatrixStack poseStack, Random random, BlockRendererDispatcher blockRenderManager, Iterator var15, BlockPos blockPos3, BlockState blockState, Block block, FluidState fluidState, IModelData modelData, Iterator var21, RenderType renderType, BufferBuilder bufferBuilder2) {
		if (bufferBuilder2 instanceof BlockSensitiveBufferBuilder) {
			lastBufferBuilder = ((BlockSensitiveBufferBuilder) bufferBuilder2);
			// All fluids have a ShadersMod render type of 1, to match behavior of Minecraft 1.7 and earlier.
			// TODO: We're using createLegacyBlock? That seems like something that Mojang wants to deprecate.
			lastBufferBuilder.beginBlock(resolveBlockId(fluidState.createLegacyBlock()), (short) 1);
		}
	}

	@Inject(method = "compile(FFFLnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;Lnet/minecraft/client/renderer/RegionRenderCacheBuilder;)Ljava/util/Set;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;renderLiquid(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockDisplayReader;Lcom/mojang/blaze3d/vertex/IVertexBuilder;Lnet/minecraft/fluid/FluidState;)Z", shift = At.Shift.AFTER))
	private void iris$finishRenderingLiquid(float cameraX, float cameraY, float cameraZ, ChunkRenderDispatcher.CompiledChunk data, RegionRenderCacheBuilder buffers, CallbackInfoReturnable<Set> cir) {
		if (lastBufferBuilder != null) {
			lastBufferBuilder.endBlock();
			lastBufferBuilder = null;
		}
	}

	@Inject(method = "compile(FFFLnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;Lnet/minecraft/client/renderer/RegionRenderCacheBuilder;)Ljava/util/Set;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;renderModel(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockDisplayReader;Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;ZLjava/util/Random;Lnet/minecraftforge/client/model/data/IModelData;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void iris$onRenderBlock(float cameraX, float cameraY, float cameraZ, ChunkRenderDispatcher.CompiledChunk data, RegionRenderCacheBuilder buffers, CallbackInfoReturnable<Set> cir, int i, BlockPos blockPos, BlockPos blockPos2, VisGraph chunkOcclusionDataBuilder, Set set, ChunkRenderCache chunkRendererRegion, MatrixStack poseStack, Random random, BlockRendererDispatcher blockRenderManager, Iterator var15, BlockPos blockPos3, BlockState blockState, Block block, FluidState fluidState, IModelData modelData, Iterator var21, RenderType rendertype, RenderType rendertype2, BufferBuilder bufferBuilder2) {
		if (bufferBuilder2 instanceof BlockSensitiveBufferBuilder) {
			lastBufferBuilder = ((BlockSensitiveBufferBuilder) bufferBuilder2);
			// TODO: Resolve render types for normal blocks?
			lastBufferBuilder.beginBlock(resolveBlockId(blockState), (short) -1);
		}
	}

	@Inject(method = "compile(FFFLnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;Lnet/minecraft/client/renderer/RegionRenderCacheBuilder;)Ljava/util/Set;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;renderModel(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockDisplayReader;Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;ZLjava/util/Random;Lnet/minecraftforge/client/model/data/IModelData;)Z", shift = At.Shift.AFTER))
	private void iris$finishRenderingBlock(float cameraX, float cameraY, float cameraZ, ChunkRenderDispatcher.CompiledChunk data, RegionRenderCacheBuilder buffers, CallbackInfoReturnable<Set> cir) {
		if (lastBufferBuilder != null) {
			lastBufferBuilder.endBlock();
			lastBufferBuilder = null;
		}
	}
}