package com.github.vini2003.linkart.mixin;

import java.util.List;

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

@Mixin({Entity.class})
public abstract class EntityMixin {
   @Shadow
	public World world;

	@Shadow
	public abstract Box getBoundingBox();

	@Shadow
	public abstract double getX();

	@Shadow
	public abstract double getY();

	@Shadow
	public abstract double getZ();

   @Inject(
      at = {@At("RETURN")},
      method = {"writeNbt(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/nbt/NbtCompound;"}
   )
   void onToTag(NbtCompound tag, CallbackInfoReturnable<NbtCompound> callbackInformationReturnable) {
      if ((Object) this instanceof AbstractMinecartEntity) {
         AbstractMinecartEntity entity = (AbstractMinecartEntity) (Object) this;
         AbstractMinecartEntityAccessor accessor = (AbstractMinecartEntityAccessor)entity;
         if (accessor.getNext() != null) {
            tag.putUuid("next", accessor.getNext().getUuid());
         }

         if (accessor.getPrevious() != null) {
            tag.putUuid("previous", accessor.getPrevious().getUuid());
         }
      }

   }

   @Inject(
      at = {@At("RETURN")},
      method = {"readNbt(Lnet/minecraft/nbt/NbtCompound;)V"}
   )
   void onFromTag(NbtCompound tag, CallbackInfo callbackInformation) {
      if ((Object) this instanceof AbstractMinecartEntity) {
         AbstractMinecartEntity entity = (AbstractMinecartEntity) (Object)this;
         AbstractMinecartEntityAccessor accessor = (AbstractMinecartEntityAccessor)entity;
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
		if ((Object) this instanceof AbstractMinecartEntity) {
			AbstractMinecartEntityAccessor accessor = (AbstractMinecartEntityAccessor) this;
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

   @Inject(at = @At("HEAD"), method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", cancellable = true)

   void onRecalculateVelocity(Vec3d movement, CallbackInfoReturnable<Vec3d> callbackInformationReturnable) {
		List<Entity> collisions = this.world.getOtherEntities((Entity) (Object) this, getBoundingBox().stretch(movement));

		for (Entity entity : collisions) {
			if (!CollisionUtils.shouldCollide((Entity) (Object) this, entity) && world.getBlockState(((AbstractMinecartEntity) (Object) this).getBlockPos()).getBlock() instanceof AbstractRailBlock) {
				callbackInformationReturnable.setReturnValue(movement);
				callbackInformationReturnable.cancel();
			}
		}
	}
}
