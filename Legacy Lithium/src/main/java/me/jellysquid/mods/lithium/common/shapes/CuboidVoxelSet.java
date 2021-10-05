package me.jellysquid.mods.lithium.common.shapes;

//import net.minecraft.util.math.Direction;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShapePart;
//import net.minecraft.util.shape.VoxelSet;

public class CuboidVoxelSet extends VoxelShapePart {
    private final int minX, minY, minZ, maxX, maxY, maxZ;

    protected CuboidVoxelSet(int xSize, int ySize, int zSize, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        super(xSize, ySize, zSize);

        this.minX = (int) Math.round(minX * xSize);
        this.maxX = (int) Math.round(maxX * xSize);
        this.minY = (int) Math.round(minY * ySize);
        this.maxY = (int) Math.round(maxY * ySize);
        this.minZ = (int) Math.round(minZ * zSize);
        this.maxZ = (int) Math.round(maxZ * zSize);
    }

    @Override
    public boolean isFilled(int x, int y, int z) {
        return x >= this.minX && x < this.maxX &&
                y >= this.minY && y < this.maxY &&
                z >= this.minZ && z < this.maxZ;
    }

    @Override
    public void setFilled(int x, int y, int z, boolean resize, boolean included) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getStart(Direction.Axis axis) {
        return axis.getCoordinate(this.minX, this.minY, this.minZ);
    }

    @Override
    public int getEnd(Direction.Axis axis) {
        return axis.getCoordinate(this.maxX, this.maxY, this.maxZ);
    }


    @Override
    public boolean isEmpty() {
        return this.minX >= this.maxX || this.minY >= this.maxY || this.minZ >= this.maxZ;
    }

    @Override
    protected boolean isZAxisLineFull(int minZ, int maxZ, int x, int y) {
        return x >= this.minX && x < this.maxX &&
                y >= this.minY && y < this.maxY &&
                minZ >= this.minZ && maxZ <= this.maxZ; // arg maxZ is exclusive
    }

    @Override
    protected void setZAxisLine(int minZ, int maxZ, int x, int y, boolean included) {
        throw new UnsupportedOperationException();
    }
}
