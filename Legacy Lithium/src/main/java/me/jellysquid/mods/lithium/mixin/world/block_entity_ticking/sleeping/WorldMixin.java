package me.jellysquid.mods.lithium.mixin.world.block_entity_ticking.sleeping;

import me.jellysquid.mods.lithium.common.util.collections.MaskedTickingBlockEntityList;
import me.jellysquid.mods.lithium.common.world.blockentity.BlockEntitySleepTracker;
import me.jellysquid.mods.lithium.common.world.blockentity.SleepingBlockEntity;
//import net.minecraft.block.entity.BlockEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
//import net.minecraft.util.profiler.Profiler;
//import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.DimensionType;
//import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
//import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.ISpawnWorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * Allows block entities to sleep.
 * Inspired by PallaPalla's lazy blockentities
 * @author 2No2Name
 */
@Mixin(World.class)
public abstract class WorldMixin implements BlockEntitySleepTracker {

    @Mutable
    @Shadow
    @Final
    public List<TileEntity> tickableTileEntities;

    @Shadow
    public abstract boolean isRemote();

    private MaskedTickingBlockEntityList<TileEntity> tickingBlockEntities$lithium;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void reinit(ISpawnWorldInfo properties, RegistryKey<World> registryKey, DimensionType dimensionType,
                        Supplier<IProfiler> supplier, boolean bl, boolean bl2, long l, CallbackInfo ci) {
        this.tickingBlockEntities$lithium = new MaskedTickingBlockEntityList<>(this.tickableTileEntities, blockEntity -> ((SleepingBlockEntity) blockEntity).canTickOnSide(this.isRemote()));
        this.tickableTileEntities = tickingBlockEntities$lithium;
    }

    @Redirect(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private Iterator<TileEntity> getAwakeBlockEntities(List<TileEntity> list) {
        if (list == this.tickableTileEntities && list instanceof MaskedTickingBlockEntityList) {
            return ((MaskedTickingBlockEntityList<TileEntity>) list).filteredIterator();
        }
        return list.iterator();
    }

    @Override
    public void setAwake(TileEntity blockEntity, boolean needsTicking) {
        this.tickingBlockEntities$lithium.setEntryVisible(blockEntity, needsTicking);
    }
}
