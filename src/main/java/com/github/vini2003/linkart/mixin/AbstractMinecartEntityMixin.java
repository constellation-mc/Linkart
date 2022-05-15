package com.github.vini2003.linkart.mixin;

import java.util.UUID;

import com.github.vini2003.linkart.accessor.AbstractMinecartEntityAccessor;
import com.github.vini2003.linkart.utility.CollisionUtils;
import com.github.vini2003.linkart.utility.RailUtils;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin({AbstractMinecartEntity.class})
public abstract class AbstractMinecartEntityMixin implements AbstractMinecartEntityAccessor {
   @Unique
   UUID nextUuid;
   @Unique
   private AbstractMinecartEntity previous;
   @Unique
   private AbstractMinecartEntity next;
   @Unique
   private UUID previousUuid;
   @Override
   public AbstractMinecartEntity getPrevious() {
      if (this.previous == null && this.getPreviousUuid() != null && !((AbstractMinecartEntity)(Object)this).world.isClient) {
         this.previous = (AbstractMinecartEntity)((ServerWorld)((AbstractMinecartEntity)(Object)this).world).getEntity(this.getPreviousUuid());
      }

      return this.previous;
   }

   @Override
   public void setPrevious(AbstractMinecartEntity previous) {
      this.previous = previous;
      this.nextUuid = previous == null ? null : previous.getUuid();
   }

   @Override
   public AbstractMinecartEntity getNext() {
      if (this.next == null && this.getNextUuid() != null && !((AbstractMinecartEntity)(Object)this).world.isClient) {
         this.next = (AbstractMinecartEntity)((ServerWorld)((AbstractMinecartEntity)(Object)this).world).getEntity(this.getNextUuid());
      }

      return this.next;
   }

   @Override
   public void setNext(AbstractMinecartEntity next) {
      this.next = next;
      this.nextUuid = next == null ? null : next.getUuid();
   }

   @Override
   public UUID getPreviousUuid() {
      return this.previousUuid;
   }

   @Override
   public void setPreviousUuid(UUID uuid) {
      this.previousUuid = uuid;
   }

   @Override
   public UUID getNextUuid() {
      return this.nextUuid;
   }

   @Override
   public void setNextUuid(UUID uuid) {
      this.nextUuid = uuid;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"tick()V"}
   )
   void onTickCommon(CallbackInfo callbackInformation) {
      World mixedWorld = ((AbstractMinecartEntity)(Object)this).world;
      AbstractMinecartEntity next = (AbstractMinecartEntity)(Object)this;

      AbstractMinecartEntityAccessor accessor = (AbstractMinecartEntityAccessor)next;
      if (!mixedWorld.isClient && accessor.getPrevious() != null) {
         AbstractMinecartEntity previous = accessor.getPrevious();
         Pair<BlockPos, MutableDouble> nextRail = RailUtils.getNextRail(next, previous);
         Vec3d nextVelocity = RailUtils.getNextVelocity(next, previous);
         if (nextVelocity != null) {
            if (nextRail == null) {
               next.setVelocity(0, 0, 0);
            }
            else if (previous.getVelocity().getX() == 0 && previous.getVelocity().getZ() == 0) {
               next.setVelocity(0, 0, 0);
            }
            //else if (previous.getPos().distanceTo(next.getPos()) > 1 && nextRail != null && (previous.getVelocity().getX() != 0 || previous.getVelocity().getZ() != 0)) {
            //  next.setVelocity(nextVelocity.getX() + 1, nextVelocity.getY() + 1, nextVelocity.getZ() + 1);
            //}
            else {next.setVelocity(nextVelocity);}
         }
      }

   }

   @Inject(
      at = {@At("HEAD")},
      method = {"pushAwayFrom(Lnet/minecraft/entity/Entity;)V"},
      cancellable = true
   )
   void onPushAway(Entity entity, CallbackInfo callbackInformation) {
      if (!CollisionUtils.shouldCollide((Entity)(Object)this, entity)) {
         callbackInformation.cancel();
      }

   }
}
