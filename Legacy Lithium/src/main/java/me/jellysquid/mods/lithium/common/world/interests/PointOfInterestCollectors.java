package me.jellysquid.mods.lithium.common.world.interests;

import me.jellysquid.mods.lithium.common.util.Collector;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestData;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
//import net.minecraft.world.poi.PointOfInterest;
//import net.minecraft.world.poi.PointOfInterestSet;
//import net.minecraft.world.poi.PointOfInterestStorage;
//import net.minecraft.world.poi.PointOfInterestType;

import java.util.function.Predicate;

public class PointOfInterestCollectors {
    public static Collector<PointOfInterest> collectAllWithinRadius(BlockPos pos, double radius, Collector<PointOfInterest> out) {
        double radiusSq = radius * radius;

        return (point) -> {
            if (point.getPos().distanceSq(pos) <= radiusSq) {
                return out.collect(point);
            }

            return true;
        };
    }

    public static Collector<PointOfInterestData> collectAllMatching(Predicate<PointOfInterestType> predicate, PointOfInterestManager.Status status, Collector<PointOfInterest> out) {
        return (set) -> ((PointOfInterestSetFilterable) set).get(predicate, status, out);
    }
}
