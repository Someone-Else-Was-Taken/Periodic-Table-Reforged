package me.jellysquid.mods.lithium.mixin.chunk.section_update_tracking;

import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
//import net.minecraft.server.world.ChunkHolder;
import net.minecraft.world.server.ChunkHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChunkHolder.class)
public class ChunkHolderMixin {

    @Shadow
    @Final
    private ShortSet[] field_244383_q;

    @Shadow
    private boolean field_244382_p;

    /**
     * Using Hashsets instead of ArraySets for better worst-case performance
     * The default case of just a few items may be very slightly slower
     */
    @ModifyVariable(
            method = "func_244386_a",
            at = @At(
                    ordinal = 0,
                    value = "FIELD",
                    target = "Lnet/minecraft/server/world/ChunkHolder;blockUpdatesBySection:[Lit/unimi/dsi/fastutil/shorts/ShortSet;",
                    shift = At.Shift.BEFORE
            )
    )
    private byte createShortHashSet(byte b) {
        if (field_244383_q[b] == null) {
            this.field_244382_p = true;
            this.field_244383_q[b] = new ShortOpenHashSet();
        }
        return b;
    }
}
