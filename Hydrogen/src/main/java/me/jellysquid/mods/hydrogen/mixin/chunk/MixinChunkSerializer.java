package me.jellysquid.mods.hydrogen.mixin.chunk;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
//import net.minecraft.world.ChunkSerializer;
//import net.minecraft.world.TickScheduler;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeContainer;
//import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
//import net.minecraft.world.chunk.ProtoChunk;
//import net.minecraft.world.chunk.UpgradeData;
//import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.storage.ChunkSerializer;
//import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ChunkSerializer.class)
public abstract class MixinChunkSerializer {
    private static final ThreadLocal<CompoundNBT> CAPTURED_TAGS = new ThreadLocal<>();

    @Shadow
    private static void readEntities(CompoundNBT tag, Chunk chunk) {
        throw new UnsupportedOperationException();
    }

    @Inject(method = "read", at = @At("HEAD"))
    private static void captureTag(ServerWorld world, TemplateManager structureManager, PointOfInterestManager poiStorage, ChunkPos pos, CompoundNBT tag, CallbackInfoReturnable<ChunkPrimer> cir) {
        // We can't access the method parameters in the later redirect, so capture them for this thread
        CAPTURED_TAGS.set(tag);
    }

    @Redirect(method = "read", at = @At(value = "NEW", target = "net/minecraft/world/chunk/Chunk"))
    private static Chunk create(World world, ChunkPos pos, BiomeContainer biomes, UpgradeData upgradeData, ITickList<Block> blockTickScheduler, ITickList<Fluid> fluidTickScheduler, long inhabitedTime, ChunkSection[] sections, Consumer<Chunk> loadToWorldConsumer) {
        CompoundNBT rootTag = CAPTURED_TAGS.get();

        if (rootTag == null) {
            throw new IllegalStateException("No captured tag was found");
        }

        CompoundNBT level = rootTag.getCompound("Level");

        // The (misleadingly named) writeEntities function below only cares about these two tags
        // However, the lambda can end up staying loaded with the chunk if it isn't within ticking radius of a player yet
        // In order to prevent huge NBT blobs from remaining loaded in memory all the time, we can strip all the other
        // data to save a fair bit of memory.
        CompoundNBT strippedTag = new CompoundNBT();
        strippedTag.put("Entities", level.getList("Entities", 10));
        strippedTag.put("TileEntities", level.getList("TileEntities", 10));

        return new Chunk(world, pos, biomes, upgradeData, blockTickScheduler, fluidTickScheduler, inhabitedTime, sections, (chunk) -> {
            readEntities(strippedTag, chunk);
        });
    }

    @Inject(method = "read", at = @At("RETURN"))
    private static void releaseTag(ServerWorld world, TemplateManager structureManager, PointOfInterestManager poiStorage, ChunkPos pos, CompoundNBT tag, CallbackInfoReturnable<ChunkPrimer> cir) {
        // Avoid leaking tags in memory
        CAPTURED_TAGS.remove();
    }
}
