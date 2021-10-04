package me.jellysquid.mods.phosphor.mixin.chunk.light;

import me.jellysquid.mods.phosphor.common.chunk.level.LevelPropagatorExtended;
import me.jellysquid.mods.phosphor.common.chunk.light.BlockLightStorageAccess;
import me.jellysquid.mods.phosphor.common.chunk.light.LightProviderBlockAccess;
import me.jellysquid.mods.phosphor.common.chunk.light.LightStorageAccess;
import me.jellysquid.mods.phosphor.common.util.LightUtil;
import me.jellysquid.mods.phosphor.common.util.math.DirectionHelper;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.ChunkSectionPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.LightType;
//import net.minecraft.world.chunk.ChunkProvider;
//import net.minecraft.world.chunk.light.BlockLightStorage;
//import net.minecraft.world.chunk.light.ChunkBlockLightProvider;
//import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.lighting.BlockLightEngine;
import net.minecraft.world.lighting.BlockLightStorage;
import net.minecraft.world.lighting.LightEngine;
import org.spongepowered.asm.mixin.*;

import static net.minecraft.util.math.SectionPos.toChunk;

@Mixin(BlockLightEngine.class)
public abstract class MixinChunkBlockLightProvider extends LightEngine<BlockLightStorage.StorageMap, BlockLightStorage>
        implements LevelPropagatorExtended, LightProviderBlockAccess {
    public MixinChunkBlockLightProvider(IChunkLightProvider chunkProvider, LightType type, BlockLightStorage lightStorage) {
        super(chunkProvider, type, lightStorage);
    }

    @Shadow
    protected abstract int getLightValue(long blockPos);

    @Shadow
    @Final
    private static Direction[] DIRECTIONS;

    /**
     * @reason Use optimized variant
     * @author JellySquid
     */
    @Override
    @Overwrite
    public int getEdgeLevel(long fromId, long toId, int currentLevel) {
        return this.getPropagatedLevel(fromId, null, toId, currentLevel);
    }

    /**
     * This breaks up the call to method_20479 into smaller parts so we do not have to pass a mutable heap object
     * to the method in order to extract the light result. This has a few other advantages, allowing us to:
     * - Avoid the de-optimization that occurs from allocating and passing a heap object
     * - Avoid unpacking coordinates twice for both the call to method_20479 and method_20710.
     * - Avoid the the specific usage of AtomicInteger, which has additional overhead for the atomic get/set operations.
     * - Avoid checking if the checked block is opaque twice.
     * - Avoid a redundant block state lookup by re-using {@param fromState}
     * <p>
     * The rest of the implementation has been otherwise copied from vanilla, but is optimized to avoid constantly
     * (un)packing coordinates and to use an optimized direction lookup function.
     *
     * @param fromState The re-usable block state at position {@param fromId}
     * @author JellySquid
     */
    @Override
    public int getPropagatedLevel(long fromId, BlockState fromState, long toId, int currentLevel) {
        if (toId == Long.MAX_VALUE) {
            return 15;
        } else if (fromId == Long.MAX_VALUE && ((BlockLightStorageAccess) this.storage).isLightEnabled(SectionPos.worldToSection(toId))) {
            // Disable blocklight sources before initial lighting
            return currentLevel + 15 - this.getLightValue(toId);
        } else if (currentLevel >= 15) {
            return currentLevel;
        }

        int toX = BlockPos.unpackX(toId);
        int toY = BlockPos.unpackY(toId);
        int toZ = BlockPos.unpackZ(toId);

        int fromX = BlockPos.unpackX(fromId);
        int fromY = BlockPos.unpackY(fromId);
        int fromZ = BlockPos.unpackZ(fromId);

        Direction dir = DirectionHelper.getVecDirection(toX - fromX, toY - fromY, toZ - fromZ);

        if (dir != null) {
            BlockState toState = this.getBlockStateForLighting(toX, toY, toZ);

            if (toState == null) {
                return 15;
            }

            int newLevel = this.getSubtractedLight(toState, toX, toY, toZ);

            if (newLevel >= 15) {
                return 15;
            }

            if (fromState == null) {
                fromState = this.getBlockStateForLighting(fromX, fromY, fromZ);
            }

            VoxelShape aShape = this.getOpaqueShape(fromState, fromX, fromY, fromZ, dir);
            VoxelShape bShape = this.getOpaqueShape(toState, toX, toY, toZ, dir.getOpposite());

            if (!LightUtil.unionCoversFullCube(aShape, bShape)) {
                return currentLevel + Math.max(1, newLevel);
            }
        }

        return 15;
    }



    /**
     * Avoids constantly (un)packing coordinates. This strictly copies vanilla's implementation.
     * @reason Use faster implementation
     * @author JellySquid
     */
    @Overwrite
    public void notifyNeighbors(long id, int targetLevel, boolean mergeAsMin) {
        int x = BlockPos.unpackX(id);
        int y = BlockPos.unpackY(id);
        int z = BlockPos.unpackZ(id);

        long chunk = SectionPos.asLong(toChunk(x), toChunk(y), toChunk(z));

        BlockState state = this.getBlockStateForLighting(x, y, z);

        for (Direction dir : DIRECTIONS) {
            int adjX = x + dir.getXOffset();
            int adjY = y + dir.getYOffset();
            int adjZ = z + dir.getZOffset();

            long adjChunk = SectionPos.asLong(toChunk(adjX), toChunk(adjY), toChunk(adjZ));

            if ((chunk == adjChunk) || this.storage.hasSection(adjChunk)) {
                this.propagateLevel(id, state, BlockPos.pack(adjX, adjY, adjZ), targetLevel, mergeAsMin);
            }
        }
    }
}