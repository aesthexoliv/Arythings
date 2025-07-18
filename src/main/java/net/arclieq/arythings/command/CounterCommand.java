package net.arclieq.arythings.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.arclieq.arythings.util.CounterHelperUtil;
import net.arclieq.arythings.util.CounterHelperUtil.CounterData;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class CounterCommand {

    /**
     * Registers the /counter command with add, setvalue, increment, setmode, get, remove, and reset subcommands.
     * <p>Usage:</p>
     * <ul> <li>/counter add [countername]</li></ul>
     * Adds a counter with [countername] as its name.
     * 
     * <ul> <li>/counter get [optional:showall]</li></ul>
     * Gets all players' counters, including offline players if [showall].
     * 
     * <ul> <li>/counter increment [amount] [optional:counter, default all] [optional:player, default <br>@a]</li></ul>
     * Increments [amount] to all counters to all players if [counter] and [player] is not set, 
     * and if [amount] will not make counter go over the max counter value, which is default to 32767.
     * 
     * <ul> <li>/counter remove [counter]</li></ul>
     * Removes [counter] for ALL players, including offline players.
     * 
     * <ul> <li>/counter reset </li></ul>
     * Resets the default counters to its default values.
     * 
     * <ul> <li>/counter setvalue [amount] [optional:counter, default all] [optional:player, default <br>@a]</li></ul>
     * Sets the value of all counter to all players to [amount] if [counter] and [player] is not set, 
     * and if [amount] is not over the max counter value, which is default to 32767.
     * 
     * <ul> <li>/counter setmode [counter] [mode]</li></ul>
     * 
     * Changes [counter]'s mode to [mode].
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("counter")
                .requires(source -> source.hasPermissionLevel(2))

                // /counter get (shows all counters for all players)
                .then(CommandManager.literal("get")
                        .executes(context -> getCounters(context, false))
                        
                        .then(CommandManager.literal("showall"))
                            .executes(context -> getCounters(context, true))
                )

                // /counter setvalue <amount> [<counter>] [<player>]
                .then(CommandManager.literal("setvalue")
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(0))
                                // /counter setvalue <amount>
                                .executes(context -> {
                                    List<String> counters = null;
                                    for (Map<String, CounterData> map : CounterHelperUtil.getAllPlayerCounters().values()) {
                                        for (String counterName : map.keySet()) {
                                            counters = List.of(counterName);
                                        }
                                    }
                                    return counterSetValue(context, counters, 
                                    context.getSource().getServer().getPlayerManager().getPlayerList());
                                })
                                // /counter setvalue <amount> <counter>
                                .then(CommandManager.argument("counter", StringArgumentType.word()).suggests(CounterCommand::suggestCounters)
                                        .executes(context -> counterSetValue(context, 
                                        List.of(StringArgumentType.getString(context, "counter")), 
                                        context.getSource().getServer().getPlayerManager().getPlayerList()))

                                        // /counter setvalue <amount> <counter> <player>
                                        .then(CommandManager.argument("player", EntityArgumentType.players())
                                            .executes(context -> counterSetValue(context, 
                                            List.of(StringArgumentType.getString(context, "counter")), 
                                            EntityArgumentType.getPlayers(context, "player")))
                                        )
                                )
                        )
                )

                // /counter increment <amount> [<counter>] [<player>]
                .then(CommandManager.literal("increment")
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                // /counter increment <amount>
                                .executes(context -> {
                                    List<String> counters = null;
                                    for (Map<String, CounterData> map : CounterHelperUtil.getAllPlayerCounters().values()) {
                                        for (String counterName : map.keySet()) {
                                            counters = List.of(counterName);
                                        }
                                    }

                                    return incrementCounter(context, counters, context.getSource().getServer().getPlayerManager().getPlayerList());
                                })
                                // /counter increment <amount> <counter>
                                .then(CommandManager.argument("counter", StringArgumentType.word()).suggests(CounterCommand::suggestCounters)
                                        .executes(context -> incrementCounter(context, List.of(StringArgumentType.getString(context, "counter")), context.getSource().getServer().getPlayerManager().getPlayerList()))
                                        // /counter increment <amount> <counter> <player>
                                        .then(CommandManager.argument("player", EntityArgumentType.players())
                                                .executes(context -> incrementCounter(context, List.of(StringArgumentType.getString(context, "counter")), EntityArgumentType.getPlayers(context, "player")))
                                        )
                                )
                        )
                )

                // /counter add <counter> [optional:mode, default manual]
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("counter", StringArgumentType.word())
                                .then(CommandManager.argument("mode", StringArgumentType.string()).suggests(CounterCommand::suggestModes)
                                    // /counter add <counter> <mode>
                                    .executes(context -> addCounter(context, true))
                                )
                                // /counter add <counter>
                                .executes(context -> addCounter(context, false))
                        )
                )

                // /counter setmode <counter> <mode> (applies to all players)
                .then(CommandManager.literal("setmode")
                        .then(CommandManager.argument("counter", StringArgumentType.word()).suggests(CounterCommand::suggestCounters)
                                .then(CommandManager.argument("mode", StringArgumentType.word()).suggests(CounterCommand::suggestModes)
                                        .executes(context -> setModeCounter(context))
                                )
                        )
                )

                // /counter remove <counter> (removes from all players)
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("counter", StringArgumentType.word()).suggests(CounterCommand::suggestCounters)
                                .executes(context -> removeCounter(context))
                        )
                )

                // /counter reset (removes all counters for everybody, then resets 'luzzantum', 
                // 'netiamond', 'astryluna_upgrade', and 'lives' for online players)
                .then(CommandManager.literal("reset")
                        .executes(context -> resetCounters(context))
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

    private static int getCounters(CommandContext<ServerCommandSource> context, boolean showAll) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = context.getSource().getServer();
        boolean any = false;

        if (showAll) {
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
        } else {
            boolean noPlayers = false;
            if(server.getPlayerManager().getPlayerList().isEmpty() || server.getPlayerManager().getPlayerList() == null) {
                noPlayers = true;
            }
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                UUID playerUUID = player.getUuid();
                Map<String, CounterHelperUtil.CounterData> counters = CounterHelperUtil.getAllPlayerCounters().get(playerUUID);

                if (counters == null || counters.isEmpty()) continue;
                any = true;
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
            if(noPlayers) {
                source.sendError(Text.literal("No online players found!"));
                return 0;
            }
        }

        if (!any) {
            source.sendError(Text.literal("No counters found for any player!"));
            return 0;
        }
        return 1;
    }

    private static int counterSetValue(CommandContext<ServerCommandSource> context, List<String> counters, Collection<ServerPlayerEntity> players) {
        int amount = IntegerArgumentType.getInteger(context, "amount");
        ServerCommandSource source = context.getSource();
        MinecraftServer server = context.getSource().getServer();
        AtomicInteger affectedPlayersCount = new AtomicInteger(0);
        AtomicInteger notAffected = new AtomicInteger(0);
        AtomicInteger notAffectedCounterCount = new AtomicInteger(0);
        AtomicInteger counterCount = new AtomicInteger(0);

        if (players.isEmpty()) {
            source.sendError(Text.literal("No target players found by the selector."));
            return 0;
        }

        if(counters == null) {
            source.sendError(Text.literal("No counters found for any player!"));
        }
        
        for (String counterName : counters) {
            counterCount.incrementAndGet();
            for (ServerPlayerEntity player : players) {
            Map<String, CounterHelperUtil.CounterData> playerCounters = CounterHelperUtil.getAllPlayerCounters().get(player.getUuid());
            if (playerCounters != null && playerCounters.containsKey(counterName)) {
                if(notAffected.get() >= players.size()) {
                    continue;
                }
                if(amount > CounterHelperUtil.getMaxCounterValue()) {
                    notAffected.incrementAndGet();
                    notAffectedCounterCount.incrementAndGet();
                } else {
                    CounterMode mode = CounterHelperUtil.getCounter(player.getUuid(), counterName, server).getValue();
                    CounterHelperUtil.setCounter(player.getUuid(), counterName, amount, mode, server);
                    affectedPlayersCount.incrementAndGet();
                }
            }
        }
        }
        if (affectedPlayersCount.get() == 0 && notAffected.get() == 0 && counterCount.get() == 1) {
            source.sendError(Text.literal("Counter '" + counters.getFirst() + "' not found for any of the " + players.size() + " player(s)."));
            return 0;
        }

        if(notAffected.get() != 0 && notAffectedCounterCount.get() == 1) {
            source.sendError(Text.literal(notAffected.get() + " player(s) with counter '" + counters.getFirst() + "' cannot be set above the max counter value: " + CounterHelperUtil.getMaxCounterValue()));
            return 0;
        } 
        if(notAffected.get() != 0 && notAffectedCounterCount.get() > 1) {
            source.sendError(Text.literal(notAffectedCounterCount.get() + " counters for " + notAffected.get() + " player(s) cannot be set above the max counter value: " + CounterHelperUtil.getMaxCounterValue()));
            return 0;
        }

        if(counterCount.get() > 1 && players.size() > 1) {
            source.sendFeedback(() -> Text.literal("Set " + counterCount.get() + " counter(s) for " + affectedPlayersCount.get() + " player(s) to " + amount).styled(style -> style.withColor(Formatting.YELLOW)), false);
            return 1;
        } 
        if(players.size() >= 1 && counterCount.get() == 1) {
            source.sendFeedback(() -> Text.literal("Set counter '" + counters.getFirst() + "' for " + affectedPlayersCount.get() + " player(s) to " + amount).styled(style -> style.withColor(Formatting.YELLOW)), false);
            return 1;
        } 
        if(counterCount.get() == 1 && players.size() == 1) {
            source.sendFeedback(() -> Text.literal("Set counter '" + counters.getFirst() + "' for player " + players.iterator().next().getName() + " to " + amount).styled(style -> style.withColor(Formatting.YELLOW)), false);
            return 1;
        }
        source.sendError(Text.literal("An unexpected error has occured."));
        return 0;
    }

    private static int incrementCounter(CommandContext<ServerCommandSource> context, List<String> counters, Collection<ServerPlayerEntity> players) {
        ServerCommandSource source = context.getSource();
        int amount = IntegerArgumentType.getInteger(context, "amount");
        MinecraftServer server = context.getSource().getServer();
        AtomicInteger affectedPlayersCount = new AtomicInteger(0);
        AtomicInteger notAffected = new AtomicInteger(0);
        AtomicInteger counterCount = new AtomicInteger(0);
        AtomicInteger notAffectedCounterCount = new AtomicInteger(0);
        if(counters == null) {
            source.sendError(Text.literal("No counters found for any player!"));
            return 0;
        }

        for (String counterName : counters) {
            counterCount.incrementAndGet();

            if (players.isEmpty() || players == null) {
                source.sendError(Text.literal("No target players found!"));
                return 0;
            }

            for (ServerPlayerEntity player : players) {
                Map<String, CounterHelperUtil.CounterData> playerCounters = CounterHelperUtil.getAllPlayerCounters().get(player.getUuid());
                if (playerCounters != null && playerCounters.containsKey(counterName)) {
                    if (notAffected.get() >= players.size()) {
                        continue;
                    }

                    if (amount + CounterHelperUtil.getCounterValue(player.getUuid(), counterName, server) > CounterHelperUtil.getMaxCounterValue()) {
                        notAffected.incrementAndGet();
                        notAffectedCounterCount.incrementAndGet();
                    } else {
                        CounterMode mode = CounterHelperUtil.getCounter(player.getUuid(), counterName, server).getValue();
                        int current = CounterHelperUtil.getCounter(player.getUuid(), counterName, server).getKey();
                        CounterHelperUtil.setCounter(player.getUuid(), counterName, current + amount, mode, server);
                        affectedPlayersCount.incrementAndGet();
                    }
                }
            }
        }
        if (affectedPlayersCount.get() == 0 && notAffected.get() == 0 && counterCount.get() == 1) {
            source.sendError(Text.literal("Counter '" + counters.getFirst() + "' not found for any of the " + players.size() + " player(s)."));
            return 0;
        }

        if(notAffected.get() != 0 && notAffectedCounterCount.get() == 1) {
            source.sendError(Text.literal(notAffected.get() + " player(s) with counter '" + counters.getFirst() + "' cannot be incremented above the max counter value: " + CounterHelperUtil.getMaxCounterValue()));
            return 0;
        } 
        if(notAffected.get() != 0 && notAffectedCounterCount.get() > 1) {
            source.sendError(Text.literal(notAffectedCounterCount.get() + " counters for " + notAffected.get() + " player(s) cannot be incremented above the max counter value: " + CounterHelperUtil.getMaxCounterValue()));
            return 0;
        }

        if(counterCount.get() > 1 && players.size() > 1) {
            source.sendFeedback(() -> Text.literal("Incremented " + counterCount.get() + " counter(s) for " + affectedPlayersCount.get() + " player(s) by " + amount).styled(style -> style.withColor(Formatting.YELLOW)), false);
            return 1;
        } 
        if(players.size() >= 1 && counterCount.get() == 1) {
            source.sendFeedback(() -> Text.literal("Incremented counter '" + counters.getFirst() + "' for " + affectedPlayersCount.get() + " player(s) by " + amount).styled(style -> style.withColor(Formatting.YELLOW)), false);
            return 1;
        } if(counterCount.get() == 1) {
            source.sendFeedback(() -> Text.literal("Incremented counter '" + counters.getFirst() + "' for player " + players.iterator().next().getName() + " by " + amount).styled(style -> style.withColor(Formatting.YELLOW)), false);
            return 1;
        }
        source.sendError(Text.literal("An unexpected error has occured."));
        return 0;
    }

    private static int setModeCounter(CommandContext<ServerCommandSource> context) {
        String countername = StringArgumentType.getString(context, "counter");
        String modeStr = StringArgumentType.getString(context, "mode").toUpperCase();
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();

        // Check if the counter is unchangeable from config, using CounterHelperUtil
        if (CounterHelperUtil.getUnchangeableCounters().contains(countername) 
            || countername.equals("netiamond") || countername.equals("luzzantum")
            || countername.equals("astryluna_upgrade") || countername.equals("lives")) {
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
    }

    private static int removeCounter(CommandContext<ServerCommandSource> context) {
        String countername = StringArgumentType.getString(context, "counter");
        // Check if the counter is marked as unchangeable, using CounterHelperUtil
        if (CounterHelperUtil.getUnchangeableCounters().contains(countername)
            || countername.equals("netiamond") || countername.equals("luzzantum")
            || countername.equals("astryluna_upgrade") || countername.equals("lives")) {
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
    }
    private static int resetCounters(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();

        // 1. Clear all counters for all players (both in memory and in file)
        CounterHelperUtil.clearAllCounters();
        CounterHelperUtil.saveData(server); // Save the empty state to disk

        // 2. Reset specific counters to 20000 for only online players
        String[] countersToReset = {"luzzantum", "netiamond", "astryluna_upgrade", "lives"};
        AtomicInteger playersReset = new AtomicInteger(0);
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            boolean playerHadCountersReset = false;
            for (String counter : countersToReset) {
                if(counter.equals("lives")) {
                    CounterHelperUtil.setCounter(player.getUuid(), counter, 2, CounterMode.MANUAL, server);
                    playerHadCountersReset = true;
                }
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
            "Counters '" + String.join(", ", countersToReset) + "' reset to default values for " + playersReset + " online player(s)."
        ).styled(style -> style.withColor(Formatting.RED)), true);

        return 1;
    }

    private static int addCounter(CommandContext<ServerCommandSource> context, boolean modeFound) {
        String countername = StringArgumentType.getString(context, "counter");
        CounterMode mode;
        String lowerMode = StringArgumentType.getString(context, "mode");
        if(!modeFound) {
            mode = CounterMode.MANUAL;
        } else {
            mode = CounterMode.valueOf(StringArgumentType.getString(context, "mode").toUpperCase());
        }
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        AtomicInteger initialized = new java.util.concurrent.atomic.AtomicInteger(0);
        AtomicInteger notInitialized = new AtomicInteger(0);
        // Iterate through all players who have existing counter data
        for (UUID playerUUID : CounterHelperUtil.getAllPlayerCounters().keySet()) {
            // Handle players who might be online but have no saved counters yet
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (!CounterHelperUtil.hasCounter(player.getUuid(), countername)) {
                    CounterHelperUtil.setCounter(player.getUuid(), countername, 0, CounterMode.MANUAL, server);
                    initialized.incrementAndGet();
                } else if(CounterHelperUtil.hasCounter(player.getUuid(), countername)) notInitialized.incrementAndGet();
            }
            if (!CounterHelperUtil.hasCounter(playerUUID, countername)) {
                CounterHelperUtil.setCounter(playerUUID, countername, 0, mode, server);
                initialized.incrementAndGet();
            } else if(CounterHelperUtil.hasCounter(playerUUID, countername)) notInitialized.incrementAndGet();
        }

        if(notInitialized.get() != 0) {
            source.sendError(Text.literal("Counter '" + countername + "' is already added for " + notInitialized.get() + " player(s)!"));
        }

        CounterHelperUtil.saveData(server);
        if(!modeFound) {
            source.sendFeedback(() -> Text.literal(
                "Added counter '" + countername + "' with mode manual " + initialized.get() + " player(s)."
            ).styled(style -> style.withColor(Formatting.GREEN)), true);
        }
        if(modeFound) {
            source.sendFeedback(() -> Text.literal(
                "Added counter '" + countername + "' with mode " + lowerMode + " for " + initialized.get() + " player(s)."
            ).styled(style -> style.withColor(Formatting.GREEN)), true);
        }
        return 1;
    }
}