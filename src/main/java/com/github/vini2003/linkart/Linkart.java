package com.github.vini2003.linkart;

import java.util.HashMap;

import com.github.vini2003.linkart.registry.LinkartConfigurations;
import com.github.vini2003.linkart.registry.LinkartDistanceRegistry;
import com.github.vini2003.linkart.registry.LinkartLinkerRegistry;
import com.github.vini2003.linkart.registry.LinkartNetworks;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

public class Linkart implements ModInitializer {
   public static final String ID = "linkart";
   public static final HashMap<PlayerEntity, AbstractMinecartEntity> SELECTED_ENTITIES = new HashMap<>();

   public void onInitialize() {
      LinkartConfigurations.initialize();
      LinkartNetworks.initialize();
      LinkartDistanceRegistry.initialize();
      LinkartLinkerRegistry.initialize();
   }
}
