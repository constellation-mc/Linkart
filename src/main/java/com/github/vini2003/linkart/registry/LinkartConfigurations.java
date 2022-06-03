package com.github.vini2003.linkart.registry;

import com.github.vini2003.linkart.configuration.LinkartConfiguration;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class LinkartConfigurations {
    public static ConfigHolder<LinkartConfiguration> INSTANCE;

    private LinkartConfigurations() {
    }

    public static void initialize() {
        AutoConfig.register(LinkartConfiguration.class, GsonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(LinkartConfiguration.class);
    }
}
