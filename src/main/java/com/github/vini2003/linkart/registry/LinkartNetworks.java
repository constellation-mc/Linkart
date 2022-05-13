package com.github.vini2003.linkart.registry;

import java.util.UUID;

import com.github.vini2003.linkart.accessor.AbstractMinecartEntityAccessor;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;

public class LinkartNetworks {

   public static final Identifier LINK_PACKET = new Identifier("linkart", "link");
   public static final Identifier UNLINK_PACKET = new Identifier("linkart", "unlink");

   public static PacketByteBuf createPacket(Entity next, Entity previous) {
      PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
      buffer.writeUuid(next.getUuid());
      buffer.writeUuid(previous.getUuid());
      return buffer;
   }

   public static void initialize() {
      ServerSidePacketRegistry.INSTANCE
            .register(
                  LINK_PACKET,
                  (context, buffer) -> {
                     UUID next = buffer.readUuid();
                     UUID previous = buffer.readUuid();
                     ServerWorld serverWorld = (ServerWorld) context.getPlayer().getEntityWorld();
                     context.getTaskQueue().execute(() -> {
                        PlayerEntity player = context.getPlayer();
                        PlayerScreenHandler playerContainer = player.playerScreenHandler;
                        ItemStack hand = context.getPlayer().getMainHandStack();

                        if (hand.getItem() != Items.CHAIN) {
                            player.sendMessage(
                                 new TranslatableText("text.linkart.message.cart_link_failure_desynchronization")
                                       .formatted(Formatting.RED),
                                 true);
                           return;
                        } else if (!player.isCreative()) {
                            hand.decrement(1);;
                        }

                        AbstractMinecartEntity entityA = (AbstractMinecartEntity) serverWorld.getEntity(next);
                        AbstractMinecartEntity entityB = (AbstractMinecartEntity) serverWorld.getEntity(previous);
                        AbstractMinecartEntityAccessor accessorA = (AbstractMinecartEntityAccessor) entityA;
                        AbstractMinecartEntityAccessor accessorB = (AbstractMinecartEntityAccessor) entityB;
                         assert accessorB != null;
                         accessorB.setNext(entityA);
                         assert accessorA != null;
                         accessorA.setPrevious(entityB);
                     });
                  });
      ServerSidePacketRegistry.INSTANCE
            .register(
                  UNLINK_PACKET,
                  (context, buffer) -> {
                     UUID next = buffer.readUuid();
                     UUID previous = buffer.readUuid();
                     ServerWorld serverWorld = (ServerWorld) context.getPlayer().getEntityWorld();
                     context.getTaskQueue()
                           .execute(
                                 () -> {
                                    AbstractMinecartEntity entityA = (AbstractMinecartEntity) serverWorld
                                          .getEntity(next);
                                    AbstractMinecartEntity entityB = (AbstractMinecartEntity) serverWorld
                                          .getEntity(previous);
                                    AbstractMinecartEntityAccessor accessorA = (AbstractMinecartEntityAccessor) entityA;
                                    AbstractMinecartEntityAccessor accessorB = (AbstractMinecartEntityAccessor) entityB;
                                     assert accessorA != null;
                                     accessorA.setNext(null);
                                     assert accessorB != null;
                                     accessorB.setPrevious(null);
                                    PlayerEntity playerEntity = context.getPlayer();
                                    ItemScatterer.spawn(
                                             playerEntity.world,
                                             playerEntity.getX(),
                                             playerEntity.getY(),
                                             playerEntity.getZ(),
                                             new ItemStack(Items.CHAIN));

                                 });
                  });
   }
}