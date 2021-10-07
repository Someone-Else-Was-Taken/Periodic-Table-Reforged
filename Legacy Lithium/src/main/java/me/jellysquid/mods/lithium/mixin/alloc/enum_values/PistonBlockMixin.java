package me.jellysquid.mods.lithium.mixin.alloc.enum_values;

import net.minecraft.block.PistonBlock;
import net.minecraft.util.Direction;
//import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonBlock.class)
public class PistonBlockMixin {
    private static final Direction[] DIRECTIONS = Direction.values();

    @Redirect(
            method = "shouldBeExtended",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Direction;values()[Lnet/minecraft/util/Direction;"
            )
    )
    private Direction[] redirectShouldExtendDirectionValues() {
        return DIRECTIONS;
    }
}
