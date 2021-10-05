package me.jellysquid.mods.lithium.mixin.entity.replace_entitytype_predicates;

import net.minecraft.entity.Entity;
//import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
//import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.AxisAlignedBB;
//import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Predicate;

@Mixin(ArmorStandEntity.class)
public class ArmorStandEntityMixin {
    @Shadow
    @Final
    private static Predicate<Entity> IS_RIDEABLE_MINECART;

    @Redirect(
            method = "collideWithNearbyEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/function/Predicate;)Ljava/util/List;"
            )
    )
    private List<Entity> getMinecartsDirectly(World world, Entity excluded, AxisAlignedBB box, Predicate<? super Entity> predicate) {
        if (predicate == IS_RIDEABLE_MINECART) {
            // Not using MinecartEntity.class and no predicate, because mods may add another minecart that is type ridable without being MinecartEntity
            return world.getEntitiesWithinAABB(AbstractMinecartEntity.class, box, (Entity e) -> e != excluded && ((AbstractMinecartEntity) e).getMinecartType() == AbstractMinecartEntity.Type.RIDEABLE);
        }

        return world.getEntitiesInAABBexcluding(excluded, box, predicate);
    }
}
