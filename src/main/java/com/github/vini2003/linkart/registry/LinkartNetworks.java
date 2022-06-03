package com.github.vini2003.linkart.registry;

import com.github.vini2003.linkart.accessor.AbstractMinecartEntityAccessor;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;

import java.util.UUID;

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
        ServerPlayNetworking.registerGlobalReceiver(LinkartNetworks.LINK_PACKET,
                (server, player, handler, buffer, responseSender) -> {
                    UUID next = buffer.readUuid();
                    UUID previous = buffer.readUuid();
                    ServerWorld serverWorld = player.getServerWorld();
                    server.execute(() -> {
                        ItemStack hand = player.getMainHandStack();
                        if (hand.getItem() != Items.CHAIN) {
                            player.sendMessage(
                                    new TranslatableText(
                                            "text.linkart.message.cart_link_failure_desynchronization")
                                            .formatted(Formatting.RED),
                                    true);
                            return;
                        } else if (!player.isCreative()) {
                            hand.decrement(1);
                        }
                        AbstractMinecartEntity entityA = (AbstractMinecartEntity) serverWorld.getEntity(next);
                        AbstractMinecartEntity entityB = (AbstractMinecartEntity) serverWorld
                                .getEntity(previous);
                        AbstractMinecartEntityAccessor accessorA = (AbstractMinecartEntityAccessor) entityA;
                        AbstractMinecartEntityAccessor accessorB = (AbstractMinecartEntityAccessor) entityB;
                        assert accessorB != null;
                        accessorB.setNext(entityA);
                        assert accessorA != null;
                        accessorA.setPrevious(entityB);
                    });
                });
        ServerPlayNetworking.registerGlobalReceiver(LinkartNetworks.UNLINK_PACKET,
                (server, player, handler, buffer, responseSender) -> {
                    UUID next = buffer.readUuid();
                    UUID previous = buffer.readUuid();
                    ServerWorld serverWorld = player.getServerWorld();
                    AbstractMinecartEntity entityA = (AbstractMinecartEntity) serverWorld
                            .getEntity(next);
                    AbstractMinecartEntity entityB = (AbstractMinecartEntity) serverWorld
                            .getEntity(previous);
                    AbstractMinecartEntityAccessor accessorA = (AbstractMinecartEntityAccessor) entityA;
                    AbstractMinecartEntityAccessor accessorB = (AbstractMinecartEntityAccessor) entityB;
                    server.execute(() -> {
                        assert accessorA != null;
                        accessorA.setPreviousUuid(null);
                        accessorA.setPrevious(null);
                        assert accessorB != null;
                        accessorB.setNextUuid(null);
                        accessorB.setNext(null);
                        ItemScatterer.spawn(
                                entityA.world,
                                entityA.getX(),
                                entityA.getY(),
                                entityA.getZ(),
                                new ItemStack(Items.CHAIN));
                    });
                });
    }
}