package me.jellysquid.mods.lithium.common.util.math;

//import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.vector.Vector3d;

public class MutableVec3d {
    private double x, y, z;

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public void add(Vector3d vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
    }

    public Vector3d toImmutable() {
        return new Vector3d(this.x, this.y, this.z);
    }
}

