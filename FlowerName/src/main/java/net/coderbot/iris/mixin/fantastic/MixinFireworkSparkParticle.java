package net.coderbot.iris.mixin.fantastic;

//import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleRenderType;
//import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
//import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.minecraft.client.particle.FireworkParticle$Spark")
public class MixinFireworkSparkParticle extends SimpleAnimatedParticle {
	private MixinFireworkSparkParticle(ClientWorld level, double x, double y, double z, IAnimatedSprite spriteProvider, float upwardsAcceleration) {
		super(level, x, y, z, spriteProvider, upwardsAcceleration);
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}
}
