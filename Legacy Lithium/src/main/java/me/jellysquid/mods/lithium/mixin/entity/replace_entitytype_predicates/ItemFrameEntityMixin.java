package me.jellysquid.mods.lithium.mixin.entity.replace_entitytype_predicates;

import me.jellysquid.mods.lithium.common.world.WorldHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
//import net.minecraft.entity.decoration.AbstractDecorationEntity;
//import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.util.math.AxisAlignedBB;
//import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Predicate;

@Mixin(ItemFrameEntity.class)
public abstract class ItemFrameEntityMixin extends HangingEntity {
    protected ItemFrameEntityMixin(EntityType<? extends HangingEntity> entityType_1, World world_1) {
        super(entityType_1, world_1);
    }

    @Redirect(
            method = "onValidSurface",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/function/Predicate;)Ljava/util/List;"
            )
    )
    private List<Entity> getAbstractDecorationEntities(World world, Entity excluded, AxisAlignedBB box, Predicate<? super Entity> predicate) {
        if (predicate == IS_HANGING_ENTITY) {
            return WorldHelper.getEntitiesOfClass(world, excluded, HangingEntity.class, box);
        }

        return world.getEntitiesInAABBexcluding(excluded, box, predicate);
    }
}
