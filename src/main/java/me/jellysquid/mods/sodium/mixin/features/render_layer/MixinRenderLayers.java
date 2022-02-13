package me.jellysquid.mods.sodium.mixin.features.render_layer;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.minecraft.block.Block;
//import net.minecraft.client.render.RenderLayer;
//import net.minecraft.client.render.RenderLayers;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.fluid.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(RenderTypeLookup.class)
public class MixinRenderLayers {
    @Mutable
    @Shadow
    @Final
    private static Map<Block, RenderType> TYPES_BY_BLOCK;

    @Mutable
    @Shadow
    @Final
    private static Map<Fluid, RenderType> TYPES_BY_FLUID;

    static {
        // Replace the backing collection types with something a bit faster, since this is a hot spot in chunk rendering.
        TYPES_BY_BLOCK = new Reference2ReferenceOpenHashMap<>(TYPES_BY_BLOCK);
        TYPES_BY_FLUID = new Reference2ReferenceOpenHashMap<>(TYPES_BY_FLUID);
    }
    @Inject(method = "getChunkRenderType", at = @At(value = "RETURN"), cancellable = true)
    private static void redirectLeavesGraphics(BlockState state, CallbackInfoReturnable<RenderType> cir) {
        if (state.getBlock() instanceof LeavesBlock) {
            boolean fancyLeaves = SodiumClientMod.options().quality.leavesQuality.isFancy(Minecraft.getInstance().gameSettings.graphicFanciness);
            cir.setReturnValue(fancyLeaves ? RenderType.getCutoutMipped() : RenderType.getSolid());
        }
    }
}
