package me.jellysquid.mods.hydrogen.mixin.client.model.json;

import com.google.common.collect.Streams;
import me.jellysquid.mods.hydrogen.common.state.StatePropertyPredicateHelper;
import me.jellysquid.mods.hydrogen.common.util.AnyPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
//import net.minecraft.client.render.model.json.MultipartModelSelector;
//import net.minecraft.client.render.model.json.OrMultipartModelSelector;
//import net.minecraft.state.StateManager;
import net.minecraft.client.renderer.model.multipart.ICondition;
import net.minecraft.client.renderer.model.multipart.OrCondition;
import net.minecraft.state.StateContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(OrCondition.class)
public class MixinOrMultipartModelSelector {
    @Shadow @Final private Iterable<? extends ICondition> conditions;

    /**
     * @author JellySquid
     * @reason Flatten predicates
     */
    @Overwrite
    public Predicate<BlockState> getPredicate(StateContainer<Block, BlockState> stateManager) {
        return StatePropertyPredicateHelper.anyMatch(Streams.stream(this.conditions).map((multipartModelSelector) -> {
            return multipartModelSelector.getPredicate(stateManager);
        }).collect(Collectors.toList()));
    }
}
