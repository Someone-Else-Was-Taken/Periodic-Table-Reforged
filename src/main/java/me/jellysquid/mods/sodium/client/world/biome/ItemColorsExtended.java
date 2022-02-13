package me.jellysquid.mods.sodium.client.world.biome;

//import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

public interface ItemColorsExtended {
    IItemColor getColorProvider(ItemStack stack);
}
