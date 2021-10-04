package me.jellysquid.mods.phosphor.mixin.world;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.SectionPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ChunkSerializer;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.ListTag;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
//import net.minecraft.util.math.ChunkSectionPos;
//import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.LightType;
//import net.minecraft.world.chunk.ChunkNibbleArray;
//import net.minecraft.world.chunk.ProtoChunk;
//import net.minecraft.world.chunk.light.LightingProvider;
//import net.minecraft.world.poi.PointOfInterestStorage;

@Mixin(ChunkSerializer.class)
public abstract class MixinChunkSerializer {
    @Inject(
            method = "read(Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/world/gen/feature/template/TemplateManager;Lnet/minecraft/village/PointOfInterestManager;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/nbt/CompoundNBT;)Lnet/minecraft/world/chunk/ChunkPrimer;",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/chunk/AbstractChunkProvider;getLightManager()Lnet/minecraft/world/lighting/WorldLightManager;",
                    ordinal = 0
            )
    )
    private static void loadLightmaps(final ServerWorld world, final TemplateManager structureManager, final PointOfInterestManager poiStorage, final ChunkPos pos, final CompoundNBT tag, final CallbackInfoReturnable<ChunkPrimer> ci) {
        final CompoundNBT levelTag = tag.getCompound("Level");

        // Load lightmaps of pre_light chunks unless erasing cached data
        if (levelTag.getBoolean("isLightOn") || !levelTag.contains("Heightmaps", 10)) {
            return;
        }

        final ListNBT sections = levelTag.getList("Sections", 10);
        final WorldLightManager lightingProvider = world.getChunkProvider().getLightManager();
        final boolean hasSkyLight = world.getDimensionType().hasSkyLight();

        lightingProvider.retainData(pos, true);

        for(int i = 0; i < sections.size(); ++i) {
            final CompoundNBT section = sections.getCompound(i);
            final int y = section.getByte("Y");

            if (section.contains("BlockLight", 7)) {
                lightingProvider.setData(LightType.BLOCK, SectionPos.from(pos, y), new NibbleArray(section.getByteArray("BlockLight")), true);
            }

            if (hasSkyLight && section.contains("SkyLight", 7)) {
                lightingProvider.setData(LightType.SKY, SectionPos.from(pos, y), new NibbleArray(section.getByteArray("SkyLight")), true);
            }
        }
    }
}
