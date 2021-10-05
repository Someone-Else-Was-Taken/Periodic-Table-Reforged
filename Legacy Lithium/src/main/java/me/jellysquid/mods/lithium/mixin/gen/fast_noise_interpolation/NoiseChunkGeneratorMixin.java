package me.jellysquid.mods.lithium.mixin.gen.fast_noise_interpolation;

import net.minecraft.util.math.MathHelper;
//import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
//import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.gen.ImprovedNoiseGenerator;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.OctavesNoiseGenerator;
//import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin {
    @Shadow
    @Final
    private OctavesNoiseGenerator field_222568_o;

    @Shadow
    @Final
    private OctavesNoiseGenerator field_222569_p;

    @Shadow
    @Final
    private OctavesNoiseGenerator field_222570_q;

    /**
     * @reason Smarter use of perlin noise that avoids unneeded sampling.
     * @author SuperCoder79
     */
    @Overwrite
    private double func_222552_a(int x, int y, int z, double horizontalScale, double verticalScale, double horizontalStretch, double verticalStretch) {
        // To generate it's terrain, Minecraft uses two different perlin noises.
        // It interpolates these two noises to create the final sample at a position.
        // However, the interpolation noise is not all that good and spends most of it's time at > 1 or < 0, rendering
        // one of the noises completely unnecessary in the process.
        // By taking advantage of that, we can reduce the sampling needed per block through the interpolation noise.

        // This controls both the frequency and amplitude of the noise.
        double frequency = 1.0;
        double interpolationValue = 0.0;

        // Calculate interpolation data to decide what noise to sample.
        for (int octave = 0; octave < 8; octave++) {
            double scaledVerticalScale = verticalStretch * frequency;
            double scaledY = y * scaledVerticalScale;

            interpolationValue += sampleOctave(this.field_222570_q.getOctave(octave),
                    OctavesNoiseGenerator.maintainPrecision(x * horizontalStretch * frequency),
                    OctavesNoiseGenerator.maintainPrecision(scaledY),
                    OctavesNoiseGenerator.maintainPrecision(z * horizontalStretch * frequency), scaledVerticalScale, scaledY, frequency);

            frequency /= 2.0;
        }

        double clampedInterpolation = (interpolationValue / 10.0 + 1.0) / 2.0;

        if (clampedInterpolation >= 1) {
            // Sample only upper noise, as the lower noise will be interpolated out.
            frequency = 1.0;
            double noise = 0.0;
            for (int octave = 0; octave < 16; octave++) {
                double scaledVerticalScale = verticalScale * frequency;
                double scaledY = y * scaledVerticalScale;

                noise += sampleOctave(this.field_222569_p.getOctave(octave),
                        OctavesNoiseGenerator.maintainPrecision(x * horizontalScale * frequency),
                        OctavesNoiseGenerator.maintainPrecision(scaledY),
                        OctavesNoiseGenerator.maintainPrecision(z * horizontalScale * frequency), scaledVerticalScale, scaledY, frequency);

                frequency /= 2.0;
            }

            return noise / 512.0;
        } else if (clampedInterpolation <= 0) {
            // Sample only lower noise, as the upper noise will be interpolated out.
            frequency = 1.0;
            double noise = 0.0;
            for (int octave = 0; octave < 16; octave++) {
                double scaledVerticalScale = verticalScale * frequency;
                double scaledY = y * scaledVerticalScale;
                noise += sampleOctave(this.field_222568_o.getOctave(octave),
                        OctavesNoiseGenerator.maintainPrecision(x * horizontalScale * frequency),
                        OctavesNoiseGenerator.maintainPrecision(scaledY),
                        OctavesNoiseGenerator.maintainPrecision(z * horizontalScale * frequency), scaledVerticalScale, scaledY, frequency);

                frequency /= 2.0;
            }

            return noise / 512.0;
        } else {
            // [VanillaCopy] SurfaceChunkGenerator#sampleNoise
            // Sample both and interpolate, as in vanilla.

            frequency = 1.0;
            double lowerNoise = 0.0;
            double upperNoise = 0.0;

            for (int octave = 0; octave < 16; octave++) {
                // Pre calculate these values to share them
                double scaledVerticalScale = verticalScale * frequency;
                double scaledY = y * scaledVerticalScale;
                double xVal = OctavesNoiseGenerator.maintainPrecision(x * horizontalScale * frequency);
                double yVal = OctavesNoiseGenerator.maintainPrecision(scaledY);
                double zVal = OctavesNoiseGenerator.maintainPrecision(z * horizontalScale * frequency);

                upperNoise += sampleOctave(this.field_222569_p.getOctave(octave), xVal, yVal, zVal, scaledVerticalScale, scaledY, frequency);
                lowerNoise += sampleOctave(this.field_222568_o.getOctave(octave), xVal, yVal, zVal, scaledVerticalScale, scaledY, frequency);

                frequency /= 2.0;
            }

            // Vanilla behavior, return interpolated noise
            return MathHelper.lerp(clampedInterpolation, lowerNoise / 512.0, upperNoise / 512.0);
        }
    }

    private static double sampleOctave(ImprovedNoiseGenerator sampler, double x, double y, double z, double scaledVerticalScale, double scaledY, double frequency) {
        return sampler.func_215456_a(x, y, z, scaledVerticalScale, scaledY) / frequency;
    }
}