package com.github.vini2003.linkart.utility;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;

import java.util.HashSet;
import java.util.Set;

public class LoadingCarts extends PersistentState {
    public static LoadingCarts getOrCreate(ServerWorld world) {
        return  (world).getPersistentStateManager().getOrCreate((nbt) -> {
            LoadingCarts carts = new LoadingCarts(world);
            carts.readNbt(nbt);
            return carts;
        }, () -> new LoadingCarts(world), "linkart_loading_carts");
    }
    private final ServerWorld world;

    public LoadingCarts(ServerWorld world) {
        this.world = world;
    }
    private final Set<BlockPos> chunksToReload = new HashSet<>();
    private final Set<AbstractMinecartEntity> cartsToBlockPos = new HashSet<>();
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for (AbstractMinecartEntity minecart : cartsToBlockPos) {
            list.add(NbtLong.of(minecart.getBlockPos().asLong()));
        }
        nbt.put("chunksToSave", list);
        cartsToBlockPos.clear();
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        NbtList list = nbt.getList("chunksToSave", NbtElement.LONG_TYPE);
        for (NbtElement element : list) {
            chunksToReload.add(BlockPos.fromLong(((NbtLong) element).longValue()));
        }
    }

    public void tick() {
        if (!chunksToReload.isEmpty()) {
            for (BlockPos pos : chunksToReload) {
                ChunkPos chunkPos = new ChunkPos(pos);
                this.world.getChunkManager().addTicket(ChunkTicketType.PORTAL, chunkPos, 4, pos);
            }
            chunksToReload.clear();
            markDirty();
        }
    }

    public void addCart(AbstractMinecartEntity cart) {
        cartsToBlockPos.add(cart);
        markDirty();
    }
}
