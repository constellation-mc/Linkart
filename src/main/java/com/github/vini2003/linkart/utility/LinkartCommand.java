package com.github.vini2003.linkart.utility;

import com.github.vini2003.linkart.Linkart;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class LinkartCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("linkart")
                .then(CommandManager.literal("config")
                        .then(CommandManager.literal("reload")
                                .executes(context -> {
                                    Linkart.loadConfig();
                                    context.getSource().sendFeedback(TextUtil.literal("reloaded linkart config"), true);
                                    return 1;
                                }))));
    }
}
