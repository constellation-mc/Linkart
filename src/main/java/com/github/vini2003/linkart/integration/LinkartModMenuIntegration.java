package com.github.vini2003.linkart.integration;

import com.github.vini2003.linkart.configuration.LinkartConfiguration;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screen.Screen;

public class LinkartModMenuIntegration implements ModMenuApi {
    public String getModId() {
        return "linkart";
    }

    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> (Screen) AutoConfig.getConfigScreen(LinkartConfiguration.class, screen).get();
    }
}
