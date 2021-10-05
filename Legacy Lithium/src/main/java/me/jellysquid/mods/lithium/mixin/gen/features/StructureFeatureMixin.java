package me.jellysquid.mods.lithium.mixin.gen.features;

//import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.ChunkPos;
//import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.IStructureReader;
import net.minecraft.world.IWorldReader;
//import net.minecraft.world.StructureHolder;
//import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
//import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructureStart;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * Why generate an empty chunk to check for a structure if the chunk's biome cannot generate the
 * structure anyway? Checking the biome first = SPEED!
 *
 * @author TelepathicGrunt
 */
@Mixin(Structure.class)
public class StructureFeatureMixin {

    /**
     * @reason Return null chunk if biome doesn't match structure
     * @author MrGrim
     */
    @Redirect(
            method = "func_236388_a_",
            slice = @Slice(
                    from = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/world/chunk/ChunkStatus;STRUCTURE_STARTS:Lnet/minecraft/world/chunk/ChunkStatus;", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/IChunk;getPos()Lnet/minecraft/util/math/ChunkPos;", ordinal = 0)
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/IWorldReader;getChunk(IILnet/minecraft/world/chunk/ChunkStatus;)Lnet/minecraft/world/chunk/IChunk;",
                    ordinal = 0
            )
    )
    private IChunk biomeConditionalGetChunk(IWorldReader worldView, int x, int z, ChunkStatus status) {
        //magic numbers << 2) + 2 and biomeY = 0 taken from ChunkGenerator.setStructureStarts
        //noinspection rawtypes
        if (worldView.getNoiseBiome((x << 2) + 2, 0, (z << 2) + 2).getGenerationSettings().hasStructure((Structure) (Object) this)) {
            return worldView.getChunk(x, z, status);
        } else {
            return null;
        }
    }

    /**
     * @reason Can't avoid the call to Chunk.getPos(), and now the chunk might be null.
     * Send a new (0,0) ChunkPos if so. It won't be used anyway.
     * @author MrGrim
     */
    @Redirect(
            method = "func_236388_a_",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/IWorldReader;getChunk(IILnet/minecraft/world/chunk/ChunkStatus;)Lnet/minecraft/world/chunk/IChunk;", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/IChunk;getPos()Lnet/minecraft/util/math/ChunkPos;", ordinal = 0)
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/IChunk;getPos()Lnet/minecraft/util/math/ChunkPos;", ordinal = 0
            )
    )
    private ChunkPos checkForNull(IChunk chunk) {
        return chunk == null ? new ChunkPos(0, 0) : chunk.getPos();
    }

    /**
     * @reason Return null here if the chunk is null. This will bypass the following if statement
     * allowing the search to continue.
     * @author MrGrim
     */
    @Redirect(
            method = "func_236388_a_",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/SectionPos;from(Lnet/minecraft/util/math/ChunkPos;I)Lnet/minecraft/util/math/SectionPos;", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/structure/StructureStart;isValid()Z", ordinal = 0)
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/feature/structure/StructureManager;getStructureStart(Lnet/minecraft/util/math/SectionPos;Lnet/minecraft/world/gen/feature/structure/Structure;Lnet/minecraft/world/IStructureReader;)Lnet/minecraft/world/gen/feature/structure/StructureStart;",
                    ordinal = 0
            )
    )
    private StructureStart<?> checkChunkBeforeGetStructureStart(StructureManager structureAccessor, SectionPos sectionPos, Structure<?> thisStructure, IStructureReader chunk) {
        return chunk == null ? null : structureAccessor.getStructureStart(sectionPos, thisStructure, chunk);
    }
}