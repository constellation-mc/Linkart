package com.github.vini2003.linkart.registry;

import com.github.vini2003.linkart.configuration.LinkartConfiguration;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class LinkartItemGroups {
   public static ItemGroup LINKART_GROUP;

   public static void initialize() {
      if (((LinkartConfiguration)LinkartConfigurations.INSTANCE.getConfig()).isLinkerEnabled()) {
         LINKART_GROUP = FabricItemGroupBuilder.build(new Identifier("linkart", "linkart"), () -> new ItemStack(LinkartItems.LINKER_ITEM));
      } else if (((LinkartConfiguration)LinkartConfigurations.INSTANCE.getConfig()).isChainEnabled()) {
         LINKART_GROUP = FabricItemGroupBuilder.build(new Identifier("linkart", "linkart"), () -> new ItemStack(LinkartItems.CHAIN_ITEM));
      }

   }
}
