package me.jellysquid.mods.sodium.client.render.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.client.texture.Sprite;

public class SpriteUtil {
    public static void markSpriteActive(TextureAtlasSprite sprite) {
        if (sprite instanceof SpriteExtended) {
            ((SpriteExtended) sprite).markActive();
        }
    }
}
