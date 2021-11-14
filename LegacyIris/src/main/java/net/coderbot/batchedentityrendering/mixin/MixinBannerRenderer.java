package net.coderbot.batchedentityrendering.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
//import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.coderbot.batchedentityrendering.impl.Groupable;
import net.coderbot.batchedentityrendering.impl.wrappers.TaggingRenderTypeWrapper;
//import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.IRenderTypeBuffer;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.BannerTileEntityRenderer;
//import net.minecraft.client.resources.model.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BannerPattern;
//import net.minecraft.world.item.DyeColor;
//import net.minecraft.world.level.block.entity.BannerPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BannerTileEntityRenderer.class)
public class MixinBannerRenderer {
    private static final String RENDER_PATTERNS =
            "Lnet/minecraft/client/renderer/tileentity/BannerTileEntityRenderer;renderPatterns(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/ModelRenderer;Lnet/minecraft/client/renderer/model/RenderMaterial;ZLjava/util/List;Z)V";

    /**
     * Holds a Groupable instance, if we successfully started a group.
     * This is because we need to make sure to end the group that we started.
     */
    @Unique
    private static Groupable groupableToEnd;
    private static int index;

    @ModifyVariable(method = "render", at = @At("HEAD"))
    private static IRenderTypeBuffer iris$wrapBufferSource(IRenderTypeBuffer multiBufferSource) {
        if (multiBufferSource instanceof Groupable) {
            Groupable groupable = (Groupable) multiBufferSource;
            boolean started = groupable.maybeStartGroup();

            if (started) {
                groupableToEnd = groupable;
            }

			index = 0;
			// NB: Groupable not needed for this implementation of MultiBufferSource.
			return type -> multiBufferSource.getBuffer(new TaggingRenderTypeWrapper(type.toString(), type, index++));
        }

        return multiBufferSource;
    }

    @Inject(method = RENDER_PATTERNS, at = @At("RETURN"))
    private static void iris$endRenderingCanvas(MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int i, int j, ModelRenderer modelPart, RenderMaterial material, boolean bl, List<Pair<BannerPattern, DyeColor>> list, boolean bl2, CallbackInfo ci) {
        if (groupableToEnd != null) {
            groupableToEnd.endGroup();
            groupableToEnd = null;
			index = 0;
        }
    }
}
