package me.jellysquid.mods.sodium.mixin.features.gui;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MixinMinecraftAccessor {
    @Accessor("debugFPS")
    abstract int getDebugFPS();
}