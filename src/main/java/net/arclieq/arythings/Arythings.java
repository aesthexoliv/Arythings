package net.arclieq.arythings;

import net.arclieq.arythings.block.ModBlocks;
import net.arclieq.arythings.item.ModItemGroups;
import net.arclieq.arythings.item.ModItems;
import net.arclieq.arythings.util.CounterHelperUtil;
import net.arclieq.arythings.util.CounterHelperUtil.CounterMode;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

    // Global configuration values for getmoditem_settings
    private static Set<String> blockedItems = new HashSet<>();


    /**
     * Called when the mod is initialized.
     * Registers server lifecycle events and calls registerMod() for further setup.
     */
    @Override
    public void onInitialize() {
        LOGGER.info("Loading mod, please wait!");
        
        // Load Arythings specific configurations (getmoditem_settings)
        loadConfig();
        // Load CounterHelperUtil specific configurations (maxCounterValue, unchangeable_counters)
        CounterHelperUtil.configureConfig(); // Renamed method call

        // Register server lifecycle events
        LOGGER.debug("Registering server lifecycle events, please wait!");
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
        LOGGER.debug("Now loading mod context...");
        registerMod();
        LOGGER.info("Finished!");
    }

    /**
     * Loads Arythings specific configuration from arythings.json.
     * This includes blocked_items for /getmoditem command.
     */
    private static void loadConfig() {
        // Ensure the config directory exists
        File configDir = Paths.get("config").toFile();
        if (!configDir.exists()) {
            boolean made = configDir.mkdirs();
            if (!made) {
                LOGGER.error("Failed to create config directory: " + configDir.getAbsolutePath());
            }
        }

        LOGGER.info("Loading config...");
        Gson GSON = new GsonBuilder().setPrettyPrinting().create();
        File configFile = Paths.get("config", "arythings.json").toFile();

        // If file doesn't exist, create it with default structure
        if (!configFile.exists()) {
            JsonObject rootObj = new JsonObject();
            
            // Default getmoditem_settings
            JsonObject getModItemSettings = new JsonObject();
            JsonArray blockedItemsArray = new JsonArray();
            // Example blocked item: blockedItemsArray.add("minecraft:diamond");
            blockedItemsArray.add("arythings:luzzantum_ingot"); // Example blocked item for testing
            getModItemSettings.add("blocked_items", blockedItemsArray);
            rootObj.add("getmoditem_settings", getModItemSettings);

            try {
                Files.write(configFile.toPath(), GSON.toJson(rootObj).getBytes(), StandardOpenOption.CREATE_NEW);
                LOGGER.info("Created default arythings.json.");
            } catch (IOException e) {
                LOGGER.error("Failed to write default arythings.json: ", e);
            }
        }

        // Read existing config
        try (FileReader reader = new FileReader(configFile)) {
            JsonObject config = JsonParser.parseReader(reader).getAsJsonObject();

            // Load getmoditem_settings
            if (config.has("getmoditem_settings") && config.get("getmoditem_settings").isJsonObject()) {
                JsonObject getModItemSettings = config.getAsJsonObject("getmoditem_settings");
                if (getModItemSettings.has("blocked_items") && getModItemSettings.get("blocked_items").isJsonArray()) {
                    JsonArray blockedItemsArray = getModItemSettings.getAsJsonArray("blocked_items");
                    blockedItems.clear(); // Clear existing to ensure fresh load
                    blockedItemsArray.forEach(element -> blockedItems.add(element.getAsString()));
                } else {
                    blockedItems.clear();
                }
            } else {
                blockedItems.clear();
            }

        } catch (IOException e) {
            LOGGER.error("Error reading arythings.json config file: ", e);
            blockedItems.clear();
        } catch (Exception e) {
            LOGGER.error("Error parsing arythings.json config, using defaults: ", e);
            blockedItems.clear();
        }
    }

    /**
     * Provides access to the set of blocked item identifiers for /getmoditem command.
     * Item identifiers are in the format "namespace:path" (e.g., "minecraft:diamond_sword").
     *
     * @return an unmodifiable set of blocked item identifiers.
     */
    public static Set<String> getBlockedItems() {
        return Collections.unmodifiableSet(blockedItems);
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

        // On player join, initialize tick counters to 20000 if this is their first join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            UUID uuid = player.getUuid();
            // List of tick counters to initialize
            String[] counters = {"luzzantum", "netiamond"};
            boolean isFirstJoin = true;
            // Check if any counter is already set (not first join)
            for (String counter : counters) {
                if (CounterHelperUtil.getCounterValue(uuid, counter, server) != 0) {
                    isFirstJoin = false;
                    break;
                }
            }
            // If first join, set all counters to 20000
            if (isFirstJoin) {
                for (String counter : counters) {
                    CounterHelperUtil.setCounter(uuid, counter, 20000, CounterHelperUtil.CounterMode.TICK, server);
                }
            }
        });

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
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            for (String counter : CounterHelperUtil.getAllPlayerCounters().getOrDefault(player.getUuid(), Collections.emptyMap()).keySet()) {
                // Now using CounterHelperUtil.getMaxCounterValue()
                if(CounterHelperUtil.getCounterValue(player.getUuid(), counter, server) > CounterHelperUtil.getMaxCounterValue()) {
                    CounterMode mode = CounterHelperUtil.getCounterMode(player.getUuid(), counter);
                    CounterHelperUtil.setCounter(player.getUuid(), counter, CounterHelperUtil.getMaxCounterValue(), mode, server);
                }
            }
        }
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
                        // Now using CounterHelperUtil.getMaxCounterValue()
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