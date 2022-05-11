package com.github.vini2003.linkart.item;

import java.util.List;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class LinkerItem extends Item {
   public LinkerItem(Settings settings) {
      super(settings);
   }

   /*public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
      super.appendTooltip(stack, world, tooltip, context);
      if (!Screen.hasShiftDown()) {
         tooltip.add(new TranslatableText("text.linkart.item.show_more").formatted(new Formatting[]{Formatting.ITALIC, Formatting.GREEN}));
      } else {
         tooltip.add(
            new TranslatableText("text.linkart.item.showing_more_parent").formatted(new Formatting[]{Formatting.ITALIC, Formatting.BLUE})
         );
         tooltip.add(new TranslatableText("text.linkart.item.showing_more_child").formatted(new Formatting[]{Formatting.ITALIC, Formatting.BLUE}));
         tooltip.add(
            new TranslatableText("text.linkart.item.showing_more_conclusion").formatted(new Formatting[]{Formatting.ITALIC, Formatting.BLUE})
         );
      }

   }*/
}
