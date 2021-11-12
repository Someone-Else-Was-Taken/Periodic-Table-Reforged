package me.jellysquid.mods.hydrogen.mixin.client.model.json;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.jellysquid.mods.hydrogen.common.collections.CollectionHelper;
//import net.minecraft.client.render.model.BakedModel;
//import net.minecraft.client.render.model.json.ModelOverride;
//import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.client.renderer.model.ItemOverrideList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemOverrideList.class)
public class MixinModelOverrideList {
    @Mutable
    @Shadow
    @Final
    private List<ItemOverride> overrides;

    @Mutable
    @Shadow
    @Final
    private List<IBakedModel> overrideBakedModels;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void reinit(CallbackInfo ci) {
        this.overrides = CollectionHelper.fixed(this.overrides);
        this.overrideBakedModels = CollectionHelper.fixed(this.overrideBakedModels);
    }
}
