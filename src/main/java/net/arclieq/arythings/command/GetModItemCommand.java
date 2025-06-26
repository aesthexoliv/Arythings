package net.arclieq.arythings.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class GetModItemCommand {

    /**
     * Registers the /getmoditem command.
     * <p>Usage:</p>
     * <li>/getmoditem <item_id> [optional:player, default @s]</li>
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("getmoditem")
            .requires(source -> source.hasPermissionLevel(2)) // Adjust permission level as needed (e.g., 2 for ops)
            .then(CommandManager.argument("item", StringArgumentType.string())
                .suggests(GetModItemCommand::suggestModItems)
                .executes(context -> getModItem(
                    context,
                    StringArgumentType.getString(context, "item"),
                    context.getSource().getPlayer() // Default to sender if no player specified
                ))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                    .executes(context -> getModItem(
                        context,
                        StringArgumentType.getString(context, "item"),
                        EntityArgumentType.getPlayer(context, "player")
                    ))
                )
            )
        );
    }

    private static int getModItem(CommandContext<ServerCommandSource> context, String itemId, ServerPlayerEntity targetPlayer) {
        Identifier itemIdentifier = Identifier.tryParse(itemId);
        if (itemIdentifier == null) {
            context.getSource().sendError(Text.literal("Invalid item ID format: " + itemId));
            return 0;
        }

        Item item = Registries.ITEM.get(itemIdentifier);
        // Check if the item exists (Registries.ITEM.get returns Items.AIR if not found)
        if (item == net.minecraft.item.Items.AIR) {
            context.getSource().sendError(Text.literal("Item not found: " + itemId));
            return 0;
        }

        ItemStack itemStack = new ItemStack(item, 1); // Give one item
        if (targetPlayer != null) {
            targetPlayer.giveItemStack(itemStack);
            context.getSource().sendFeedback(() -> Text.literal("Given 1 " + item.getName().getString() + " to " + targetPlayer.getName().getString() + "."), true);
            return 1;
        } else {
            context.getSource().sendError(Text.literal("Player not found or command sender is not a player."));
            return 0;
        }
    }

    private static CompletableFuture<Suggestions> suggestModItems(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        Registries.ITEM.stream()
            .filter(item -> {
                Identifier id = Registries.ITEM.getId(item);
                // Suggest items from your mod (e.g., "arythings") or other non-minecraft mods
                return !id.getNamespace().equals("minecraft");
                // If you only want to suggest items from your mod, use:
                // return id.getNamespace().equals("your_mod_id");
            })
            .map(item -> Registries.ITEM.getId(item).toString())
            .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
