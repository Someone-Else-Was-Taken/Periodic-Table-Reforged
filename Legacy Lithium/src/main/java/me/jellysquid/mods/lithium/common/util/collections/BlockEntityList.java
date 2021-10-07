package me.jellysquid.mods.lithium.common.util.collections;

import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.nbt.CompoundTag;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.*;

@SuppressWarnings("NullableProblems")
public class BlockEntityList implements List<TileEntity> {
    //BlockEntityList does not support double-add of the same object. But it does support multiple at the same position.
    //This collection behaves like a set with insertion order. It also provides a position->blockEntity lookup.

    private final ReferenceLinkedOpenHashSet<TileEntity> allBlockEntities;

    //When there is only 1 BlockEntity at a position, it is stored in posMap.
    //When there are multiple at a position, the first added is stored in posMap
    //and all of them are stored in posMapMulti using a List (in the order they were added)
    private final Long2ReferenceOpenHashMap<TileEntity> posMap;
    private final Long2ReferenceOpenHashMap<List<TileEntity>> posMapMulti;
    public BlockEntityList(List<TileEntity> list, boolean hasPositionLookup) {
        this.posMap = hasPositionLookup ? new Long2ReferenceOpenHashMap<>() : null;
        this.posMapMulti = hasPositionLookup ? new Long2ReferenceOpenHashMap<>() : null;

        if (this.posMap != null) {
            this.posMap.defaultReturnValue(null);
            this.posMapMulti.defaultReturnValue(null);
        }

        this.allBlockEntities = new ReferenceLinkedOpenHashSet<>();
        this.addAll(list);
    }

    @Override
    public int size() {
        return this.allBlockEntities.size();
    }

    @Override
    public boolean isEmpty() {
        return this.allBlockEntities.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.allBlockEntities.contains(o);
    }

    @Override
    public Iterator<TileEntity> iterator() {
        return this.allBlockEntities.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.allBlockEntities.toArray();
    }

    @Override
    @SuppressWarnings("SuspiciousToArrayCall")
    public <T> T[] toArray(T[] a) {
        return this.allBlockEntities.toArray(a);
    }

    @Override
    public boolean add(TileEntity blockEntity) {
        return this.addNoDoubleAdd(blockEntity, true);
    }

    private boolean addNoDoubleAdd(TileEntity blockEntity, boolean exceptionOnDoubleAdd) {
        boolean added = this.allBlockEntities.add(blockEntity);
        if (!added && exceptionOnDoubleAdd
                //Ignore double add when we encounter vanilla's command block double add bug
                && !( blockEntity instanceof CommandBlockTileEntity)) {
            this.throwException(blockEntity);
        }

        if (added && this.posMap != null) {
            long pos = getEntityPos(blockEntity);

            TileEntity prev = this.posMap.putIfAbsent(pos, blockEntity);
            if (prev != null) {
                List<TileEntity> multiEntry = this.posMapMulti.computeIfAbsent(pos, (long l) -> new ArrayList<>());
                if (multiEntry.size() == 0) {
                    //newly created multi entry: make sure it contains all elements
                    multiEntry.add(prev);
                }
                multiEntry.add(blockEntity);
            }
        }
        return added;
    }

    private void throwException(TileEntity blockEntity) {
        throw new IllegalStateException("Lithium BlockEntityList" + (this.posMap != null ? " with posMap" : "") + ": Adding the same BlockEntity object twice: " + blockEntity.write(new CompoundNBT()));
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof TileEntity) {
            TileEntity blockEntity = (TileEntity) o;
            if (this.allBlockEntities.remove(o)) {
                if (this.posMap != null) {
                    long pos = getEntityPos(blockEntity);
                    List<TileEntity> multiEntry = this.posMapMulti.get(pos);
                    if (multiEntry != null) {
                        multiEntry.remove(blockEntity);
                        if (multiEntry.size() <= 1) {
                            this.posMapMulti.remove(pos);
                        }
                    }
                    if (multiEntry != null && multiEntry.size() > 0) {
                        this.posMap.put(pos, multiEntry.get(0));
                    } else {
                        this.posMap.remove(pos);
                    }
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.allBlockEntities.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends TileEntity> c) {
        for (TileEntity blockEntity : c) {
            this.add(blockEntity);
        }

        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends TileEntity> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;

        for (Object obj : c) {
            modified |= this.remove(obj);
        }

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        for (TileEntity blockEntity : this.allBlockEntities) {
            if (!c.contains(blockEntity)) {
                modified |= this.remove(blockEntity);
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        this.allBlockEntities.clear();
        if (this.posMap != null) {
            this.posMap.clear();
            this.posMapMulti.clear();
        }
    }

    @Override
    public TileEntity get(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TileEntity set(int index, TileEntity element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, TileEntity element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TileEntity remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<TileEntity> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<TileEntity> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<TileEntity> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    private static long getEntityPos(TileEntity e) {
        return e.getPos().toLong();
    }


    public boolean addIfAbsent(TileEntity blockEntity) {
        //we are not checking position equality but object/reference equality (like vanilla)
        //the hashset prevents double add of the same object
        return this.addNoDoubleAdd(blockEntity, false);
    }

    @SuppressWarnings("unused")
    public boolean hasPositionLookup() {
        return this.posMap != null;
    }

    //Methods only supported when posMap is present!
    public void markRemovedAndRemoveAllAtPosition(BlockPos blockPos) {
        long pos = blockPos.toLong();
        TileEntity blockEntity = this.posMap.remove(pos);
        if (blockEntity != null) {
            List<TileEntity> multiEntry = this.posMapMulti.remove(pos);
            if (multiEntry != null) {
                for (TileEntity blockEntity1 : multiEntry) {
                    blockEntity1.remove();
                    this.allBlockEntities.remove(blockEntity1);
                }
            } else {
                blockEntity.remove();
                this.allBlockEntities.remove(blockEntity);
            }
        }
    }

    public TileEntity getFirstNonRemovedBlockEntityAtPosition(long pos) {
        if (this.isEmpty()) {
            return null;
        }
        TileEntity blockEntity = this.posMap.get(pos);
        //usual case: we find no BlockEntity or only one that also is not removed
        if (blockEntity == null || !blockEntity.isRemoved()) {
            return blockEntity;
        }
        //vanilla edge case: two BlockEntities at the same position
        //Look up in the posMultiMap to find the first non-removed BlockEntity
        List<TileEntity> multiEntry = this.posMapMulti.get(pos);
        if (multiEntry != null) {
            for (TileEntity blockEntity1 : multiEntry) {
                if (!blockEntity1.isRemoved()) {
                    return blockEntity1;
                }
            }
        }
        return null;
    }
}
