package net.coderbot.iris.compat.physicsmod.mixin;

//import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;

// Run after the PhysicsMod injection
@Mixin(value = WorldRenderer.class, priority = 1001)
public class MixinLevelRenderer {
}
