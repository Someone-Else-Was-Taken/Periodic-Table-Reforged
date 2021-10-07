package me.jellysquid.mods.lithium.mixin.ai.pathing;

import me.jellysquid.mods.lithium.common.ai.pathing.PathNodeCache;
//import net.minecraft.entity.ai.pathing.Path;
//import net.minecraft.entity.ai.pathing.PathNode;
//import net.minecraft.entity.ai.pathing.PathNodeNavigator;
//import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.pathfinding.FlaggedPathPoint;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(PathFinder.class)
public class PathNodeNavigatorMixin {
    @Inject(method = "generatePath(Lnet/minecraft/pathfinding/PathPoint;Ljava/util/Map;FIF)Lnet/minecraft/pathfinding/Path;", at = @At("HEAD"))
    private void preFindPathToAny(PathPoint startNode, Map<FlaggedPathPoint, BlockPos> positions, float followRange, int distance, float rangeMultiplier, CallbackInfoReturnable<Path> cir) {
        PathNodeCache.enableChunkCache();
    }

    @Inject(method = "generatePath(Lnet/minecraft/pathfinding/PathPoint;Ljava/util/Map;FIF)Lnet/minecraft/pathfinding/Path;", at = @At("RETURN"))
    private void postFindPathToAny(PathPoint startNode, Map<FlaggedPathPoint, BlockPos> positions, float followRange, int distance, float rangeMultiplier, CallbackInfoReturnable<Path> cir) {
        PathNodeCache.disableChunkCache();
    }
}
