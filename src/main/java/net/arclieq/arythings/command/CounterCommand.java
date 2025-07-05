package net.arclieq.arythings.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.arclieq.arythings.util.CounterHelperUtil;
import net.arclieq.arythings.util.CounterHelperUtil.CounterMode;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class CounterCommand {

    /**
     * Registers the /counter command with add, setvalue, increment, setmode, get, remove, and reset subcommands.
     * <p>Usage:</p>
     * <li>/counter add <countername></li>
     * <li>/counter setvalue <amount> [optional:counter, default all] [optional:player, default @a]</li>
     * <li>/counter increment <amount> [optional:counter, default all] [optional:player, default @a]</li>
     * <li>/counter setmode <counter> <mode></li>
     * <li>/counter get</li>
     * <li>/counter remove <counter></li>
     * <li>/counter reset</li>
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("counter")
                .requires(source -> source.hasPermissionLevel(2)) // Only OPs (permission level 2+) can use

                // /counter get (shows all counters for all players, formatted, no dashes)
                .then(CommandManager.literal("get")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            MinecraftServer server = source.getServer();
                            boolean any = false;
                            // Iterate over all players with saved counter data, not just online players
                            for (Map.Entry<UUID, Map<String, CounterHelperUtil.CounterData>> playerEntry : CounterHelperUtil.getAllPlayerCounters().entrySet()) {
                                UUID playerUUID = playerEntry.getKey();
                                Map<String, CounterHelperUtil.CounterData> counters = playerEntry.getValue();

                                if (counters == null || counters.isEmpty()) continue;
                                any = true;

                                // Attempt to get the player's name, if online. Otherwise, use UUID string.
                                ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUUID);
                                String playerName = (player != null) ? player.getName().getString() : playerUUID.toString();

                                // Prepare all lines
                                String playerLine = "- Player " + playerName + " -";
                                java.util.List<String> counterLines = new java.util.ArrayList<>();
                                for (Map.Entry<String, CounterHelperUtil.CounterData> entry : counters.entrySet()) {
                                    String line = entry.getKey() + ": " + entry.getValue().value + " (" + entry.getValue().mode + ")";
                                    counterLines.add(line);
                                }

                                // Find max width among all lines for consistent formatting
                                int maxWidth = playerLine.length();
                                for (String line : counterLines) {
                                    if (line.length() > maxWidth) maxWidth = line.length();
                                }

                                // Build output
                                StringBuilder sb = new StringBuilder();
                                sb.append(centerText(playerLine, maxWidth)).append("\n"); // Player line centered within adjusted width
                                for (String line : counterLines) {
                                    sb.append(line).append("\n");
                                }
                                source.sendFeedback(() -> Text.literal(sb.toString()).styled(style -> style.withColor(Formatting.YELLOW)), false);
                            }
                            if (!any) {
                                source.sendError(Text.literal("No counters found for any player!"));
                                return 0;
                            }
                            return 1;
                        })
                )

                // /counter setvalue <amount> [<counter>] [<player>]
                .then(CommandManager.literal("setvalue")
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(0))
                                // Base case: /counter setvalue <amount>
                                .executes(context -> {
                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                    ServerCommandSource source = context.getSource();
                                    MinecraftServer server = source.getServer();
                                    Collection<ServerPlayerEntity> playersToAffect = server.getPlayerManager().getPlayerList(); // Defaults to @a
                                    final AtomicInteger affectedPlayersCount = new AtomicInteger(0);
                                    final AtomicInteger counterCount = new AtomicInteger(0);

                                    if (playersToAffect.isEmpty()) {
                                        source.sendError(Text.literal("No online players found to update counters for."));
                                        return 0;
                                    }

                                    for (ServerPlayerEntity player : playersToAffect) {
                                        Map<String, CounterHelperUtil.CounterData> playerCounters = CounterHelperUtil.getAllPlayerCounters().get(player.getUuid());
                                        if (playerCounters == null || playerCounters.isEmpty()) continue;

                                        for (String counter : playerCounters.keySet()) {
                                            CounterMode mode = CounterHelperUtil.getCounter(player.getUuid(), counter, server).getValue();
                                            CounterHelperUtil.setCounter(player.getUuid(), counter, amount, mode, server);
                                            counterCount.incrementAndGet();
                                        }
                                        affectedPlayersCount.incrementAndGet();
                                    }
                                    if (counterCount.get() == 0) {
                                        source.sendError(Text.literal("No counters found for any online player to update."));
                                        return 0;
                                    }
                                    source.sendFeedback(() -> Text.literal("Set " + counterCount.get() + " counter(s) for " + affectedPlayersCount.get() + " player(s) to " + amount).styled(style -> style.withColor(Formatting.YELLOW)), true);
                                    return 1;
                                })
                                // Optional counter argument: /counter setvalue <amount> <counter>
                                .then(CommandManager.argument("counter", StringArgumentType.word()).suggests(CounterCommand::suggestCounters)
                                        .executes(context -> {
                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            String counterName = StringArgumentType.getString(context, "counter");
                                            ServerCommandSource source = context.getSource();
                                            MinecraftServer server = source.getServer();
                                            Collection<ServerPlayerEntity> playersToAffect = server.getPlayerManager().getPlayerList();
                                            final AtomicInteger affectedCount = new AtomicInteger(0);

                                            for (ServerPlayerEntity player : playersToAffect) {
                                                Map<String, CounterHelperUtil.CounterData> playerCounters = CounterHelperUtil.getAllPlayerCounters().get(player.getUuid());
                                                if (playerCounters != null && playerCounters.containsKey(counterName)) {
                                                    CounterMode mode = CounterHelperUtil.getCounter(player.getUuid(), counterName, server).getValue();
                                                    CounterHelperUtil.setCounter(player.getUuid(), counterName, amount, mode, server);
                                                    affectedCount.incrementAndGet();
                                                }
                                            }
                                            if (affectedCount.get() == 0) {
                                                source.sendError(Text.literal("Counter '" + counterName + "' not found for any online player."));
                                                return 0;
                                            }
                                            source.sendFeedback(() -> Text.literal("Set counter '" + counterName + "' for " + affectedCount.get() + " player(s) to " + amount).styled(style -> style.withColor(Formatting.YELLOW)), true);
                                            return 1;
                                        })
                                        // Optional player argument after counter: /counter setvalue <amount> <counter> <player>
                                        .then(CommandManager.argument("player", EntityArgumentType.players())
                                                .executes(context -> {
                                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                                    String counterName = StringArgumentType.getString(context, "counter");
                                                    Collection<ServerPlayerEntity> playersToAffect = EntityArgumentType.getPlayers(context, "player");
                                                    ServerCommandSource source = context.getSource();
                                                    MinecraftServer server = context.getSource().getServer();
                                                    final AtomicInteger affectedPlayersCount = new AtomicInteger(0);

                                                    if (playersToAffect.isEmpty()) {
                                                        source.sendError(Text.literal("No target players found by the selector."));
                                                        return 0;
                                                    }

                                                    for (ServerPlayerEntity player : playersToAffect) {
                                                        Map<String, CounterHelperUtil.CounterData> playerCounters = CounterHelperUtil.getAllPlayerCounters().get(player.getUuid());
                                                        if (playerCounters != null && playerCounters.containsKey(counterName)) {
                                                            CounterMode mode = CounterHelperUtil.getCounter(player.getUuid(), counterName, server).getValue();
                                                            CounterHelperUtil.setCounter(player.getUuid(), counterName, amount, mode, server);
                                                            affectedPlayersCount.incrementAndGet();
                                                        }
                                                    }
                                                    if (affectedPlayersCount.get() == 0) {
                                                        source.sendError(Text.literal("Counter '" + counterName + "' not found for any of the " + playersToAffect.size() + " selected player(s)."));
                                                        return 0;
                                                    }
                                                    source.sendFeedback(() -> Text.literal("Set counter '" + counterName + "' for " + affectedPlayersCount.get() + " selected player(s) to " + amount).styled(style -> style.withColor(Formatting.YELLOW)), true);
                                                    return 1;
                                                })
                                        )
                                )
                                // Optional player argument directly after amount (no specific counter): /counter setvalue <amount> <player>
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(context -> {
                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            Collection<ServerPlayerEntity> playersToAffect = EntityArgumentType.getPlayers(context, "player");
                                            ServerCommandSource source = context.getSource();
                                            MinecraftServer server = context.getSource().getServer();
                                            final AtomicInteger affectedPlayersCount = new AtomicInteger(0);
                                            final AtomicInteger counterCount = new AtomicInteger(0);

                                            if (playersToAffect.isEmpty()) {
                                                source.sendError(Text.literal("No target players found by the selector."));
                                                return 0;
                                            }

                                            for (ServerPlayerEntity player : playersToAffect) {
                                                Map<String, CounterHelperUtil.CounterData> playerCounters = CounterHelperUtil.getAllPlayerCounters().get(player.getUuid());
                                                if (playerCounters == null || playerCounters.isEmpty()) continue;

                                                for (String counter : playerCounters.keySet()) {
                                                    CounterMode mode = CounterHelperUtil.getCounter(player.getUuid(), counter, server).getValue();
                                                    CounterHelperUtil.setCounter(player.getUuid(), counter, amount, mode, server);
                                                    counterCount.incrementAndGet();
                                                }
                                                affectedPlayersCount.incrementAndGet();
                                            }
                                            if (affectedPlayersCount.get() == 0) {
                                                source.sendError(Text.literal("The selected player(s) have no counters to update."));
                                                return 0;
                                            }
                                            source.sendFeedback(() -> Text.literal("Set " + counterCount.get() + " counter(s) for " + affectedPlayersCount.get() + " selected player(s) to " + amount).styled(style -> style.withColor(Formatting.YELLOW)), true);
                                            return 1;
                                        })
                                )
                        )
                )

                // /counter increment <amount> [<counter>] [<player>]
                .then(CommandManager.literal("increment")
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                // Base case: /counter increment <amount>
                                .executes(context -> {
                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                    ServerCommandSource source = context.getSource();
                                    MinecraftServer server = source.getServer();
                                    Collection<ServerPlayerEntity> playersToAffect = server.getPlayerManager().getPlayerList(); // Defaults to @a
                                    final AtomicInteger affectedPlayersCount = new AtomicInteger(0);
                                    final AtomicInteger counterCount = new AtomicInteger(0);

                                    if (playersToAffect.isEmpty()) {
                                        source.sendError(Text.literal("No online players found to update counters for."));
                                        return 0;
                                    }

                                    for (ServerPlayerEntity player : playersToAffect) {
                                        Map<String, CounterHelperUtil.CounterData> playerCounters = CounterHelperUtil.getAllPlayerCounters().get(player.getUuid());
                                        if (playerCounters == null || playerCounters.isEmpty()) continue;

                                        for (String counter : playerCounters.keySet()) {
                                            CounterMode mode = CounterHelperUtil.getCounter(player.getUuid(), counter, server).getValue();
                                            int current = CounterHelperUtil.getCounter(player.getUuid(), counter, server).getKey();
                                            CounterHelperUtil.setCounter(player.getUuid(), counter, current + amount, mode, server);
                                            counterCount.incrementAndGet();
                                        }
                                        affectedPlayersCount.incrementAndGet();
                                    }
                                    if (counterCount.get() == 0) {
                                        source.sendError(Text.literal("No counters found for any online player to update."));
                                        return 0;
                                    }
                                    source.sendFeedback(() -> Text.literal("Incremented " + counterCount.get() + " counter(s) for " + affectedPlayersCount.get() + " player(s) by " + amount).styled(style -> style.withColor(Formatting.YELLOW)), true);
                                    return 1;
                                })
                                // Optional counter argument: /counter increment <amount> <counter>
                                .then(CommandManager.argument("counter", StringArgumentType.word()).suggests(CounterCommand::suggestCounters)
                                        .executes(context -> {
                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            String counterName = StringArgumentType.getString(context, "counter");
                                            ServerCommandSource source = context.getSource();
                                            MinecraftServer server = source.getServer();
                                            Collection<ServerPlayerEntity> playersToAffect = server.getPlayerManager().getPlayerList();
                                            final AtomicInteger affectedCount = new AtomicInteger(0);

                                            for (ServerPlayerEntity player : playersToAffect) {
                                                Map<String, CounterHelperUtil.CounterData> playerCounters = CounterHelperUtil.getAllPlayerCounters().get(player.getUuid());
                                                if (playerCounters != null && playerCounters.containsKey(counterName)) {
                                                    CounterMode mode = CounterHelperUtil.getCounter(player.getUuid(), counterName, server).getValue();
                                                    int current = CounterHelperUtil.getCounter(player.getUuid(), counterName, server).getKey();
                                                    CounterHelperUtil.setCounter(player.getUuid(), counterName, current + amount, mode, server);
                                                    affectedCount.incrementAndGet();
                                                }
                                            }
                                            if (affectedCount.get() == 0) {
                                                source.sendError(Text.literal("Counter '" + counterName + "' not found for any online player."));
                                                return 0;
                                            }
                                            source.sendFeedback(() -> Text.literal("Incremented counter '" + counterName + "' for " + affectedCount.get() + " player(s) by " + amount).styled(style -> style.withColor(Formatting.YELLOW)), true);
                                            return 1;
                                        })
                                        // Optional player argument after counter: /counter increment <amount> <counter> <player>
                                        .then(CommandManager.argument("player", EntityArgumentType.players())
                                                .executes(context -> {
                                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                                    String counterName = StringArgumentType.getString(context, "counter");
                                                    Collection<ServerPlayerEntity> playersToAffect = EntityArgumentType.getPlayers(context, "player");
                                                    ServerCommandSource source = context.getSource();
                                                    MinecraftServer server = context.getSource().getServer();
                                                    final AtomicInteger affectedPlayersCount = new AtomicInteger(0);

                                                    if (playersToAffect.isEmpty()) {
                                                        source.sendError(Text.literal("No target players found by the selector."));
                                                        return 0;
                                                    }

                                                    for (ServerPlayerEntity player : playersToAffect) {
                                                        Map<String, CounterHelperUtil.CounterData> playerCounters = CounterHelperUtil.getAllPlayerCounters().get(player.getUuid());
                                                        if (playerCounters != null && playerCounters.containsKey(counterName)) {
                                                            CounterMode mode = CounterHelperUtil.getCounter(player.getUuid(), counterName, server).getValue();
                                                            int current = CounterHelperUtil.getCounter(player.getUuid(), counterName, server).getKey();
                                                            CounterHelperUtil.setCounter(player.getUuid(), counterName, current + amount, mode, server);
                                                            affectedPlayersCount.incrementAndGet();
                                                        }
                                                    }
                                                    if (affectedPlayersCount.get() == 0) {
                                                        source.sendError(Text.literal("Counter '" + counterName + "' not found for any of the " + playersToAffect.size() + " selected player(s)."));
                                                        return 0;
                                                    }
                                                    source.sendFeedback(() -> Text.literal("Incremented counter '" + counterName + "' for " + affectedPlayersCount.get() + " selected player(s) by " + amount).styled(style -> style.withColor(Formatting.YELLOW)), true);
                                                    return 1;
                                                })
                                        )
                                )
                                // Optional player argument directly after amount (no specific counter): /counter increment <amount> <player>
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(context -> {
                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            Collection<ServerPlayerEntity> playersToAffect = EntityArgumentType.getPlayers(context, "player");
                                            ServerCommandSource source = context.getSource();
                                            MinecraftServer server = context.getSource().getServer();
                                            final AtomicInteger affectedPlayersCount = new AtomicInteger(0);
                                            final AtomicInteger counterCount = new AtomicInteger(0);

                                            if (playersToAffect.isEmpty()) {
                                                source.sendError(Text.literal("No target players found by the selector."));
                                                return 0;
                                            }

                                            for (ServerPlayerEntity player : playersToAffect) {
                                                Map<String, CounterHelperUtil.CounterData> playerCounters = CounterHelperUtil.getAllPlayerCounters().get(player.getUuid());
                                                if (playerCounters == null || playerCounters.isEmpty()) continue;

                                                for (String counter : playerCounters.keySet()) {
                                                    CounterMode mode = CounterHelperUtil.getCounter(player.getUuid(), counter, server).getValue();
                                                    int current = CounterHelperUtil.getCounter(player.getUuid(), counter, server).getKey();
                                                    CounterHelperUtil.setCounter(player.getUuid(), counter, current + amount, mode, server);
                                                    counterCount.incrementAndGet();
                                                }
                                                affectedPlayersCount.incrementAndGet();
                                            }
                                            if (affectedPlayersCount.get() == 0) {
                                                source.sendError(Text.literal("The selected player(s) have no counters to update."));
                                                return 0;
                                            }
                                            source.sendFeedback(() -> Text.literal("Incremented " + counterCount.get() + " counter(s) for " + affectedPlayersCount.get() + " selected player(s) by " + amount).styled(style -> style.withColor(Formatting.YELLOW)), true);
                                            return 1;
                                        })
                                )
                        )
                )

                // /counter add <counter> [optional:mode, default manual]
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("counter", StringArgumentType.word())
                                .then(CommandManager.argument("mode", StringArgumentType.string()).suggests(CounterCommand::suggestModes)
                                        .executes(context -> {
                                            // /counter add <counter> <mode>
                                            String countername = StringArgumentType.getString(context, "counter");
                                            CounterMode mode;
                                            String lowerMode = StringArgumentType.getString(context, "mode");
                                            ServerCommandSource source = context.getSource();
                                            MinecraftServer server = source.getServer();
                                            java.util.concurrent.atomic.AtomicInteger initialized = new java.util.concurrent.atomic.AtomicInteger(0);
                                            try {
                                                mode = CounterMode.valueOf(StringArgumentType.getString(context, "mode").toUpperCase());
                                            } catch (IllegalArgumentException e) {
                                                source.sendError(Text.literal("Invalid mode! Use tick, manual, or custom."));
                                                return 0;
                                            }
                                            // Iterate through all players who have existing counter data
                                            for (UUID playerUUID : CounterHelperUtil.getAllPlayerCounters().keySet()) {
                                                if (!CounterHelperUtil.hasCounter(playerUUID, countername)) {
                                                    CounterHelperUtil.setCounter(playerUUID, countername, 0, mode, server);
                                                    initialized.incrementAndGet();
                                                }
                                            }
                                            // Handle players who might be online but have no saved counters yet
                                            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                                                if (!CounterHelperUtil.hasCounter(player.getUuid(), countername)) {
                                                    CounterHelperUtil.setCounter(player.getUuid(), countername, 0, CounterMode.MANUAL, server);
                                                    initialized.incrementAndGet();
                                                }
                                            }
                                            CounterHelperUtil.saveData(server);
                                            source.sendFeedback(() -> Text.literal(
                                                    "Added counter '" + countername + "' with mode " + lowerMode + " for " + initialized.get() + " player(s)."
                                            ).styled(style -> style.withColor(Formatting.GREEN)), true);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    // /counter add <counter> (defaults to manual)
                                    String countername = StringArgumentType.getString(context, "counter");
                                    ServerCommandSource source = context.getSource();
                                    MinecraftServer server = source.getServer();
                                    java.util.concurrent.atomic.AtomicInteger initialized = new java.util.concurrent.atomic.AtomicInteger(0);
                                    // Iterate through all players who have existing counter data
                                    for (UUID playerUUID : CounterHelperUtil.getAllPlayerCounters().keySet()) {
                                        if (!CounterHelperUtil.hasCounter(playerUUID, countername)) {
                                            CounterHelperUtil.setCounter(playerUUID, countername, 0, CounterMode.MANUAL, server);
                                            initialized.incrementAndGet();
                                        }
                                    }
                                    // Handle players who might be online but have no saved counters yet
                                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                                        if (!CounterHelperUtil.hasCounter(player.getUuid(), countername)) {
                                            CounterHelperUtil.setCounter(player.getUuid(), countername, 0, CounterMode.MANUAL, server);
                                            initialized.incrementAndGet();
                                        }
                                    }
                                    CounterHelperUtil.saveData(server);
                                    source.sendFeedback(() -> Text.literal(
                                            "Added counter '" + countername + "' with mode manual for " + initialized.get() + " player(s)."
                                    ).styled(style -> style.withColor(Formatting.GREEN)), true);
                                    return 1;
                                })
                        )
                )

                // /counter setmode <counter> <mode> (applies to all players)
                .then(CommandManager.literal("setmode")
                        .then(CommandManager.argument("counter", StringArgumentType.word()).suggests(CounterCommand::suggestCounters)
                                .then(CommandManager.argument("mode", StringArgumentType.word()).suggests(CounterCommand::suggestModes)
                                        .executes(context -> {
                                            String countername = StringArgumentType.getString(context, "counter");
                                            String modeStr = StringArgumentType.getString(context, "mode").toUpperCase();
                                            ServerCommandSource source = context.getSource();
                                            MinecraftServer server = source.getServer();

                                            // Check if the counter is unchangeable from config, using CounterHelperUtil
                                            if (CounterHelperUtil.getUnchangeableCounters().contains(countername)) {
                                                source.sendError(Text.literal("You cannot change this counter's mode: " + countername));
                                                return 0;
                                            }

                                            CounterHelperUtil.CounterMode mode;
                                            try {
                                                mode = CounterHelperUtil.CounterMode.valueOf(modeStr);
                                            } catch (IllegalArgumentException e) {
                                                source.sendError(Text.literal("Invalid mode! Use tick, manual, or custom."));
                                                return 0;
                                            }
                                            AtomicInteger changed = new AtomicInteger(0);
                                            // Iterate through all players who have existing counter data
                                            for (UUID playerUUID : CounterHelperUtil.getAllPlayerCounters().keySet()) {
                                                if (CounterHelperUtil.hasCounter(playerUUID, countername)) {
                                                    int value = CounterHelperUtil.getCounter(playerUUID, countername, server).getKey();
                                                    CounterHelperUtil.setCounter(playerUUID, countername, value, mode, server);
                                                    changed.getAndIncrement();
                                                }
                                            }
                                            // Also check for online players who might not yet be in saved data but have the counter
                                            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                                                if (CounterHelperUtil.hasCounter(player.getUuid(), countername)) {
                                                    int value = CounterHelperUtil.getCounter(player.getUuid(), countername, server).getKey();
                                                    CounterHelperUtil.setCounter(player.getUuid(), countername, value, mode, server);
                                                    // No need to increment 'changed' again if it was already handled by allPlayerCounters loop
                                                }
                                            }
                                            CounterHelperUtil.saveData(server);
                                            source.sendFeedback(() -> Text.literal(
                                                    "Set mode for counter '" + countername + "' to " + mode + " for " + changed + " player(s)."
                                            ).styled(style -> style.withColor(Formatting.YELLOW)), true);
                                            if (changed.get() == 0) {
                                                source.sendError(Text.literal("Counter '" + countername + "' not found for any player."));
                                                return 0;
                                            }
                                            return 1;
                                        })
                                )
                        )
                )

                // /counter remove <counter> (removes from all players)
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("counter", StringArgumentType.word()).suggests(CounterCommand::suggestCounters)
                                .executes(context -> {
                                    String countername = StringArgumentType.getString(context, "counter");
                                    // Check if the counter is marked as unchangeable, using CounterHelperUtil
                                    if (CounterHelperUtil.getUnchangeableCounters().contains(countername)) {
                                        context.getSource().sendError(Text.literal("You cannot remove this counter: " + countername));
                                        return 0;
                                    }
                                    ServerCommandSource source = context.getSource();
                                    MinecraftServer server = source.getServer();
                                    AtomicInteger removedCount = new AtomicInteger(0);
                                    java.util.List<UUID> uuidsToRemoveFrom = new java.util.ArrayList<>(CounterHelperUtil.getAllPlayerCounters().keySet());

                                    for (UUID playerUUID : uuidsToRemoveFrom) {
                                        boolean removed = CounterHelperUtil.removeCounter(playerUUID, countername, server);
                                        if (removed) {
                                            removedCount.getAndIncrement();
                                        }
                                    }
                                    // If the counter was removed from any player, save the data
                                    if (removedCount.get() > 0) {
                                        CounterHelperUtil.saveData(server);
                                        source.sendFeedback(() -> Text.literal(
                                                "Removed counter '" + countername + "' from " + removedCount + " player(s)."
                                        ).styled(style -> style.withColor(Formatting.RED)), true);
                                    } else {
                                        source.sendError(Text.literal("Counter not found for any player: " + countername + "!"));
                                        return 0;
                                    }
                                    return 1;
                                })
                        )
                )

                // /counter reset (removes all counters for everybody, then resets luzzantum and netiamond for online players)
                .then(CommandManager.literal("reset")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            MinecraftServer server = source.getServer();

                            // 1. Clear all counters for all players (both in memory and in file)
                            CounterHelperUtil.clearAllCounters();
                            CounterHelperUtil.saveData(server); // Save the empty state to disk

                            // 2. Reset specific counters to 20000 for only online players
                            String[] countersToReset = {"luzzantum", "netiamond"};
                            AtomicInteger playersReset = new AtomicInteger(0);
                            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                                boolean playerHadCountersReset = false;
                                for (String counter : countersToReset) {
                                    CounterHelperUtil.setCounter(player.getUuid(), counter, 20000, CounterMode.TICK, server);
                                    playerHadCountersReset = true;
                                }
                                if (playerHadCountersReset) {
                                    playersReset.getAndIncrement();
                                }
                            }
                            CounterHelperUtil.saveData(server); // Save the reset state for online players

                            source.sendFeedback(() -> Text.literal(
                                    "All counters removed for all players. " +
                                    "Counters '" + String.join(", ", countersToReset) + "' reset to 20000 for " + playersReset + " online player(s)."
                            ).styled(style -> style.withColor(Formatting.RED)), true);

                            return 1;
                        })
                )
        );
    }

    private static CompletableFuture<Suggestions> suggestCounters(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        ServerPlayerEntity player = null;
        try {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
            if (!players.isEmpty()) {
                player = players.iterator().next();
            }
        } catch (Exception ignored) {
            if (context.getSource().getEntity() instanceof ServerPlayerEntity p) {
                player = p;
            }
        }
        if (player != null) {
            CounterHelperUtil.getAllPlayerCounters()
                    .getOrDefault(player.getUuid(), Collections.emptyMap())
                    .keySet()
                    .forEach(builder::suggest);
        }
        return builder.buildFuture();
    }

    private static String centerText(String text, int width) {
        int pad = width - text.length();
        int left = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + text + " ".repeat(right);
    }

    private static CompletableFuture<Suggestions> suggestModes(
            CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        String[] modes = {"manual", "tick", "custom"};
        for (String mode : modes) {
            builder.suggest(mode);
        }
        return builder.buildFuture();
    }
}