package com.github.vini2003.linkart.registry;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LinkartTags {
    public static final TagKey<Item> LINKER_ITEMS = TagKey.of(Registry.ITEM_KEY, new Identifier("linkart", "linker_items"));

    public static void initialize() {}
}
