package com.github.vini2003.linkart.registry;

import java.util.Collection;
import java.util.HashMap;
import net.minecraft.entity.EntityType;

public class LinkartDistanceRegistry {
   public static final LinkartDistanceRegistry INSTANCE = new LinkartDistanceRegistry();
   private static final HashMap<EntityType<?>, Double> DISTANCES = new HashMap<>();

   private LinkartDistanceRegistry() {
   }

   public static void initialize() {
   }

   public Collection<EntityType<?>> getKeys() {
      return DISTANCES.keySet();
   }

   public Collection<Double> getValues() {
      return DISTANCES.values();
   }

   public Double getByKey(EntityType<?> key) {
      return (Double)DISTANCES.get(key);
   }

   public EntityType<?> getByValue(Double value) {
      throw new UnsupportedOperationException("Cannot retrieve registry entry by key in registry whose values can be duplicate!");
   }

   public void register(EntityType<?> key, Double value) {
      DISTANCES.put(key, value);
   }

   static {
      INSTANCE.register(EntityType.MINECART, 1.0);
      INSTANCE.register(EntityType.CHEST_MINECART, 1.0);
      INSTANCE.register(EntityType.HOPPER_MINECART, 1.0);
      INSTANCE.register(EntityType.FURNACE_MINECART, 1.0);
      INSTANCE.register(EntityType.TNT_MINECART, 1.0);
      INSTANCE.register(EntityType.SPAWNER_MINECART, 1.0);
      INSTANCE.register(EntityType.COMMAND_BLOCK_MINECART, 1.0);
   }
}
