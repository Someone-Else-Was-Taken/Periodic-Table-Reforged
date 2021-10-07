package me.jellysquid.mods.lithium.mixin.ai.pathing;

import me.jellysquid.mods.lithium.common.ai.pathing.PathNodeDefaults;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Blocks.class)
public class MixinBlocks {
    @Inject(method = "cacheBlockStates", at = @At("HEAD"))
    private static void startCacheBlockStates(CallbackInfo ci) {
        PathNodeDefaults.allowAccess();
    }

    @Inject(method = "cacheBlockStates", at = @At("RETURN"))
    private static void endCacheBlockStates(CallbackInfo ci) {
        PathNodeDefaults.disallowAccess();
    }
}
