package me.jellysquid.mods.lithium.mixin.alloc.explosion_behavior;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.BlockView;
import net.minecraft.world.EntityExplosionContext;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.IBlockReader;
//import net.minecraft.world.explosion.EntityExplosionBehavior;
//import net.minecraft.world.explosion.Explosion;
//import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(EntityExplosionContext.class)
public class EntityExplosionBehaviorMixin extends ExplosionContext {
    @Shadow
    @Final
    private Entity entity;

    /**
     * @author 2No2Name
     * @reason avoid lambda and optional allocation
     */
    @Overwrite
    public Optional<Float> getExplosionResistance(Explosion explosion, IBlockReader world, BlockPos pos, BlockState blockState, FluidState fluidState) {
        Optional<Float> optionalBlastResistance = super.getExplosionResistance(explosion, world, pos, blockState, fluidState);
        if (optionalBlastResistance.isPresent()) {
            float blastResistance = optionalBlastResistance.get();
            float effectiveExplosionResistance = this.entity.getExplosionResistance(explosion, world, pos, blockState, fluidState, blastResistance);
            if (effectiveExplosionResistance != blastResistance) {
                return Optional.of(effectiveExplosionResistance);
            }
        }
        return optionalBlastResistance;
    }
}
