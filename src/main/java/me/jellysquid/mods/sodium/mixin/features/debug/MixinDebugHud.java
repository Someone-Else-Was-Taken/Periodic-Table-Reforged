package me.jellysquid.mods.sodium.mixin.features.debug;

import com.google.common.collect.Lists;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderBackend;
import net.minecraft.client.gui.overlay.DebugOverlayGui;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

@Mixin(DebugOverlayGui.class)
public abstract class MixinDebugHud {
    @Shadow
    private static long bytesToMb(long bytes) {
        throw new UnsupportedOperationException();
    }

    /*@Inject(method = "getDebugInfoRight", at = @At("RETURN"))
    private void appendRightText(CallbackInfoReturnable<List<String>> cir) {
        List<String> strings = cir.getReturnValue();*/
    @Redirect(method = "getDebugInfoRight", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList([Ljava/lang/Object;)Ljava/util/ArrayList;"))
    private ArrayList<String> redirectRightTextEarly(Object[] elements) {
        ArrayList<String> strings = Lists.newArrayList((String[]) elements);
        strings.add("");
        strings.add("Magnesium Renderer");
        strings.add(TextFormatting.UNDERLINE + getFormattedVersionText());
        strings.add("");

        if (SodiumClientMod.options().advanced.ignoreDriverBlacklist) {
            strings.add(TextFormatting.RED + "(!!) Driver blacklist ignored");
        }

        for (int i = 0; i < strings.size(); i++) {
            String str = strings.get(i);

            if (str.startsWith("Allocated:")) {
                strings.add(i + 1, getNativeMemoryString());

                break;
            }
        }

        return strings;
    }

    private static String getFormattedVersionText() {
        String version = SodiumClientMod.getVersion();
        TextFormatting color;

        if (version.endsWith("-dirty")) {
            color = TextFormatting.RED;
        } else if (version.contains("+rev.")) {
            color = TextFormatting.LIGHT_PURPLE;
        } else {
            color = TextFormatting.GREEN;
        }

        return color + version;
    }



    private static String getNativeMemoryString() {
        return "Off-Heap: +" + bytesToMb(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed()) + "MB";
    }
}
