package me.jellysquid.mods.lithium.mixin.ai.pathing;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Region;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
//import net.minecraft.world.chunk.ChunkCache;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The hottest part of path-finding is reading blocks out from the world. This patch makes a number of changes to
 * avoid slow paths in the game and to better inline code. In testing, it shows a small improvement in path-finding
 * code.
 */
@Mixin(Region.class)
public class ChunkCacheMixin {
    private static final BlockState DEFAULT_BLOCK = Blocks.AIR.getDefaultState();

    @Shadow
    @Final
    protected IChunk[][] chunks;

    @Shadow
    @Final
    protected int chunkX;

    @Shadow
    @Final
    protected int chunkZ;

    // A 1D view of the chunks available to this cache
    private IChunk[] chunksFlat;

    // The x/z length of this cache
    private int xLen, zLen;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(World world, BlockPos minPos, BlockPos maxPos, CallbackInfo ci) {
        this.xLen = 1 + (maxPos.getX() >> 4) - (minPos.getX() >> 4);
        this.zLen = 1 + (maxPos.getZ() >> 4) - (minPos.getZ() >> 4);

        this.chunksFlat = new Chunk[this.xLen * this.zLen];

        // Flatten the 2D chunk array into our 1D array
        for (int x = 0; x < this.xLen; x++) {
            System.arraycopy(this.chunks[x], 0, this.chunksFlat, x * this.zLen, this.zLen);
        }
    }

    /**
     * @reason Use optimized function
     * @author JellySquid
     */
    @Overwrite
    public BlockState getBlockState(BlockPos pos) {
        int y = pos.getY();

        if (!World.isYOutOfBounds(pos.getY())) {
            int x = pos.getX();
            int z = pos.getZ();

            int chunkX = (x >> 4) - this.chunkX;
            int chunkZ = (z >> 4) - this.chunkZ;

            if (chunkX >= 0 && chunkX < this.xLen && chunkZ >= 0 && chunkZ < this.zLen) {
                IChunk chunk = this.chunksFlat[(chunkX * this.zLen) + chunkZ];

                // Avoid going through Chunk#getBlockState
                if (chunk != null) {
                    ChunkSection section = chunk.getSections()[y >> 4];

                    if (section != null) {
                        return section.getBlockState(x & 15, y & 15, z & 15);
                    }
                }
            }
        }

        return DEFAULT_BLOCK;
    }

    /**
     * @reason Use optimized function
     * @author JellySquid
     */
    @Overwrite
    public FluidState getFluidState(BlockPos pos) {
        return this.getBlockState(pos).getFluidState();
    }
}


