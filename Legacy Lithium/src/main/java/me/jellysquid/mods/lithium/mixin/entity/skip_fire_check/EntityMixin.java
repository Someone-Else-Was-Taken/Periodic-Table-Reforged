package me.jellysquid.mods.lithium.mixin.entity.skip_fire_check;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
//import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.stream.Stream;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    private int fire;

    @Shadow
    protected abstract int getFireImmuneTicks();

    @Redirect(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;method_29556(Lnet/minecraft/util/math/Box;)Ljava/util/stream/Stream;"
            )
    )
    private Stream<BlockState> skipFireTestIfResultDoesNotMatter(World world, AxisAlignedBB box) {
        // Skip scanning the blocks around the entity touches by returning an empty stream when the result does not matter
        if (this.fire > 0 || this.fire == -this.getFireImmuneTicks()) {
            return Stream.empty();
        }

        return world.getStatesInArea(box);
    }
}
