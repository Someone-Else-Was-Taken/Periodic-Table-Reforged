package me.jellysquid.mods.lithium.mixin.ai.task;

import me.jellysquid.mods.lithium.common.ai.WeightedListIterable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
//import net.minecraft.entity.ai.brain.MemoryModuleType;
//import net.minecraft.entity.ai.brain.task.CompositeTask;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTask;
import net.minecraft.entity.ai.brain.task.Task;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.WeightedList;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(MultiTask.class)
public class CompositeTaskMixin<E extends LivingEntity> {
    @Shadow
    @Final
    private WeightedList<Task<? super E>> field_220419_e;

    @Shadow
    @Final
    private Set<MemoryModuleType<?>> field_220416_b;

    /**
     * @reason Replace stream code with traditional iteration
     * @author JellySquid
     */
    @Overwrite
    protected boolean shouldContinueExecuting(ServerWorld world, E entity, long time) {
        for (Task<? super E> task : WeightedListIterable.cast(this.field_220419_e)) {
            if (task.getStatus() == Task.Status.RUNNING) {
                if (task.shouldContinueExecuting(world, entity, time)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @reason Replace stream code with traditional iteration
     * @author JellySquid
     */
    @Overwrite
    public void updateTask(ServerWorld world, E entity, long time) {
        for (Task<? super E> task : WeightedListIterable.cast(this.field_220419_e)) {
            if (task.getStatus() == Task.Status.RUNNING) {
                task.tick(world, entity, time);
            }
        }
    }

    /**
     * @reason Replace stream code with traditional iteration
     * @author JellySquid
     */
    @Overwrite
    public void resetTask(ServerWorld world, E entity, long time) {
        for (Task<? super E> task : WeightedListIterable.cast(this.field_220419_e)) {
            if (task.getStatus() == Task.Status.RUNNING) {
                task.stop(world, entity, time);
            }
        }

        Brain<?> brain = entity.getBrain();

        for (MemoryModuleType<?> module : this.field_220416_b) {
            brain.removeMemory(module);
        }
    }
}
