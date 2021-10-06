package me.jellysquid.mods.lithium.common.ai.pathing;

//import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.pathfinding.PathNodeType;

public interface BlockStatePathingCache {
    PathNodeType getPathNodeType();

    PathNodeType getNeighborPathNodeType();
}
