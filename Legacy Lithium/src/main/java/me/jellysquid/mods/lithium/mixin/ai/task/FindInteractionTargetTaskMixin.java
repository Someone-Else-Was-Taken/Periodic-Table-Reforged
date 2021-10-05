package me.jellysquid.mods.lithium.mixin.ai.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
//import net.minecraft.entity.ai.brain.EntityLookTarget;
//import net.minecraft.entity.ai.brain.MemoryModuleState;
//import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.FindInteractionAndLookTargetTask;
//import net.minecraft.entity.ai.brain.task.FindInteractionTargetTask;
import net.minecraft.entity.ai.brain.task.Task;
//import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Mixin(FindInteractionAndLookTargetTask.class)
public abstract class FindInteractionTargetTaskMixin extends Task<LivingEntity> {
    @Shadow
    @Final
    private Predicate<LivingEntity> field_220536_d;

    @Shadow
    protected abstract List<LivingEntity> getVisibleMobs(LivingEntity entity);

    @Shadow
    protected abstract boolean isNearInteractableEntity(LivingEntity entity);

    @Shadow
    @Final
    private int field_220534_b;

    public FindInteractionTargetTaskMixin(Map<MemoryModuleType<?>, MemoryModuleStatus> memories) {
        super(memories);
    }

    /**
     * @reason Replace stream code with traditional iteration
     * @author JellySquid
     */
    @Overwrite
    public boolean shouldExecute(ServerWorld world, LivingEntity entity) {
        if (!this.field_220536_d.test(entity)) {
            return false;
        }

        List<LivingEntity> visibleEntities = this.getVisibleMobs(entity);

        for (LivingEntity otherEntity : visibleEntities) {
            if (this.isNearInteractableEntity(otherEntity)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @reason Replace stream code with traditional iteration
     * @author JellySquid
     */
    @Overwrite
    public void startExecuting(ServerWorld world, LivingEntity entity, long time) {
        super.startExecuting(world, entity, time);

        Brain<?> brain = entity.getBrain();

        List<LivingEntity> visibleEntities = brain.getMemory(MemoryModuleType.VISIBLE_MOBS)
                .orElse(Collections.emptyList());

        for (LivingEntity otherEntity : visibleEntities) {
            if (otherEntity.getDistanceSq(entity) > (double) this.field_220534_b) {
                continue;
            }

            if (this.isNearInteractableEntity(otherEntity)) {
                brain.setMemory(MemoryModuleType.INTERACTION_TARGET, otherEntity);
                brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(otherEntity, true));

                break;
            }
        }
    }

}
