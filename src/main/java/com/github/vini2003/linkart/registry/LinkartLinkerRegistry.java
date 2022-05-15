package com.github.vini2003.linkart.registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import io.github.foundationgames.sandwichable.entity.EntitiesRegistry;
import io.github.foundationgames.sandwichable.entity.SandwichTableMinecartEntity;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class LinkartLinkerRegistry {
   public static final LinkartLinkerRegistry INSTANCE = new LinkartLinkerRegistry();
   private static final BiMap<EntityType<?>, Collection<Item>> ENTRIES = HashBiMap.create();

   private LinkartLinkerRegistry() {
   }

   public static void initialize() {
      INSTANCE.register(EntityType.MINECART, Items.CHAIN);
      INSTANCE.register(EntityType.CHEST_MINECART, Items.CHAIN);
      INSTANCE.register(EntityType.FURNACE_MINECART, Items.CHAIN);
      INSTANCE.register(EntityType.HOPPER_MINECART, Items.CHAIN);
      INSTANCE.register(EntityType.TNT_MINECART, Items.CHAIN);
      INSTANCE.register(EntityType.SPAWNER_MINECART, Items.CHAIN);
      INSTANCE.register(EntityType.COMMAND_BLOCK_MINECART, Items.CHAIN);
      if (FabricLoader.getInstance().isModLoaded("sandwichable")) {
         INSTANCE.register(EntitiesRegistry.SANDWICH_TABLE_MINECART, Items.CHAIN);
      }
   }

   public Collection<EntityType<?>> getKeys() {
      return ENTRIES.keySet();
   }

   public Collection<Item> getValues() {
      Set<Item> values = new HashSet<>();
      ENTRIES.forEach((entry, items) -> values.addAll(items));
      return values;
   }

   public Collection<Item> getByKey(EntityType<?> key) {
      return (Collection<Item>) ENTRIES.get(key);
   }

   public EntityType<?> getByValue(Collection<Item> value) {
      return (EntityType<?>) ENTRIES.inverse().get(value);
   }

   public void register(EntityType<?> key, Item value) {
      (ENTRIES.computeIfAbsent(key, k -> new HashSet<>())).add(value);
   }
}