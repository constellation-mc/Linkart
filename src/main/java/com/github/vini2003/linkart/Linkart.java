package com.github.vini2003.linkart;

import com.github.vini2003.linkart.registry.LinkartConfigurations;
import com.github.vini2003.linkart.registry.LinkartNetworks;
import com.github.vini2003.linkart.registry.LinkartTags;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

import java.util.HashMap;

public class Linkart implements ModInitializer {
    public static final String ID = "linkart";
    public static final HashMap<PlayerEntity, AbstractMinecartEntity> SELECTED_ENTITIES = new HashMap<>();

    public void onInitialize() {
        LinkartConfigurations.initialize();
        LinkartNetworks.initialize();
        LinkartTags.initialize();
    }
}
