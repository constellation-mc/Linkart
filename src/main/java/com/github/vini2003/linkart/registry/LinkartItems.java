package com.github.vini2003.linkart.registry;

import com.github.vini2003.linkart.configuration.LinkartConfiguration;
import com.github.vini2003.linkart.item.LinkerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item.Settings;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LinkartItems {
   //public static Item LINKER_ITEM;

   public static void initialize() {
      /*if (((LinkartConfiguration)LinkartConfigurations.INSTANCE.getConfig()).isLinkerEnabled()) {
         LINKER_ITEM = (Item)Registry.register(
            Registry.ITEM,
            new Identifier("linkart", "linker"),
            new LinkerItem(new Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
         );
      }*/
   }
}
