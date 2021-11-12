package me.jellysquid.mods.hydrogen.mixin.nbt;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.INBT;
//import net.minecraft.nbt.Tag;
import net.minecraft.tags.ITag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(CompoundNBT.class)
public class MixinCompoundTag {
    @Mutable
    @Shadow
    @Final
    private Map<String, INBT> tagMap;

    @Inject(method = "<init>(Ljava/util/Map;)V", at = @At("RETURN"))
    private void reinit(Map<String, INBT> tags, CallbackInfo ci) {
        this.tagMap = tags instanceof Object2ObjectMap ? tags : new Object2ObjectOpenHashMap<>(tags);
    }
}
