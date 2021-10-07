package me.jellysquid.mods.lithium.mixin.alloc.enum_values;

//import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    private static final EquipmentSlotType[] SLOTS = EquipmentSlotType.values();

    /**
     * @reason Avoid cloning enum values
     */
    @Redirect(
            method = "func_241354_r_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EquipmentSlot;values()[Lnet/minecraft/entity/EquipmentSlot;"
            )
    )
    private EquipmentSlotType[] redirectEquipmentSlotsClone() {
        return SLOTS;
    }
}
