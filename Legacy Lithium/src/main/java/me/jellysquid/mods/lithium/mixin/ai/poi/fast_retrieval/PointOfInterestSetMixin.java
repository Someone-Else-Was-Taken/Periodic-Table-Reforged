package me.jellysquid.mods.lithium.mixin.ai.poi.fast_retrieval;

import me.jellysquid.mods.lithium.common.util.Collector;
import me.jellysquid.mods.lithium.common.world.interests.PointOfInterestSetFilterable;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestData;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
//import net.minecraft.world.poi.PointOfInterest;
//import net.minecraft.world.poi.PointOfInterestSet;
//import net.minecraft.world.poi.PointOfInterestStorage;
//import net.minecraft.world.poi.PointOfInterestType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(PointOfInterestData.class)
public class PointOfInterestSetMixin implements PointOfInterestSetFilterable {
    @Shadow
    @Final
    private Map<PointOfInterestType, Set<PointOfInterest>> byType;

    @Override
    public boolean get(Predicate<PointOfInterestType> type, PointOfInterestManager.Status status, Collector<PointOfInterest> consumer) {
        for (Map.Entry<PointOfInterestType, Set<PointOfInterest>> entry : this.byType.entrySet()) {
            if (!type.test(entry.getKey())) {
                continue;
            }

            for (PointOfInterest poi : entry.getValue()) {
                if (!status.getTest().test(poi)) {
                    continue;
                }

                if (!consumer.collect(poi)) {
                    return false;
                }
            }
        }

        return true;
    }
}
