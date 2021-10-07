package me.jellysquid.mods.lithium.mixin.entity.inactive_navigations;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import me.jellysquid.mods.lithium.common.entity.EntityNavigationExtended;
import me.jellysquid.mods.lithium.common.world.ServerWorldExtended;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
//import net.minecraft.entity.ai.pathing.EntityNavigation;
//import net.minecraft.entity.mob.MobEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.profiler.IProfiler;
import net.minecraft.server.MinecraftServer;
//import net.minecraft.server.WorldGenerationProgressListener;
//import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.profiler.Profiler;
//import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.DimensionType;
//import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
//import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
//import net.minecraft.world.gen.Spawner;
//import net.minecraft.world.gen.chunk.ChunkGenerator;
//import net.minecraft.world.level.ServerWorldProperties;
//import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraft.world.storage.SaveFormat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * This patch is supposed to reduce the cost of setblockstate calls that change the collision shape of a block.
 * In vanilla, changing the collision shape of a block will notify *ALL* EntityNavigations in the world.
 * As EntityNavigations only care about these changes when they actually have a currentPath, we skip the iteration
 * of many navigations. For that optimization we need to keep track of which navigations have a path and which do not.
 *
 * Another possible optimization for the future: If we can somehow find a maximum range that a navigation listens for,
 * we can partition the set by region/chunk/etc. to be able to only iterate over nearby EntityNavigations. In vanilla
 * however, that limit calculation includes the entity position, which can change by a lot very quickly in rare cases.
 * For this optimization we would need to add detection code for very far entity movements. Therefore we don't implement
 * this yet.
 */
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements ServerWorldExtended {
    @Mutable
    @Shadow
    @Final
    private Set<PathNavigator> navigations;

    private ReferenceOpenHashSet<PathNavigator> activeEntityNavigations;
    private ArrayList<PathNavigator> activeEntityNavigationUpdates;
    private boolean isIteratingActiveEntityNavigations;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(MinecraftServer server, Executor workerExecutor, SaveFormat.LevelSave session, IServerWorldInfo properties, RegistryKey<World> registryKey, DimensionType dimensionType, IChunkStatusListener worldGenerationProgressListener, ChunkGenerator chunkGenerator, boolean debugWorld, long l, List<ISpecialSpawner> list, boolean bl, CallbackInfo ci) {
        this.navigations = new ReferenceOpenHashSet<>(this.navigations);
        this.activeEntityNavigations = new ReferenceOpenHashSet<>();
        this.activeEntityNavigationUpdates = new ArrayList<>();
        this.isIteratingActiveEntityNavigations = false;
    }

    @Redirect(
            method = "onEntityAdded",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/MobEntity;getNavigation()Lnet/minecraft/pathfinding/PathNavigator;"
            )
    )
    private PathNavigator startListeningOnEntityLoad(MobEntity mobEntity) {
        PathNavigator navigation = mobEntity.getNavigator();
        ((EntityNavigationExtended) navigation).setRegisteredToWorld(true);
        if (navigation.getPath() != null) {
            this.activeEntityNavigations.add(navigation);
        }
        return navigation;
    }

    @Redirect(
            method = "removeEntityComplete",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Set;remove(Ljava/lang/Object;)Z"
            )
    )
    private boolean stopListeningOnEntityUnload(Set<PathNavigator> set, Object navigation) {
        PathNavigator entityNavigation = (PathNavigator) navigation;
        ((EntityNavigationExtended) entityNavigation).setRegisteredToWorld(false);
        this.activeEntityNavigations.remove(entityNavigation);
        return set.remove(entityNavigation);
    }

    /**
     * Optimization: Only update listeners that may care about the update. Listeners which have no path
     * never react to the update.
     * With thousands of non-pathfinding mobs in the world, this can be a relevant difference.
     */
    @Redirect(
            method = "notifyBlockUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"
            )
    )
    private Iterator<PathNavigator> getActiveListeners(Set<PathNavigator> set) {
        this.isIteratingActiveEntityNavigations = true;
        return this.activeEntityNavigations.iterator();
    }

    @Inject(method = "notifyBlockUpdate", at = @At("RETURN"))
    private void onIterationFinished(BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo ci) {
        this.isIteratingActiveEntityNavigations = false;
        if (!this.activeEntityNavigationUpdates.isEmpty()) {
            this.applyActiveEntityNavigationUpdates();
        }
    }

    private void applyActiveEntityNavigationUpdates() {
        ArrayList<PathNavigator> entityNavigationsUpdates = this.activeEntityNavigationUpdates;
        for (int i = entityNavigationsUpdates.size() - 1; i >= 0; i--) {
            PathNavigator entityNavigation = entityNavigationsUpdates.remove(i);
            if (entityNavigation.getPath() != null && ((EntityNavigationExtended) entityNavigation).isRegisteredToWorld()) {
                this.activeEntityNavigations.add(entityNavigation);
            } else {
                this.activeEntityNavigations.remove(entityNavigation);
            }
        }
    }

    @Override
    public void setNavigationActive(Object entityNavigation) {
        PathNavigator entityNavigation1 = (PathNavigator) entityNavigation;
        if (!this.isIteratingActiveEntityNavigations) {
            this.activeEntityNavigations.add(entityNavigation1);
        } else {
            this.activeEntityNavigationUpdates.add(entityNavigation1);
        }
    }

    @Override
    public void setNavigationInactive(Object entityNavigation) {
        PathNavigator entityNavigation1 = (PathNavigator) entityNavigation;
        if (!this.isIteratingActiveEntityNavigations) {
            this.activeEntityNavigations.remove(entityNavigation1);
        } else {
            this.activeEntityNavigationUpdates.add(entityNavigation1);
        }
    }

    protected ServerWorldMixin(ISpawnWorldInfo properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<IProfiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
    }

    /**
     * Debug function
     * @return whether the activeEntityNavigation set is in the correct state
     */
    public boolean isConsistent() {
        int i = 0;
        for (PathNavigator entityNavigation : this.navigations) {
            if ((entityNavigation.getPath() != null && ((EntityNavigationExtended) entityNavigation).isRegisteredToWorld()) != this.activeEntityNavigations.contains(entityNavigation)) {
                return false;
            }
            if (entityNavigation.getPath() != null) {
                i++;
            }
        }
        return this.activeEntityNavigations.size() == i;
    }
}