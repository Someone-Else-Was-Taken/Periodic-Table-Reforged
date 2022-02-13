package me.jellysquid.mods.sodium.mixin.core.model;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.jellysquid.mods.sodium.client.world.biome.ItemColorsExtended;
//import net.minecraft.client.color.item.ItemColorProvider;
//import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
//import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.registries.IRegistryDelegate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemColors.class)
public class MixinItemColors implements ItemColorsExtended {
    private Reference2ReferenceMap<IRegistryDelegate<Item>, IItemColor> itemsToColor;

    private static final IItemColor DEFAULT_PROVIDER = (stack, tintIdx) -> -1;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        this.itemsToColor = new Reference2ReferenceOpenHashMap<>();
        this.itemsToColor.defaultReturnValue(DEFAULT_PROVIDER);
    }

    @Inject(method = "register", at = @At("HEAD"))
    private void preRegisterColor(IItemColor mapper, IItemProvider[] convertibles, CallbackInfo ci) {
        for (IItemProvider convertible : convertibles) {
            this.itemsToColor.put(convertible.asItem().delegate, mapper);
        }
    }

    @Override
    public IItemColor getColorProvider(ItemStack stack) {
        return this.itemsToColor.get(stack.getItem().delegate);
    }
}