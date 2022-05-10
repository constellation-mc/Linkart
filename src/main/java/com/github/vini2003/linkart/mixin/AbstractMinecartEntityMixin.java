package com.github.vini2003.linkart.mixin;

import com.github.vini2003.linkart.accessor.AbstractMinecartEntityAccessor;
import com.github.vini2003.linkart.utility.CollisionUtils;
import com.github.vini2003.linkart.utility.RailUtils;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
         Vec3d nextVelocity = RailUtils.getNextVelocity(next, previous);
         if (nextVelocity != null) {
            next.setVelocity(nextVelocity);
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
