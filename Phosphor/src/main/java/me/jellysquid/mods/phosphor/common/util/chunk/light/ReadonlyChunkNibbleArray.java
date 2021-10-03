package me.jellysquid.mods.phosphor.common.util.chunk.light;

import me.jellysquid.mods.phosphor.common.chunk.light.IReadonly;
//import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.NibbleArray;

public class ReadonlyChunkNibbleArray extends NibbleArray implements IReadonly {
    public ReadonlyChunkNibbleArray() {
    }

    public ReadonlyChunkNibbleArray(byte[] bs) {
        super(bs);
    }

    @Override
    public NibbleArray copy() {
        return new NibbleArray(this.getData());
    }

    @Override
    public boolean isReadonly() {
        return true;
    }
}
