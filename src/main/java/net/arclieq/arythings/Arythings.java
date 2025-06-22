package net.arclieq.arythings;

import net.arclieq.arythings.block.ModBlocks;
import net.arclieq.arythings.item.ModItemGroups;
import net.arclieq.arythings.item.ModItems;
import net.arclieq.arythings.util.CounterHelperUtil;
import net.arclieq.arythings.world.gen.ModWorldGeneration;
import net.arclieq.arythings.command.ModCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.UUID;

/**
 * The main class of the Arythings mod.
 * Handles initialization, command registration, tick data management, and player join logic.
 */
public class Arythings implements ModInitializer {
    // Last changed: 20/06/2025
    // AI helped me a LOT with the code and comments, and even had suggestions for the comments/code!
    // More specifically, GPT-4.1 and Gemini 2.0 Flash made most of the code and comments!
    // (most of the counter code was made by GPT-4.1!)

    // Chat reminder (for vscode me): When a chat has a '*' it means that it is important, DO NOT* remove it.
    // *If the code in the chat is already done/finished, you can remove the chat.

    /* TODO LIST:
     * - Add custom enchants (25/07/2025)
     * - Add upgraded mace usages (01/08/2025)
     * - Add luzzantum right click methods (01/08/2025)
     * - Add nether ore textures, upgraded mace textures, new ores, blocks, and items textures (01/08/2025)
     */
    public static final String MOD_ID = "arythings";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Formatting constants for tooltips and messages
    public static final Formatting 
        GRAY = Formatting.GRAY, 
        GREEN = Formatting.GREEN, 
        RED = Formatting.RED, 
        AQUA = Formatting.AQUA;

    // Counter for periodic tick data saving
    private int tickSaveCounter = 0;

    /**
     * Called when the mod is initialized.
     * Registers server lifecycle events and calls registerMod() for further setup.
     */
    @Override
    public void onInitialize() {
        // Register server lifecycle events
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
        LOGGER.debug("Server-side events registered.");
        LOGGER.debug("Now loading commands and mod context, please wait!");

        LOGGER.info("Mod is loading...");
        loadConfig();
        LOGGER.warn("Warning: Possibly buggy/broken mod!");
        registerMod();
        LOGGER.debug("Loaded all registries.");
        LOGGER.info("Finished!");
    }

    /**
     * Loads the max counter value from config file.
     * Config file: config/arythings.json
     */
    public static void loadConfig() {
        LOGGER.info("Loading config...");
        try {
            File configFile = Paths.get("config", "arythings.json").toFile();
            File configDir = configFile.getParentFile();
            if (!configDir.exists()) {
                boolean made = configDir.mkdirs();
                if (!made) {
                    LOGGER.error("Failed to create config directory: " + configDir.getAbsolutePath());
                }
            }
            if (!configFile.exists()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("maxCounterValue", 32767);
                try {
                    Files.write(configFile.toPath(), CounterHelperUtil.GSON.toJson(obj).getBytes(), StandardOpenOption.CREATE_NEW);
                    LOGGER.info("Created config at " + configFile.getAbsolutePath());
                } catch (IOException e) {
                    LOGGER.error("Failed to write config: ", e);
                }
                CounterHelperUtil.maxCounterValue = 32767;
            }
        } catch (Exception e) {
            LOGGER.error("Error loading config, using default.", e);
            CounterHelperUtil.maxCounterValue = 32767;
        }
    }

    /**
     * Registers commands, player join logic, and mod content.
     */
    private void registerMod() {
        // Register custom commands
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                ModCommands.registerCommands(dispatcher);
            }
        );
        LOGGER.debug("Registered commands.");

        // On player join, initialize tick counters to 20000 if this is their first join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            UUID uuid = player.getUuid();
            // List of tick counters to initialize
            String[] counters = {"luzzantum", "netiamond"};
            boolean isFirstJoin = true;
            // Check if any counter is already set (not first join)
            LOGGER.debug("Checking if player(s) are first-join...");
            for (String counter : counters) {
                if (CounterHelperUtil.getCounterValue(uuid, counter, server) != 0) {
                    isFirstJoin = false;
                    break;
                }
            }
            // If first join, set all counters to 20000
            if (isFirstJoin) {
                LOGGER.debug("First-join player detected, setting counter(s) to starting value!");
                for (String counter : counters) {
                    CounterHelperUtil.setCounter(uuid, counter, 20000, CounterHelperUtil.CounterMode.TICK, server);
                }
            }
        });
        LOGGER.debug("Registered player first-join event.");

        // Register custom items
        ModItems.registerModItems();
        // Register custom blocks
        ModBlocks.registerModBlocks();
        // Register custom item groups.
        ModItemGroups.registerItemGroups();
        // Register world generation (sounds decieving since it says generate, but it just registers it, not generate it yet)
        ModWorldGeneration.generateModWorldGen();
    }

    /**
     * Called when the server starts.
     * Loads tick data from file.
     */
    private void onServerStarted(MinecraftServer server) {
        CounterHelperUtil.loadData(server);
    }

    /**
     * Called when the server is stopping.
     * Saves tick data to file.
     */
    private void onServerStopping(MinecraftServer server) {
        CounterHelperUtil.saveData(server);
    }

    /**
     * Called every server tick.
     * Periodically saves tick data to file (every 5 minutes).
     */
    private void onServerTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            UUID uuid = player.getUuid();
            Map<String, CounterHelperUtil.CounterData> counters = CounterHelperUtil.getAllPlayerCounters().get(uuid);
            if (counters != null) {
                for (Map.Entry<String, CounterHelperUtil.CounterData> entry : counters.entrySet()) {
                    CounterHelperUtil.CounterData data = entry.getValue();
                    if (data.mode == CounterHelperUtil.CounterMode.TICK) {
                        if(data.value < CounterHelperUtil.getMaxCounterValue()) data.value++;
                    }
                }
            }
        }
        tickSaveCounter++;
        // 20 ticks/sec * 60 sec/min * 5 min = 6000 ticks (5 minutes)
        if (tickSaveCounter >= 20 * 60 * 5) {
            CounterHelperUtil.saveData(server);
            tickSaveCounter = 0;
        }
    }
}
