package me.jellysquid.mods.lithium.mixin.ai.task;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.entity.LivingEntity;
//import net.minecraft.entity.ai.brain.MemoryModuleState;
//import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(Task.class)
public class TaskMixin<E extends LivingEntity> {
    @Mutable
    @Shadow
    @Final
    protected Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryState;


    @Inject(method = "<init>(Ljava/util/Map;II)V", at = @At("RETURN"))
    private void init(Map<MemoryModuleType<?>, MemoryModuleStatus> map, int int_1, int int_2, CallbackInfo ci) {
        this.requiredMemoryState = new Reference2ObjectOpenHashMap<>(map);
    }

    /**
     * @reason Replace stream-based code with traditional iteration, use a flattened array list to avoid pointer chasing
     * @author JellySquid
     */
    @Overwrite
    private boolean hasRequiredMemories(E entity) {
        Iterable<Reference2ObjectMap.Entry<MemoryModuleType<?>, MemoryModuleStatus>> iterable =
                Reference2ObjectMaps.fastIterable((Reference2ObjectOpenHashMap<MemoryModuleType<?>, MemoryModuleStatus>) this.requiredMemoryState);

        for (Reference2ObjectMap.Entry<MemoryModuleType<?>, MemoryModuleStatus> entry : iterable) {
            if (!entity.getBrain().hasMemory(entry.getKey(), entry.getValue())) {
                return false;
            }
        }

        return true;
    }
}
