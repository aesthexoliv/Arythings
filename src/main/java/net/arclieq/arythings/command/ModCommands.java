package net.arclieq.arythings.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
;

public class ModCommands {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        GetModItemCommand.register(dispatcher);
        CounterCommand.register(dispatcher);
        BanItemCommand.register(dispatcher);
    }
}
