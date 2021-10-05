package me.jellysquid.mods.lithium.mixin.ai.poi.fast_init;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.jellysquid.mods.lithium.common.world.interests.PointOfInterestTypeHelper;
import net.minecraft.block.BlockState;
import net.minecraft.village.PointOfInterestType;
//import net.minecraft.world.poi.PointOfInterestType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

/**
 * Replaces the backing map type with a faster collection type which uses reference equality.
 */
@Mixin(PointOfInterestType.class)
public class PointOfInterestTypeMixin {
    @Mutable
    @Shadow
    @Final
    private static Map<BlockState, PointOfInterestType> POIT_BY_BLOCKSTATE;

    static {
        POIT_BY_BLOCKSTATE = new Reference2ReferenceOpenHashMap<>(POIT_BY_BLOCKSTATE);

        PointOfInterestTypeHelper.init(new ObjectArraySet<>(POIT_BY_BLOCKSTATE.keySet()));
    }
}
