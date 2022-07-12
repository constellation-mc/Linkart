package com.github.vini2003.linkart.mixin;

import com.github.vini2003.linkart.accessor.AbstractMinecartEntityAccessor;
import com.github.vini2003.linkart.utility.CollisionUtils;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin({Entity.class})
public abstract class EntityMixin {
    @Shadow
    public World world;

    @Shadow
    public abstract Box getBoundingBox();

    @Inject(at = @At("RETURN"), method = "writeNbt")
    void onToTag(NbtCompound tag, CallbackInfoReturnable<NbtCompound> callbackInformationReturnable) {
        Entity entity = ((Entity) (Object) this);
        if (entity instanceof AbstractMinecartEntity abstractMinecartEntity) {
            AbstractMinecartEntityAccessor accessor = (AbstractMinecartEntityAccessor) abstractMinecartEntity;
            if (accessor.getNext() != null) {
                tag.putUuid("next", accessor.getNext().getUuid());
            }

            if (accessor.getPrevious() != null) {
                tag.putUuid("previous", accessor.getPrevious().getUuid());
            }
        }

    }

    @Inject(at = @At("HEAD"), method = "readNbt")
    void onFromTag(NbtCompound tag, CallbackInfo callbackInformation) {
        Entity entity = ((Entity) (Object) this);
        if (entity instanceof AbstractMinecartEntity abstractMinecartEntity) {
            AbstractMinecartEntityAccessor accessor = (AbstractMinecartEntityAccessor) abstractMinecartEntity;
            if (tag.containsUuid("next")) {
                accessor.setNextUuid(tag.getUuid("next"));
            }

            if (tag.containsUuid("previous")) {
                accessor.setPreviousUuid(tag.getUuid("previous"));
            }
        }

    }

    @Inject(at = @At("HEAD"), method = "remove")
    void removeLink(CallbackInfo callbackInformation) {
        Entity entity = ((Entity) (Object) this);
        if (entity instanceof AbstractMinecartEntity) {
            AbstractMinecartEntityAccessor accessor = (AbstractMinecartEntityAccessor) entity;
            AbstractMinecartEntityAccessor next = (AbstractMinecartEntityAccessor) accessor.getNext();
            AbstractMinecartEntityAccessor previous = (AbstractMinecartEntityAccessor) accessor.getPrevious();

            if (next != null) {
                next.setPrevious(null);
            }

            if (previous != null) {
                previous.setNext(null);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "adjustMovementForCollisions*", cancellable = true)
    void onRecalculateVelocity(Vec3d movement, CallbackInfoReturnable<Vec3d> callbackInformationReturnable) {
        List<Entity> collisions = this.world.getOtherEntities((Entity) (Object) this, getBoundingBox().stretch(movement));

        for (Entity entity : collisions) {
            if (!CollisionUtils.shouldCollide((Entity) (Object) this, entity) && world.getBlockState(((Entity) (Object) this).getBlockPos()).getBlock() instanceof AbstractRailBlock) {
                callbackInformationReturnable.setReturnValue(movement);
                callbackInformationReturnable.cancel();
            }
        }
    }
}
