package com.github.vini2003.linkart.mixin;

import com.github.vini2003.linkart.accessor.AbstractMinecartEntityAccessor;
import com.github.vini2003.linkart.registry.LinkartConfigurations;
import com.github.vini2003.linkart.utility.CollisionUtils;
import com.github.vini2003.linkart.utility.RailUtils;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

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
        if (this.previous == null && this.getPreviousUuid() != null && !((AbstractMinecartEntity) (Object) this).world.isClient) {
            this.previous = (AbstractMinecartEntity) ((ServerWorld) ((AbstractMinecartEntity) (Object) this).world).getEntity(this.getPreviousUuid());
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
        if (this.next == null && this.getNextUuid() != null && !((AbstractMinecartEntity) (Object) this).world.isClient) {
            this.next = (AbstractMinecartEntity) ((ServerWorld) ((AbstractMinecartEntity) (Object) this).world).getEntity(this.getNextUuid());
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
        World mixedWorld = ((AbstractMinecartEntity) (Object) this).world;
        AbstractMinecartEntity next = (AbstractMinecartEntity) (Object) this;

        AbstractMinecartEntityAccessor accessor = (AbstractMinecartEntityAccessor) next;
        if (!mixedWorld.isClient && accessor.getPrevious() != null) {
            AbstractMinecartEntity previous = accessor.getPrevious();
            Pair<BlockPos, MutableDouble> nextRail = RailUtils.getNextRail(next, previous);
            Vec3d nextVelocity = RailUtils.getNextVelocity(next, previous);
            if (nextVelocity != null) {
                if ((next.getPos().distanceTo(previous.getPos()) > LinkartConfigurations.INSTANCE.getConfig().getPathfindingDistance()) && getPrevious() != null) {
                    unlinkCarts();
                } else if (!(next.world.getBlockState(next.getBlockPos()).getBlock() instanceof AbstractRailBlock)) {
                    next.setVelocity(0, 0, 0);
                } else {
                    next.setVelocity(nextVelocity);
                }
            }
        }
    }

    private void unlinkCarts() {
        AbstractMinecartEntity next = (AbstractMinecartEntity) (Object) this;
        AbstractMinecartEntityAccessor accessor = (AbstractMinecartEntityAccessor) next;
        AbstractMinecartEntity previous = accessor.getPrevious();
        UUID nextUUID = next.getUuid();
        UUID previousUUID = previous.getUuid();
        ServerWorld serverWorld = (ServerWorld) ((AbstractMinecartEntity) (Object) this).world;
        AbstractMinecartEntity entityA = (AbstractMinecartEntity) serverWorld
                .getEntity(nextUUID);
        AbstractMinecartEntity entityB = (AbstractMinecartEntity) serverWorld
                .getEntity(previousUUID);
        assert entityA != null;
        ((AbstractMinecartEntityAccessor) previous).setNext(null);
        assert entityB != null;
        ((AbstractMinecartEntityAccessor) next).setPrevious(null);
        if (((AbstractMinecartEntityAccessor) next).getPrevious() == null) {
            ItemScatterer.spawn(
                    entityA.world,
                    entityA.getX(),
                    entityA.getY(),
                    entityA.getZ(),
                    new ItemStack(Items.CHAIN));
        }
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"pushAwayFrom(Lnet/minecraft/entity/Entity;)V"},
            cancellable = true
    )
    void onPushAway(Entity entity, CallbackInfo callbackInformation) {
        if (!CollisionUtils.shouldCollide((Entity) (Object) this, entity)) {
            callbackInformation.cancel();
        }

    }
}
