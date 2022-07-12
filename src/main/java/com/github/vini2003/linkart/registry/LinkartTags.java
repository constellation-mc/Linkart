package com.github.vini2003.linkart.registry;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class LinkartTags {
    @Deprecated(since = "3.1.1-1.17")
    public static final Tag<Item> LINKER_ITEMS = TagRegistry.item(new Identifier("linkart", "linker_items"));

    public static void initialize() {}
}
