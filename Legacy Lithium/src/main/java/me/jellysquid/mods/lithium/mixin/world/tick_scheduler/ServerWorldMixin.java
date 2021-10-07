package me.jellysquid.mods.lithium.mixin.world.tick_scheduler;

import me.jellysquid.mods.lithium.common.world.scheduler.LithiumServerTickScheduler;
//import net.minecraft.server.world.ServerTickScheduler;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.util.Identifier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.NextTickListEntry;
//import net.minecraft.world.ScheduledTick;
import net.minecraft.world.server.ServerTickList;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    /**
     * Redirects the creation of the vanilla server tick scheduler with our own. This only happens once per world load.
     */
    @Redirect(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/server/world/ServerTickScheduler"
            )
    )
    private <T> ServerTickList<T> redirectServerTickSchedulerCtor(ServerWorld world, Predicate<T> invalidPredicate, Function<T, ResourceLocation> idToName, Consumer<NextTickListEntry<T>> tickConsumer) {
        return new LithiumServerTickScheduler<>(world, invalidPredicate, idToName, tickConsumer);
    }
}

