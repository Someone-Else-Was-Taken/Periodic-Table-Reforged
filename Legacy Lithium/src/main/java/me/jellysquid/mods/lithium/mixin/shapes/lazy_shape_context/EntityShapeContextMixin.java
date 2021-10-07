package me.jellysquid.mods.lithium.mixin.shapes.lazy_shape_context;

//import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
//import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(EntitySelectionContext.class)
public class EntityShapeContextMixin {
    @Mutable
    @Shadow
    @Final
    private Item item;
    @Mutable
    @Shadow
    @Final
    private Predicate<Fluid> fluidPredicate;

    private Entity lithium_entity;

    /**
     * Mixin the instanceof to always return false to avoid the expensive inventory access.
     * No need to use Opcodes.INSTANCEOF or similar.
     */
    @ModifyConstant(
            method = "<init>(Lnet/minecraft/entity/Entity;)V",
            constant = @Constant(classValue = LivingEntity.class, ordinal = 0)
    )
    private static boolean redirectInstanceOf(Object obj, Class<?> clazz) {
        return false;
    }

    @ModifyConstant(
            method = "<init>(Lnet/minecraft/entity/Entity;)V",
            constant = @Constant(classValue = LivingEntity.class, ordinal = 2)
    )
    private static boolean redirectInstanceOf2(Object obj, Class<?> clazz) {
        return false;
    }

    @Inject(
            method = "<init>(Lnet/minecraft/entity/Entity;)V",
            at = @At("RETURN")
    )
    private void initFields(Entity entity, CallbackInfo ci) {
        this.item = null;
        this.fluidPredicate = null;
        this.lithium_entity = entity;
    }

    /**
     * @author 2No2Name
     * @reason allow skipping unused initialization
     */
    @Overwrite
    public boolean hasItem(Item item) {
        if (this.item == null) {
            this.item = this.lithium_entity instanceof LivingEntity ? ((LivingEntity)this.lithium_entity).getHeldItemMainhand().getItem() : Items.AIR;
        }
        return this.item == item;
    }

    /**
     * @author 2No2Name
     * @reason allow skipping unused lambda allocation
     */
    @Overwrite
    public boolean func_230426_a_(FluidState aboveState, FlowingFluid fluid) {
        return this.lithium_entity instanceof LivingEntity && ((LivingEntity) this.lithium_entity).func_230285_a_(fluid) && !aboveState.getFluid().isEquivalentTo(fluid);
    }
}
