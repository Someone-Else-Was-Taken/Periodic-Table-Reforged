package me.jellysquid.mods.sodium.client.render.pipeline;

import me.jellysquid.mods.sodium.client.model.light.LightMode;
import me.jellysquid.mods.sodium.client.model.light.LightPipeline;
import me.jellysquid.mods.sodium.client.model.light.LightPipelineProvider;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuad;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadViewMutable;
import me.jellysquid.mods.sodium.client.model.quad.blender.BiomeColorBlender;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadOrientation;
import me.jellysquid.mods.sodium.client.model.quad.sink.ModelQuadSinkDelegate;
import me.jellysquid.mods.sodium.client.render.occlusion.BlockOcclusionCache;
import me.jellysquid.mods.sodium.client.util.ModelQuadUtil;
import me.jellysquid.mods.sodium.client.util.color.ColorABGR;
import me.jellysquid.mods.sodium.client.util.rand.XoRoShiRoRandom;
import me.jellysquid.mods.sodium.client.world.biome.BlockColorsExtended;
import me.jellysquid.mods.sodium.common.util.DirectionUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.color.block.BlockColorProvider;
//import net.minecraft.client.render.model.BakedModel;
//import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.vector.Vector3d;
//import net.minecraft.world.BlockRenderView;
import net.minecraft.world.IBlockDisplayReader;

import java.util.List;
import java.util.Random;

public class BlockRenderer {
    private final Random random = new XoRoShiRoRandom();

    private final BlockColorsExtended blockColors;
    private final BlockOcclusionCache occlusionCache;

    private final ModelQuad cachedQuad = new ModelQuad();
    private final QuadLightData cachedQuadLightData = new QuadLightData();

    private final BiomeColorBlender biomeColorBlender;
    private final LightPipelineProvider lighters;

    private final boolean useAmbientOcclusion;

    public BlockRenderer(Minecraft client, LightPipelineProvider lighters, BiomeColorBlender biomeColorBlender) {
        this.blockColors = (BlockColorsExtended) client.getBlockColors();
        this.biomeColorBlender = biomeColorBlender;

        this.lighters = lighters;

        this.occlusionCache = new BlockOcclusionCache();
        this.useAmbientOcclusion = Minecraft.isAmbientOcclusionEnabled();
    }

    public boolean renderModel(IBlockDisplayReader world, BlockState state, BlockPos pos, IBakedModel model, ModelQuadSinkDelegate builder, boolean cull, long seed) {
        LightPipeline lighter = this.lighters.getLighter(this.getLightingMode(state, model));
        Vector3d offset = state.getOffset(world, pos);

        boolean rendered = false;

        for (Direction dir : DirectionUtil.ALL_DIRECTIONS) {
            this.random.setSeed(seed);

            List<BakedQuad> sided = model.getQuads(state, dir, this.random);

            if (sided.isEmpty()) {
                continue;
            }

            if (!cull || this.occlusionCache.shouldDrawSide(state, world, pos, dir)) {
                this.renderQuadList(world, state, pos, lighter, offset, builder, sided, ModelQuadFacing.fromDirection(dir));

                rendered = true;
            }
        }

        this.random.setSeed(seed);

        List<BakedQuad> all = model.getQuads(state, null, this.random);

        if (!all.isEmpty()) {
            this.renderQuadList(world, state, pos, lighter, offset, builder, all, ModelQuadFacing.NONE);

            rendered = true;
        }

        return rendered;
    }

    private void renderQuadList(IBlockDisplayReader world, BlockState state, BlockPos pos, LightPipeline lighter, Vector3d offset,
                                ModelQuadSinkDelegate builder, List<BakedQuad> quads, ModelQuadFacing facing) {
        IBlockColor colorizer = null;

        // This is a very hot allocation, iterate over it manually
        // noinspection ForLoopReplaceableByForEach
        for (int i = 0, quadsSize = quads.size(); i < quadsSize; i++) {
            BakedQuad quad = quads.get(i);

            QuadLightData light = this.cachedQuadLightData;
            lighter.calculate((ModelQuadView) quad, pos, light, quad.getFace(), quad.applyDiffuseLighting());

            if (quad.hasTintIndex() && colorizer == null) {
                colorizer = this.blockColors.getColorProvider(state);
            }

            this.renderQuad(world, state, pos, builder, offset, colorizer, quad, light, facing);
        }
    }

    private void renderQuad(IBlockDisplayReader world, BlockState state, BlockPos pos, ModelQuadSinkDelegate consumer, Vector3d offset,
                            IBlockColor colorProvider, BakedQuad bakedQuad, QuadLightData light, ModelQuadFacing facing) {
        ModelQuadView src = (ModelQuadView) bakedQuad;

        ModelQuadOrientation order = ModelQuadOrientation.orient(light.br);
        ModelQuadViewMutable copy = this.cachedQuad;

        int norm = ModelQuadUtil.getFacingNormal(bakedQuad.getFace());
        int[] colors = null;

        if (bakedQuad.hasTintIndex()) {
            colors = this.biomeColorBlender.getColors(colorProvider, world, state, pos, src);
        }

        for (int dstIndex = 0; dstIndex < 4; dstIndex++) {
            int srcIndex = order.getVertexIndex(dstIndex);

            copy.setX(dstIndex, src.getX(srcIndex) + (float) offset.getX());
            copy.setY(dstIndex, src.getY(srcIndex) + (float) offset.getY());
            copy.setZ(dstIndex, src.getZ(srcIndex) + (float) offset.getZ());
            copy.setColor(dstIndex, ColorABGR.mul(colors != null ? colors[srcIndex] : 0xFFFFFFFF, light.br[srcIndex]));
            copy.setTexU(dstIndex, src.getTexU(srcIndex));
            copy.setTexV(dstIndex, src.getTexV(srcIndex));
            copy.setLight(dstIndex, light.lm[srcIndex]);
            copy.setNormal(dstIndex, norm);
            copy.setSprite(src.getSprite());
        }

        consumer.get(facing)
                .write(copy);
    }

    private LightMode getLightingMode(BlockState state, IBakedModel model) {
        if (this.useAmbientOcclusion && model.isAmbientOcclusion() && state.getLightValue() == 0) {
            return LightMode.SMOOTH;
        } else {
            return LightMode.FLAT;
        }
    }
}
