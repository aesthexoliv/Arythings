package net.arclieq.arythings.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.arclieq.arythings.util.ConfigManager;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class BanItemCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        
        // Register /banitem
        dispatcher.register(CommandManager.literal("banitem")
            .requires(source -> source.hasPermissionLevel(4))
            .then(CommandManager.argument("item", IdentifierArgumentType.identifier()).suggests(BanItemCommand::suggestBannableItems)
                .executes(context -> {
                    Identifier itemId = IdentifierArgumentType.getIdentifier(context, "item");
                    boolean itemExists = Registries.ITEM.containsId(itemId);

                    // Item not found in registry:
                    if(!itemExists) {
                        context.getSource().sendError(Text.literal("Error: Item '").append(Text.literal(itemId.toString().formatted(Formatting.YELLOW))
                            .append(Text.literal("' not found!")))); 
                        return 0;
                    }
                
                    // Item already banned:
                    if(ConfigManager.getBannedItems().contains(itemId.toString())) {
                        context.getSource().sendError(Text.literal("Error: Item '").append(Text.literal(itemId.toString().formatted(Formatting.YELLOW))
                            .append(Text.literal("' is already banned!")))); 
                        return 0;
                    }
                    
                    // Ban item:
                    ConfigManager.addBannedItem(itemId.toString());
                    context.getSource().sendFeedback(() -> Text.literal("Item '").append(Text.literal(itemId.toString()).formatted(Formatting.RED)).append("' is now banned and will be removed from inventories."), true);
                    return 1;
                })
            )
        );

        // Register /unbanitem
        dispatcher.register(CommandManager.literal("unbanitem")
            .requires(source -> source.hasPermissionLevel(4))
            .then(CommandManager.argument("item", IdentifierArgumentType.identifier()).suggests(BanItemCommand::suggestBannedItems)
                .executes(context -> {
                    Identifier itemId = IdentifierArgumentType.getIdentifier(context, "item");
                    boolean banned = ConfigManager.getBannedItems().contains(itemId.toString());
                    boolean itemExists = Registries.ITEM.containsId(itemId);

                    // If not banned item:
                    if(!banned && itemExists) {
                        context.getSource().sendError(Text.literal("Item '" + 
                            itemId.toString().formatted(Formatting.YELLOW) + "' is not banned!"));
                        return 0;
                    }

                    // Banned item found in config, registry item not found.
                    if(banned && !itemExists) {
                        context.getSource().sendError(Text.literal("Error: Item '" + 
                            itemId.toString().formatted(Formatting.YELLOW) + "' not found ingame!"));
                        return 0;
                    }

                    // NOT banned item, NOT an ingame item.
                    if(!banned && !itemExists) {
                        context.getSource().sendError(Text.literal("Error: Item '" + 
                            itemId.toString() + "' not found and not banned!"));
                        return 0;
                    }

                    // Banned item unban:
                    ConfigManager.removeBannedItem(itemId.toString());
                    context.getSource().sendFeedback(() -> Text.literal("Item '")
                        .append(Text.literal(itemId.toString()).formatted(Formatting.GREEN))
                        .append("' is no longer banned."), true);
                    return 1;
                })
            )
        );
    }

    private static CompletableFuture<Suggestions> suggestBannableItems(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        Registries.ITEM.stream()
            .filter(item -> !ConfigManager.getBannedItems().contains(Registries.ITEM.getId(item).toString()))
            .map(item -> Registries.ITEM.getId(item).toString())
            .forEach(builder::suggest);
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestBannedItems(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        for (String s : ConfigManager.getBannedItems()) {
            if(Registries.ITEM.containsId(Identifier.of(s))) builder.suggest(s);
        }
        return builder.buildFuture();
    }
}