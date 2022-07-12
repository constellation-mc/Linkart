package com.github.vini2003.linkart.mixin;

import com.github.vini2003.linkart.Linkart;
import com.github.vini2003.linkart.accessor.AbstractMinecartEntityAccessor;
import com.github.vini2003.linkart.registry.LinkartConfigurations;
import com.github.vini2003.linkart.registry.LinkartNetworks;
import com.github.vini2003.linkart.registry.LinkartTags;
import com.github.vini2003.linkart.utility.TextUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlayerEntity.class})
public class PlayerEntityMixin {

    @Inject(at = @At("HEAD"), method = "interact", cancellable = true)
    void onInteract(Entity entityA, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (entityA instanceof AbstractMinecartEntity && hand == Hand.MAIN_HAND && entityA.world.isClient) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            ItemStack stackInHand = player.getStackInHand(hand);
            if (stackInHand.isIn(LinkartTags.LINKER_ITEMS)) {
                double x1 = entityA.getX();
                double y1 = entityA.getY();
                double z1 = entityA.getZ();
                if (Linkart.SELECTED_ENTITIES.get(player) == null) {
                    Linkart.SELECTED_ENTITIES.put(player, (AbstractMinecartEntity) entityA);
                    sendToClient(player, new TranslatableText("text.linkart.message.cart.link_initialize",
                                    TextUtils.literal((int) x1, Formatting.GREEN),
                                    TextUtils.literal((int) y1, Formatting.GREEN),
                                    TextUtils.literal((int) z1, Formatting.GREEN)));
                    cancelCallback(cir);
                } else {
                    AbstractMinecartEntityAccessor accessorA = (AbstractMinecartEntityAccessor) entityA;
                    AbstractMinecartEntity entityB = Linkart.SELECTED_ENTITIES.get(player);
                    AbstractMinecartEntityAccessor accessorB = (AbstractMinecartEntityAccessor) entityB;
                    double x2 = entityB.getX();
                    double y2 = entityB.getY();
                    double z2 = entityB.getZ();
                    if (player.world.isClient) {
                        boolean boolA = accessorA.getNext() == entityB && accessorB.getPrevious() == entityA;
                        boolean boolB = accessorB.getNext() == entityA && accessorA.getPrevious() == entityB;
                        if (boolA) {
                            accessorA.setNext(null);
                            accessorB.setPrevious(null);
                            ClientPlayNetworking.send(LinkartNetworks.UNLINK_PACKET, LinkartNetworks.createPacket(entityA, entityB));
                        } else if (boolB) {
                            accessorB.setNext(null);
                            accessorA.setPrevious(null);
                            ClientPlayNetworking.send(LinkartNetworks.UNLINK_PACKET, LinkartNetworks.createPacket(entityA, entityB));
                        }
                        if (boolA || boolB) {
                            sendToClient(
                                    player,
                                    new TranslatableText("text.linkart.message.cart_unlink_success",
                                            TextUtils.literal((int) x1, Formatting.YELLOW),
                                            TextUtils.literal((int) y1, Formatting.YELLOW),
                                            TextUtils.literal((int) z1, Formatting.YELLOW),
                                            TextUtils.literal((int) x2, Formatting.YELLOW),
                                            TextUtils.literal((int) y2, Formatting.YELLOW),
                                            TextUtils.literal((int) z2, Formatting.YELLOW)));
                            cancelCallback(cir, player);
                            return;
                        }
                    }
                    if (entityA == entityB) {
                        sendToClient(player, new TranslatableText("text.linkart.message.cart_link_failure_self").formatted(Formatting.RED));
                        cancelCallback(cir, player);
                    } else if (accessorB.getPrevious() != entityA && accessorA.getNext() != entityB) {

                        int pD = (LinkartConfigurations.INSTANCE.getConfig()).getPathfindingDistance();
                        if (entityA.getPos().distanceTo(entityB.getPos()) > (double) pD) {
                            sendToClient(player, new TranslatableText("text.linkart.message.cart_link_failure_distance", TextUtils.literal(pD)).formatted(Formatting.RED));
                            cancelCallback(cir, player);
                        } else {
                            accessorB.setNext((AbstractMinecartEntity) entityA);
                            ((AbstractMinecartEntityAccessor) accessorB.getNext()).setPrevious(entityB);
                            ClientPlayNetworking.send(LinkartNetworks.LINK_PACKET, LinkartNetworks.createPacket(entityA, entityB));
                            sendToClient(player, new TranslatableText(
                                            "text.linkart.message.cart_link_success",
                                            TextUtils.literal((int) x1, Formatting.GREEN),
                                            TextUtils.literal((int) y1, Formatting.GREEN),
                                            TextUtils.literal((int) z1, Formatting.GREEN),
                                            TextUtils.literal((int) x2, Formatting.GREEN),
                                            TextUtils.literal((int) y2, Formatting.GREEN),
                                            TextUtils.literal((int) z2, Formatting.GREEN))
                            );
                            cancelCallback(cir, player);
                        }
                    } else {
                        sendToClient(player, new TranslatableText("text.linkart.message.cart_link_failure_recursion").formatted(Formatting.RED));
                        cancelCallback(cir, player);
                    }
                }

            }
        }
    }

    private static void cancelCallback(CallbackInfoReturnable<ActionResult> callbackInformationReturnable) {
        callbackInformationReturnable.setReturnValue(ActionResult.FAIL);
        callbackInformationReturnable.cancel();
    }

    private static void cancelCallback(CallbackInfoReturnable<ActionResult> callbackInformationReturnable, PlayerEntity playerEntity) {
        callbackInformationReturnable.setReturnValue(ActionResult.FAIL);
        callbackInformationReturnable.cancel();
        Linkart.SELECTED_ENTITIES.put(playerEntity, null);
    }

    private static void sendToClient(PlayerEntity playerEntity, Text text) {
        if (playerEntity.world.isClient) {
            playerEntity.sendMessage(text, true);
        }

    }
}