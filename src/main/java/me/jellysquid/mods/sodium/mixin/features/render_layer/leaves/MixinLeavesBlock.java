package me.jellysquid.mods.sodium.mixin.features.render_layer.leaves;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import me.jellysquid.mods.sodium.client.gui.options.storage.MinecraftOptionsStorage;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.util.Direction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LeavesBlock.class)
public class MixinLeavesBlock extends Block {

    public MixinLeavesBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    private static final MinecraftOptionsStorage vanillaOpts = new MinecraftOptionsStorage();


    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (SodiumClientMod.options().quality.leavesQuality.isFancy(Minecraft.getInstance().gameSettings.graphicFanciness)) {
            return super.isSideInvisible(state, stateFrom, direction);
        } else {
            return stateFrom.getBlock() instanceof LeavesBlock || super.isSideInvisible(state, stateFrom, direction);
        }
    }
}