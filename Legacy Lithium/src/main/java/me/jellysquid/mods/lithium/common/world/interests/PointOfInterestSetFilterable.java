package me.jellysquid.mods.lithium.common.world.interests;

import me.jellysquid.mods.lithium.common.util.Collector;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
//import net.minecraft.world.poi.PointOfInterest;
//import net.minecraft.world.poi.PointOfInterestStorage;
//import net.minecraft.world.poi.PointOfInterestType;

import java.util.function.Predicate;

public interface PointOfInterestSetFilterable {
    boolean get(Predicate<PointOfInterestType> type, PointOfInterestManager.Status status, Collector<PointOfInterest> consumer);
}
