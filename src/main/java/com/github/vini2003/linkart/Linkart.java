package com.github.vini2003.linkart;

import com.github.vini2003.linkart.configuration.LinkartConfiguration;
import com.github.vini2003.linkart.mixin.PersistentStateAccessor;
import com.github.vini2003.linkart.utility.LinkartCommand;
import com.github.vini2003.linkart.utility.LoadingCarts;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Linkart implements ModInitializer {
    public static final String ID = "linkart";
    public static LinkartConfiguration CONFIG;
    public static final Map<PlayerEntity, AbstractMinecartEntity> LINKING_CARTS = new HashMap<>();
    public static final Map<PlayerEntity, AbstractMinecartEntity> UNLINKING_CARTS = new HashMap<>();
    public static final Tag<Item> LINKERS = TagFactory.ITEM.create(new Identifier(ID, "linkers"));

    static {
        loadConfig();
    }

    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            LinkartCommand.register(dispatcher);
        });

        ServerWorldEvents.LOAD.register((server, world) -> {
            if (CONFIG.chunkloading) LoadingCarts.getOrCreate(world);
        });

        ServerTickEvents.START_WORLD_TICK.register(world -> {
            if (CONFIG.chunkloading && ((PersistentStateAccessor)world.getPersistentStateManager()).linkart$loadedStates().containsKey("linkart_loading_carts")) {
                LoadingCarts.getOrCreate(world).tick();
            }
        });
    }

    public static void loadConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Path config = FabricLoader.getInstance().getConfigDir().resolve("linkart.json");
        if (Files.exists(config)) {
            try {
                CONFIG = gson.fromJson(Files.newBufferedReader(config), LinkartConfiguration.class);
                Files.write(config, gson.toJson(CONFIG).getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            CONFIG = new LinkartConfiguration();
            try {
                Files.createFile(config);
                Files.write(config, gson.toJson(CONFIG).getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
