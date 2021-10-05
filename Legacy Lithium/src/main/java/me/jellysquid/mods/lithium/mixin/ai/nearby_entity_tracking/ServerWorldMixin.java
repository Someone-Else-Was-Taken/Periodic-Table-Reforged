package me.jellysquid.mods.lithium.mixin.ai.nearby_entity_tracking;

import me.jellysquid.mods.lithium.common.entity.tracker.EntityTrackerEngine;
import me.jellysquid.mods.lithium.common.entity.tracker.EntityTrackerEngineProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
//import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;

/**
 * Installs event listeners to the world class which will be used to notify the {@link EntityTrackerEngine} of changes.
 */
@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    /**
     * Notify the entity tracker when an entity is removed from the world.
     */
    @Redirect(
            method = "onChunkUnloading",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Iterator;next()Ljava/lang/Object;"
            )
    )
    private Object onEntityRemoved(Iterator<Entity> iterator) {
        Entity entity = iterator.next();
        if (!(entity instanceof LivingEntity)) {
            return entity;
        }

        int chunkX = MathHelper.floor(entity.getPosX()) >> 4;
        int chunkY = MathHelper.clamp(MathHelper.floor(entity.getPosY()) >> 4, 0, 15);
        int chunkZ = MathHelper.floor(entity.getPosZ()) >> 4;

        EntityTrackerEngine tracker = EntityTrackerEngineProvider.getEntityTracker(this);
        tracker.onEntityRemoved(chunkX, chunkY, chunkZ, (LivingEntity) entity);
        return entity;
    }
}
