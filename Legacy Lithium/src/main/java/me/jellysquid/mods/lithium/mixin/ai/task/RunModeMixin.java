package me.jellysquid.mods.lithium.mixin.ai.task;

import me.jellysquid.mods.lithium.common.ai.WeightedListIterable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
//import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WeightedList;
//import net.minecraft.util.collection.WeightedList;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

public class RunModeMixin {
    @Mixin(targets = "net/minecraft/entity/ai/brain/task/MultiTask$RunType$1")
    public static class RunOneMixin {
        /**
         * @reason Replace stream code with traditional iteration
         * @author JellySquid
         */
        @Overwrite
        public <E extends LivingEntity> void func_220630_a(WeightedList<Task<? super E>> tasks, ServerWorld world, E entity, long time) {
            for (Task<? super E> task : WeightedListIterable.cast(tasks)) {
                if (task.getStatus() == Task.Status.STOPPED) {
                    if (task.start(world, entity, time)) {
                        break;
                    }
                }
            }
        }
    }

    @Mixin(targets = "net/minecraft/entity/ai/brain/task/MultiTask$RunType$2")
    public static class TryAllMixin {
        /**
         * @reason Replace stream code with traditional iteration
         * @author JellySquid
         */
        @Overwrite
        public <E extends LivingEntity> void func_220630_a(WeightedList<Task<? super E>> tasks, ServerWorld world, E entity, long time) {
            for (Task<? super E> task : WeightedListIterable.cast(tasks)) {
                if (task.getStatus() == Task.Status.STOPPED) {
                    task.start(world, entity, time);
                }
            }
        }
    }
}
