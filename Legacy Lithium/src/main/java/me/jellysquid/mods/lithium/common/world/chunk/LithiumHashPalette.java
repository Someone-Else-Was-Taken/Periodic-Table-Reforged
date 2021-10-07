package me.jellysquid.mods.lithium.common.world.chunk;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListNBT;
//import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketBuffer;
//import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ObjectIntIdentityMap;
//import net.minecraft.util.collection.IdList;
import net.minecraft.util.palette.IPalette;
import net.minecraft.util.palette.IResizeCallback;
//import net.minecraft.world.chunk.Palette;
//import net.minecraft.world.chunk.PaletteResizeListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

import static it.unimi.dsi.fastutil.Hash.FAST_LOAD_FACTOR;

/**
 * Generally provides better performance over the vanilla {@link net.minecraft.world.chunk.BiMapPalette} when calling
 * {@link LithiumHashPalette#getIndex(Object)} through using a faster backing map and reducing pointer chasing.
 */
public class LithiumHashPalette<T> implements IPalette<T> {
    private static final int ABSENT_VALUE = -1;

    private final ObjectIntIdentityMap<T> idList;
    private final IResizeCallback<T> resizeHandler;
    private final Function<CompoundNBT, T> elementDeserializer;
    private final Function<T, CompoundNBT> elementSerializer;
    private final int indexBits;

    private final Reference2IntMap<T> table;
    private T[] entries;
    private int size = 0;

    @SuppressWarnings("unchecked")
    public LithiumHashPalette(ObjectIntIdentityMap<T> ids, int bits, IResizeCallback<T> resizeHandler, Function<CompoundNBT, T> deserializer, Function<T, CompoundNBT> serializer) {
        this.idList = ids;
        this.indexBits = bits;
        this.resizeHandler = resizeHandler;
        this.elementDeserializer = deserializer;
        this.elementSerializer = serializer;

        int capacity = 1 << bits;

        this.entries = (T[]) new Object[capacity];
        this.table = new Reference2IntOpenHashMap<>(capacity, FAST_LOAD_FACTOR);
        this.table.defaultReturnValue(ABSENT_VALUE);
    }

    @Override
    public int idFor(T obj) {
        int id = this.table.getInt(obj);

        if (id == ABSENT_VALUE) {
            id = this.computeEntry(obj);
        }

        return id;
    }

    private int computeEntry(T obj) {
        int id = this.addEntry(obj);

        if (id >= 1 << this.indexBits) {
            if (this.resizeHandler == null) {
                throw new IllegalStateException("Cannot grow");
            } else {
                id = this.resizeHandler.onResize(this.indexBits + 1, obj);
            }
        }

        return id;
    }

    private int addEntry(T obj) {
        int nextId = this.size;

        if (nextId >= this.entries.length) {
            this.resize(this.size);
        }

        this.table.put(obj, nextId);
        this.entries[nextId] = obj;

        this.size++;

        return nextId;
    }

    private void resize(int neededCapacity) {
        this.entries = Arrays.copyOf(this.entries, HashCommon.nextPowerOfTwo(neededCapacity + 1));
    }

    @Override
    public boolean func_230341_a_(Predicate<T> predicate) {
        for (int i = 0; i < this.size; ++i) {
            if (predicate.test(this.entries[i])) {
                return true;
            }
        }

        return false;
    }

    @Override
    public T get(int id) {
        T[] entries = this.entries;

        if (id >= 0 && id < entries.length) {
            return entries[id];
        }

        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void read(PacketBuffer buf) {
        this.clear();

        int entryCount = buf.readVarInt();

        for (int i = 0; i < entryCount; ++i) {
            this.addEntry(this.idList.getByValue(buf.readVarInt()));
        }
    }

    @Override
    public void write(PacketBuffer buf) {
        int size = this.size;
        buf.writeVarInt(size);

        for (int i = 0; i < size; ++i) {
            buf.writeVarInt(this.idList.getId(this.get(i)));
        }
    }

    @Override
    public int getSerializedSize() {
        int size = PacketBuffer.getVarIntSize(this.size);

        for (int i = 0; i < this.size; ++i) {
            size += PacketBuffer.getVarIntSize(this.idList.getId(this.get(i)));
        }

        return size;
    }

    @Override
    public void read(ListNBT list) {
        this.clear();

        for (int i = 0; i < list.size(); ++i) {
            this.addEntry(this.elementDeserializer.apply(list.getCompound(i)));
        }
    }

    public void toTag(ListNBT list) {
        for (int i = 0; i < this.size; ++i) {
            list.add(this.elementSerializer.apply(this.get(i)));
        }
    }

    public int getSize() {
        return this.size;
    }

    private void clear() {
        Arrays.fill(this.entries, null);
        this.table.clear();
        this.size = 0;
    }
}
