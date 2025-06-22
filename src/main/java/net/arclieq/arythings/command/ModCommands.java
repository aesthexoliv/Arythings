package net.arclieq.arythings.command;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.arclieq.arythings.util.CounterHelperUtil;
import net.arclieq.arythings.util.CounterHelperUtil.CounterMode;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import java.util.Map;

public class ModCommands {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        registerCounterCommand(dispatcher);
        registerGetModItemCommand(dispatcher);
    }

    /**
     * Registers the /counter command with add, setvalue, increment, setmode, get and remove subcommands.
     * <p>Usage:</p>
     *   <li>/counter add <countername></li>
     *   <li>/counter setvalue <amount> [optional:counter, default all] [optional:player, default @a]</li>
     *   <li>/counter increment <amount> [optional:counter, default all] [optional:player, default @a]</li>
     *   <li>/counter setmode <counter> <mode></li>
     *   <li>/counter get</li>
     *   <li>/counter remove <counter></li>
     */
    public static void registerCounterCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
    dispatcher.register(CommandManager.literal("counter")
    .requires(source -> source.hasPermissionLevel(2)) // Only OPs (permission level 2+) can use

    // /counter get (shows all counters for all players, formatted)
    .then(CommandManager.literal("get")
        .executes(context -> {
            ServerCommandSource source = context.getSource();
            MinecraftServer server = source.getServer();
            boolean any = false;
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                Map<String, CounterHelperUtil.CounterData> counters =
                    CounterHelperUtil.getAllPlayerCounters().get(player.getUuid());
                if (counters == null || counters.isEmpty()) continue;
                any = true;

                // Prepare all lines
                String playerLine = "- Player " + player.getName().getString() + " -";
                java.util.List<String> counterLines = new java.util.ArrayList<>();
                for (Map.Entry<String, CounterHelperUtil.CounterData> entry : counters.entrySet()) {
                    String line = entry.getKey() + ": " + entry.getValue().value + " (" + entry.getValue().mode + ")";
                    counterLines.add(line);
                }

                // Find max width (for inside text)
                int maxWidth = playerLine.length();
                for (String line : counterLines) {
                    if (line.length() > maxWidth) maxWidth = line.length();
                }

                // Build border
                String border = "-".repeat(maxWidth);

                // Build output
                StringBuilder sb = new StringBuilder();
                sb.append(border).append("\n");
                sb.append(centerText(playerLine, maxWidth)).append("\n");
                for (String line : counterLines) {
                    sb.append(line).append("\n");
                }
                sb.append(border);
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
                        CounterMode mode = CounterHelperUtil.getCounterMode(player.getUuid(), counter);
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
            .then(CommandManager.argument("counter", StringArgumentType.word()).suggests(ModCommands::suggestCounters)
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
                            CounterMode mode = CounterHelperUtil.getCounterMode(player.getUuid(), counterName);
                            CounterHelperUtil.setCounter(player.getUuid(), counterName, amount, mode, server);
                            affectedCount.incrementAndGet();
                        }
                    }
                    if (affectedCount.get() == 0) {
                        source.sendError(Text.literal("Counter '" + counterName + "' not found for any online player.")); // Slightly refined
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
                        MinecraftServer server = source.getServer();
                        final AtomicInteger affectedPlayersCount = new AtomicInteger(0);

                        if (playersToAffect.isEmpty()) { // Check if the selector yielded any players
                            source.sendError(Text.literal("No target players found by the selector."));
                            return 0;
                        }

                        for (ServerPlayerEntity player : playersToAffect) {
                            Map<String, CounterHelperUtil.CounterData> playerCounters = CounterHelperUtil.getAllPlayerCounters().get(player.getUuid());
                            if (playerCounters != null && playerCounters.containsKey(counterName)) {
                                CounterMode mode = CounterHelperUtil.getCounterMode(player.getUuid(), counterName);
                                CounterHelperUtil.setCounter(player.getUuid(), counterName, amount, mode, server);
                                affectedPlayersCount.incrementAndGet();
                            }
                        }
                        if (affectedPlayersCount.get() == 0) { // Check if the counter was found for any of the *selected* players
                            source.sendError(Text.literal("Counter '" + counterName + "' not found for any of the " + playersToAffect.size() + " selected player(s)."));
                            return 0;
                        }
                        source.sendFeedback(() -> Text.literal("Set counter '" + counterName + "' for " + affectedPlayersCount.get() + " selected player(s) to " + amount).styled(style -> style.withColor(Formatting.YELLOW)), true); // Added 'selected'
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
                    MinecraftServer server = source.getServer();
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
                            CounterMode mode = CounterHelperUtil.getCounterMode(player.getUuid(), counter);
                            CounterHelperUtil.setCounter(player.getUuid(), counter, amount, mode, server);
                            counterCount.incrementAndGet();
                        }
                        affectedPlayersCount.incrementAndGet();
                    }
                    if (affectedPlayersCount.get() == 0) { // This implies players were found by selector, but had no counters
                        source.sendError(Text.literal("The selected player(s) have no counters to update."));
                        return 0;
                    }
                    source.sendFeedback(() -> Text.literal("Set " + counterCount.get() + " counter(s) for " + affectedPlayersCount.get() + " selected player(s) to " + amount).styled(style -> style.withColor(Formatting.YELLOW)), true); // Added 'selected'
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
                        CounterMode mode = CounterHelperUtil.getCounterMode(player.getUuid(), counter);
                        int current = CounterHelperUtil.getCounterValue(player.getUuid(), counter, server);
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
            .then(CommandManager.argument("counter", StringArgumentType.word()).suggests(ModCommands::suggestCounters)
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
                            CounterMode mode = CounterHelperUtil.getCounterMode(player.getUuid(), counterName);
                            int current = CounterHelperUtil.getCounterValue(player.getUuid(), counterName, server);
                            CounterHelperUtil.setCounter(player.getUuid(), counterName, current + amount, mode, server);
                            affectedCount.incrementAndGet();
                        }
                    }
                    if (affectedCount.get() == 0) {
                        source.sendError(Text.literal("Counter '" + counterName + "' not found for any online player.")); // Slightly refined
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
                        MinecraftServer server = source.getServer();
                        final AtomicInteger affectedPlayersCount = new AtomicInteger(0);

                        if (playersToAffect.isEmpty()) { // Check if the selector yielded any players
                            source.sendError(Text.literal("No target players found by the selector."));
                            return 0;
                        }

                        for (ServerPlayerEntity player : playersToAffect) {
                            Map<String, CounterHelperUtil.CounterData> playerCounters = CounterHelperUtil.getAllPlayerCounters().get(player.getUuid());
                            if (playerCounters != null && playerCounters.containsKey(counterName)) {
                                CounterMode mode = CounterHelperUtil.getCounterMode(player.getUuid(), counterName);
                                int current = CounterHelperUtil.getCounterValue(player.getUuid(), counterName, server);
                                CounterHelperUtil.setCounter(player.getUuid(), counterName, current + amount, mode, server);
                                affectedPlayersCount.incrementAndGet();
                            }
                        }
                        if (affectedPlayersCount.get() == 0) { // Check if the counter was found for any of the *selected* players
                            source.sendError(Text.literal("Counter '" + counterName + "' not found for any of the " + playersToAffect.size() + " selected player(s)."));
                            return 0;
                        }
                        source.sendFeedback(() -> Text.literal("Incremented counter '" + counterName + "' for " + affectedPlayersCount.get() + " selected player(s) by " + amount).styled(style -> style.withColor(Formatting.YELLOW)), true); // Added 'selected'
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
                    MinecraftServer server = source.getServer();
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
                            CounterMode mode = CounterHelperUtil.getCounterMode(player.getUuid(), counter);
                            int current = CounterHelperUtil.getCounterValue(player.getUuid(), counter, server);
                            CounterHelperUtil.setCounter(player.getUuid(), counter, current + amount, mode, server);
                            counterCount.incrementAndGet();
                        }
                        affectedPlayersCount.incrementAndGet();
                    }
                    if (affectedPlayersCount.get() == 0) { // This implies players were found by selector, but had no counters
                        source.sendError(Text.literal("The selected player(s) have no counters to update."));
                        return 0;
                    }
                    source.sendFeedback(() -> Text.literal("Incremented " + counterCount.get() + " counter(s) for " + affectedPlayersCount.get() + " selected player(s) by " + amount).styled(style -> style.withColor(Formatting.YELLOW)), true); // Added 'selected'
                    return 1;
                })
            )
        )
    )

    // /counter add <counter> [optional:mode, default manual]
    .then(CommandManager.literal("add")
        .then(CommandManager.argument("counter", StringArgumentType.word())
            .then(CommandManager.argument("mode", StringArgumentType.string()).suggests(ModCommands::suggestModes)
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
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        if (!CounterHelperUtil.hasCounter(player.getUuid(), countername)) {
                            CounterHelperUtil.setCounter(player.getUuid(), countername, 0, mode, server);
                            initialized.incrementAndGet();
                        }
                    }
                    source.sendFeedback(() -> Text.literal(
                        "Added counter '" + countername + "' with mode " + lowerMode + " for " + initialized.get() + " player(s)."
                    ).styled(style -> style.withColor(Formatting.YELLOW)), true);
                    return 1;
                })
            )
            .executes(context -> {
                // /counter add <counter> (defaults to manual)
                String countername = StringArgumentType.getString(context, "counter");
                ServerCommandSource source = context.getSource();
                MinecraftServer server = source.getServer();
                java.util.concurrent.atomic.AtomicInteger initialized = new java.util.concurrent.atomic.AtomicInteger(0);
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    if (!CounterHelperUtil.hasCounter(player.getUuid(), countername)) {
                        CounterHelperUtil.setCounter(player.getUuid(), countername, 0, CounterMode.MANUAL, server);
                        initialized.incrementAndGet();
                    }
                }
                source.sendFeedback(() -> Text.literal(
                    "Added counter '" + countername + "' with mode manual for " + initialized.get() + " player(s)."
                ).styled(style -> style.withColor(Formatting.YELLOW)), true);
                return 1;
            })
        )
    )

    // /counter setmode <counter> <mode> (applies to all players)
    .then(CommandManager.literal("setmode")
        .then(CommandManager.argument("counter", StringArgumentType.word()).suggests(ModCommands::suggestCounters)
            .then(CommandManager.argument("mode", StringArgumentType.word()).suggests(ModCommands::suggestModes)
                .executes(context -> {
                    String countername = StringArgumentType.getString(context, "counter");
                    String modeStr = StringArgumentType.getString(context, "mode").toUpperCase();
                    ServerCommandSource source = context.getSource();
                    MinecraftServer server = source.getServer();
                    CounterHelperUtil.CounterMode mode;
                    try {
                        mode = CounterHelperUtil.CounterMode.valueOf(modeStr);
                    } catch (IllegalArgumentException e) {
                        source.sendError(Text.literal("Invalid mode! Use tick, manual, or custom."));
                        return 0;
                    }
                    int changed = 0;
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        int value = CounterHelperUtil.getCounterValue(player.getUuid(), countername, server);
                        CounterHelperUtil.setCounter(player.getUuid(), countername, value, mode, server);
                        source.sendFeedback(() -> Text.literal(
                            "Set mode for counter '" + countername + "' to " + mode + "."
                        ).styled(style -> style.withColor(Formatting.YELLOW)), true);
                        changed++;
                    }
                    if (changed == 0) {
                        source.sendError(Text.literal("No players found."));
                        return 0;
                    }
                    return 1;
                })
            )
        )
    )

    // /counter remove <counter> (removes from all players)
    .then(CommandManager.literal("remove")
        .then(CommandManager.argument("counter", StringArgumentType.word()).suggests(ModCommands::suggestCounters)
            .executes(context -> {
                String countername = StringArgumentType.getString(context, "counter");
                if(countername.equalsIgnoreCase("counter") || countername.equalsIgnoreCase("netiamond")) {
                    context.getSource().sendError(Text.literal("You cannot remove this counter: " + countername + "!"));
                    return 0;
                }
                ServerCommandSource source = context.getSource();
                MinecraftServer server = source.getServer();
                int removedCount = 0;
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    boolean removed = CounterHelperUtil.removeCounter(player.getUuid(), countername, server);
                    if (removed) {
                        source.sendFeedback(() -> Text.literal(
                            "Removed counter '" + countername + "'."
                        ).styled(style -> style.withColor(Formatting.YELLOW)), true);
                        removedCount++;
                    }
                }
                if (removedCount == 0) {
                    source.sendError(Text.literal("Counter not found: " + countername + "!"));
                    return 0;
                }
                return 1;
            })
        )
    )
    );
    }

    /**
     * Registers the /getmoditem command.
     * Usage:
     *   /getmoditem <item> [player] (defaults to arythings namespace if not specified)
     *   /getmoditem <namespace>:<item> [player] (namespace must NOT be minecraft)
     */
    public static void registerGetModItemCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("getmoditem")
            .requires(source -> source.hasPermissionLevel(2)) // OPs only
            .then(CommandManager.argument("players", EntityArgumentType.players())
                .then(CommandManager.argument("item", StringArgumentType.word()).suggests(ModCommands::suggestModItems)
                    .executes(context -> {
                        return giveModItem(context.getSource(), 
                        EntityArgumentType.getPlayers(context, "players"), 
                        StringArgumentType.getString(context, "item"), 
                        1);
                    })
                    .then(CommandManager.argument("amount", IntegerArgumentType.integer(1)))
                    .executes(context -> {
                        return giveModItem(context.getSource(), 
                        EntityArgumentType.getPlayers(context, "players"), 
                        StringArgumentType.getString(context, "item"), 
                        IntegerArgumentType.getInteger(context, "amount"));
                    })
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
            id = Identifier.of(input);
            if (id == null) {
                source.sendError(Text.literal("Invalid item identifier: " + input));
                return 0;
            }
            if (id.getNamespace().equals("minecraft")) {
                source.sendError(Text.literal("Cannot use /getmoditem for Minecraft item: " + id + "! Use /give instead."));
                return 0;
            }
        } else {
            id = Identifier.of("arythings", input);
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

    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("search")
        .then(CommandManager.argument("something", StringArgumentType.string())));
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
                .getOrDefault(player.getUuid(), java.util.Collections.emptyMap())
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

    private static CompletableFuture<Suggestions> suggestModItems(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        Registries.ITEM.stream()
            .filter(item -> {
                Identifier id = Registries.ITEM.getId(item);
                // Only suggest non-minecraft items
                return !id.getNamespace().equals("minecraft");
            })
            .map(item -> Registries.ITEM.getId(item).toString())
            .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
