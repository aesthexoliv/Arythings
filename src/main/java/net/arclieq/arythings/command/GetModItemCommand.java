package net.arclieq.arythings.command;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.arclieq.arythings.Arythings;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class GetModItemCommand {

    /**
     * Registers the /getmoditem command.
     * <p>Usage:</p>
     * <li>/getmoditem give <players> <item> [amount]</li>
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("getmoditem")
            .requires(source -> source.hasPermissionLevel(2)) // OPs only
            .then(CommandManager.literal("give")
                .then(CommandManager.argument("players", EntityArgumentType.players())
                    .then(CommandManager.argument("item", StringArgumentType.word()).suggests(GetModItemCommand::suggestModItems) // Item argument
                        .executes(context -> { // Executes if only item is provided (amount defaults to 1)
                            return giveModItem(context.getSource(),
                            EntityArgumentType.getPlayers(context, "players"),
                            StringArgumentType.getString(context, "item"),
                            1);
                        })
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))) // Optional amount argument
                            .executes(context -> { // Executes if both item and amount are provided
                            return giveModItem(context.getSource(),
                            EntityArgumentType.getPlayers(context, "players"),
                            StringArgumentType.getString(context, "item"),
                            IntegerArgumentType.getInteger(context, "amount"));
                        })
                    )
                )
            )
        );
    }

    /**
     * Helper for giving a mod item to one or more players.
     */
    private static int giveModItem(ServerCommandSource source, Collection<ServerPlayerEntity> players, String input, int amount) {
        Identifier id;
        if (input.contains(":")) {
            id = Identifier.tryParse(input);
            if (id == null) {
                source.sendError(Text.literal("Invalid item identifier: " + input));
                return 0;
            }
        } else {
            id = Identifier.of(Arythings.MOD_ID, input);
        }

        if (id.getNamespace().equals("minecraft")) {
            source.sendError(Text.literal("Cannot use /getmoditem for Minecraft items! Use /give instead."));
            return 0;
        }

        // Check if the item is banned by configuration
        if (Arythings.getBannedItems().contains(id.toString())) {
            source.sendError(Text.literal("This item is banned by server configuration and cannot be given: " + id));
            return 0;
        }

        Item item = Registries.ITEM.get(id);
        if (item == null || Registries.ITEM.getId(item).equals(Registries.ITEM.getDefaultId())) {
            source.sendError(Text.literal("Item not found: " + id));
            return 0;
        }

        int givenCount = 0;
        for (ServerPlayerEntity player : players) {
            ItemStack stack = new ItemStack(item, amount);
            boolean given = player.getInventory().insertStack(stack);
            if (!given) {
                player.dropItem(stack, false);
            }
            source.sendFeedback(() -> Text.literal("Gave " + amount + " " + id.toTranslationKey() + " to " + player.getName().getString()).styled(style -> style.withColor(Formatting.YELLOW)), true);
            givenCount++;
        }
        return givenCount;
    }

    private static CompletableFuture<Suggestions> suggestModItems(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        Registries.ITEM.stream()
            .filter(item -> {
                Identifier id = Registries.ITEM.getId(item);
                // Only suggest non-minecraft items. The global ban system handles banned items.
                return !id.getNamespace().equals("minecraft");
            })
            .map(item -> Registries.ITEM.getId(item).toString())
            .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
