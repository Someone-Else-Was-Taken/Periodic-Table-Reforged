package me.jellysquid.mods.hydrogen.mixin.state;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import com.mojang.serialization.MapCodec;
import me.jellysquid.mods.hydrogen.common.cache.StatePropertyTableCache;
import me.jellysquid.mods.hydrogen.common.collections.FastImmutableTable;
import me.jellysquid.mods.hydrogen.common.jvm.ClassConstructors;
import net.minecraft.state.Property;
//import net.minecraft.state.State;
import net.minecraft.state.StateHolder;
//import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(StateHolder.class)
public class MixinState<O, S> {
    @Mutable
    @Shadow
    @Final
    private ImmutableMap<Property<?>, Comparable<?>> properties;

    @Shadow
    private Table<Property<?>, Comparable<?>, S> propertyToStateTable;

    @Shadow
    @Final
    protected O instance;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void reinit(O owner, ImmutableMap<Property<?>, Comparable<?>> entries, MapCodec<S> mapCodec, CallbackInfo ci) {
        this.properties = ClassConstructors.createFastImmutableMap(this.properties);
    }

    @Inject(method = "func_235899_a_", at = @At("RETURN"))
    private void postCreateWithTable(Map<Map<Property<?>, Comparable<?>>, S> states, CallbackInfo ci) {
        this.propertyToStateTable = new FastImmutableTable<>(this.propertyToStateTable, StatePropertyTableCache.getTableCache(this.instance));
    }
}
