package com.github.vini2003.linkart.mixin;

import com.github.vini2003.linkart.Linkart;
import com.github.vini2003.linkart.accessor.LinkableMinecartsAccessor;
import com.github.vini2003.linkart.utility.CollisionUtils;
import com.github.vini2003.linkart.utility.LoadingCarts;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin({AbstractMinecartEntity.class})
public abstract class AbstractMinecartEntityMixin extends Entity implements LinkableMinecartsAccessor {
    @Unique
    private AbstractMinecartEntity linkart$following;
    @Unique
    private AbstractMinecartEntity linkart$follower;
    @Unique
    private UUID linkart$followingUUID;
    @Unique
    private UUID linkart$followerUUID;
    @Unique
    private ItemStack linkart$itemStack;

    public AbstractMinecartEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    private static void linkart$spawnChainParticles(AbstractMinecartEntity entity, LinkableMinecartsAccessor duck) {
        if (!entity.world.isClient()) {
            ((ServerWorld) entity.world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, duck.linkart$getLinkItem()), entity.getX(), entity.getY() + 0.3, entity.getZ(), 15, 0.2, 0.2, 0.2, 0.2);
        }
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void linkart$tick(CallbackInfo ci) {
        if (!world.isClient()) {
            if (linkart$getFollowing() != null) {
                if (linkart$getFollowing().removed || this.removed) {
                    linkart$unlink();
                    return;
                }

                Vec3d pos = getPos();
                Vec3d pos2 = linkart$getFollowing().getPos();
                double dist = Math.abs(pos.distanceTo(pos2)) - 1.2;
                Vec3d vec3d = pos.relativize(pos2).normalize();
                vec3d.multiply(Linkart.CONFIG.velocityMultiplier);

                if (dist <= 1) {
                    setVelocity(vec3d.multiply(dist * 0.75));
                } else {
                    if (dist <= Linkart.CONFIG.pathfindingDistance) {
                        setVelocity(vec3d);
                    } else {
                        linkart$unlink();
                    }
                }
            }

            if (linkart$getFollower() != null) {
                if (Linkart.CONFIG.chunkloading && !approximatelyZero(this.getVelocity().length())) {
                    ((ServerWorld)this.world).getChunkManager().addTicket(ChunkTicketType.PORTAL, this.getChunkPos(), Linkart.CONFIG.chunkloadingRadius, this.getBlockPos());
                    LoadingCarts.getOrCreate((ServerWorld) world).addCart((AbstractMinecartEntity) (Object) this);
                }
            }
        }
    }

    private static boolean approximatelyZero(double a) {
        return Math.abs(0 - a) < 0.00029146489604938;
    }

    private void linkart$unlink() {
        LinkableMinecartsAccessor duck = (LinkableMinecartsAccessor) linkart$getFollowing();

        duck.linkart$setFollower(null);
        linkart$setFollowing(null);

        ItemEntity itemEntity = new ItemEntity(world, getX(), getY(), getZ(), Items.CHAIN.getDefaultStack());
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);

        linkart$spawnChainParticles((AbstractMinecartEntity) (Object) this, this);

        duck.linkart$setLinkItem(null);
    }

    @Inject(at = @At("HEAD"), method = "pushAwayFrom", cancellable = true)
    void onPushAway(Entity entity, CallbackInfo ci) {
        if (!CollisionUtils.shouldCollide(this, entity)) {
            ci.cancel();
        }
    }

    @Inject(at = @At("RETURN"), method = "writeCustomDataToNbt")
    private void linkart$write(NbtCompound nbt, CallbackInfo ci) {
        if (linkart$followingUUID != null) {
            nbt.putUuid("LK-Following", linkart$followingUUID);
        }

        if (linkart$followerUUID != null) {
            nbt.putUuid("LK-Follower", linkart$followerUUID);
        }

        if (linkart$itemStack != null) {
            nbt.put("LK-ItemStack", linkart$itemStack.writeNbt(new NbtCompound()));
        }
    }

    @Inject(at = @At("RETURN"), method = "readCustomDataFromNbt")
    private void linkart$read(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("LK-Following")) {
            linkart$followingUUID = nbt.getUuid("LK-Following");
        }

        if (nbt.contains("LK-Follower")) {
            linkart$followerUUID = nbt.getUuid("LK-Follower");
        }

        if (nbt.contains("LK-ItemStack")) {
            linkart$itemStack = ItemStack.fromNbt(nbt.getCompound("LK-ItemStack"));
        }
    }

    @Override
    public AbstractMinecartEntity linkart$getFollowing() {
        if (linkart$following == null) {
            linkart$following = (AbstractMinecartEntity) ((ServerWorld) this.world).getEntity(linkart$followingUUID);
        }
        return linkart$following;
    }

    public void linkart$setFollowing(AbstractMinecartEntity following) {
        this.linkart$following = following;
        this.linkart$followingUUID = following != null ? following.getUuid() : null;
    }

    @Override
    public AbstractMinecartEntity linkart$getFollower() {
        if (linkart$follower == null) {
            linkart$follower = (AbstractMinecartEntity) ((ServerWorld) this.world).getEntity(linkart$followerUUID);
        }
        return linkart$follower;
    }

    public void linkart$setFollower(AbstractMinecartEntity follower) {
        this.linkart$follower = follower;
        this.linkart$followerUUID = follower != null ? follower.getUuid() : null;
    }

    @Override
    public ItemStack linkart$getLinkItem() {
        return linkart$itemStack;
    }

    @Override
    public void linkart$setLinkItem(ItemStack linkItem) {
        this.linkart$itemStack = linkItem;
    }
}
