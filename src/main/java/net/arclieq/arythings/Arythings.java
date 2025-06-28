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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
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
import java.util.HashMap;
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

    // --- Global Configuration Values ---
    // Changed to Map<String, JsonObject> to store item ID and its properties directly as JSON objects.
    private static Map<String, JsonObject> bannedItems = new HashMap<>();
    private static int maxCounterValue = 32767;
    private static Set<String> unchangeableCounters = new HashSet<>();

    /**
     * Called when the mod is initialized.
     * Registers server lifecycle events and calls registerMod() for further setup.
     */
    @Override
    public void onInitialize() {
        LOGGER.info("Loading mod, please wait!");
        
        // Load all configurations from a single, unified method
        loadAndSyncConfig();

        // Register server lifecycle events
        LOGGER.info("Loading events, please wait.");
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
        LOGGER.info("Loading mod context...");
        registerMod();
        LOGGER.info("Finished!");
    }

    /**
     * Loads all configurations from arythings.json, creates the file with defaults if it doesn't exist,
     * and adds any missing settings to an existing file. This is the single source of truth for config management.
     */
    private static void loadAndSyncConfig() {
        // Ensure the config directory exists
        File configDir = Paths.get("config").toFile();
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        LOGGER.info("Loading config...");
        File configFile = Paths.get("config", "arythings.json").toFile();
        JsonObject config;
        boolean needsSave = false;

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                config = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (Exception e) {
                LOGGER.debug("Could not parse arythings.json, creating a new one.", e);
                config = new JsonObject();
                needsSave = true;
            }
        } else {
            config = new JsonObject();
            needsSave = true;
        }

        // --- 1. Handle banned_items ---
        if (!config.has("banned_items") || !config.get("banned_items").isJsonArray()) {
            config.add("banned_items", new JsonArray());
            needsSave = true;
        }
        
        // Clear existing banned items before loading
        bannedItems.clear(); 
        JsonArray bannedItemsArray = config.getAsJsonArray("banned_items");
        for (int i = 0; i < bannedItemsArray.size(); i++) {
            JsonObject bannedItemObj = null;
            String itemId = null;

            // Support old config format (just string) and new format (object)
            if (bannedItemsArray.get(i).isJsonObject()) {
                bannedItemObj = bannedItemsArray.get(i).getAsJsonObject();
                itemId = bannedItemObj.has("item_id") ? bannedItemObj.get("item_id").getAsString() : null;
            } else if (bannedItemsArray.get(i).isJsonPrimitive()) {
                // Handle old string-only format: convert to new object format with defaults
                itemId = bannedItemsArray.get(i).getAsString();
                bannedItemObj = new JsonObject();
                bannedItemObj.addProperty("item_id", itemId);
                bannedItemObj.addProperty("affects_creative", true); // Default to true for old format
                bannedItemObj.addProperty("affects_operators", true); // Default to true for old format
                needsSave = true; // Mark for save to update format
            } else {
                needsSave = true; // Mark for save to clean up malformed entries
            }

            if (itemId != null && !itemId.isEmpty()) {
                // Ensure default values for new properties if they are missing in existing objects
                if (!bannedItemObj.has("affects_creative")) {
                    bannedItemObj.addProperty("affects_creative", true);
                    needsSave = true;
                }
                if (!bannedItemObj.has("affects_operators")) {
                    bannedItemObj.addProperty("affects_operators", true);
                    needsSave = true;
                }
                bannedItems.put(itemId, bannedItemObj);
            } else if (bannedItemObj != null) {
                needsSave = true; // Potentially save to clean up malformed entries
            }
        }


        // --- 2. Handle maxCounterValue ---
        if (!config.has("maxCounterValue")) {
            config.addProperty("maxCounterValue", 32767);
            needsSave = true;
        }
        maxCounterValue = config.get("maxCounterValue").getAsInt();

        // --- 3. Handle unchangeable_counters ---
        if (!config.has("unchangeable_counters")) {
            JsonObject unchangeableDefaults = new JsonObject();
            unchangeableDefaults.add("counter_names", new JsonArray());
            config.add("unchangeable_counters", unchangeableDefaults);
            needsSave = true;
        }
        JsonObject unchangeableCountersObj = config.getAsJsonObject("unchangeable_counters");
        if (!unchangeableCountersObj.has("counter_names") || !unchangeableCountersObj.get("counter_names").isJsonArray()) {
            unchangeableCountersObj.add("counter_names", new JsonArray());
            needsSave = true;
        }
        JsonArray counterNamesArray = unchangeableCountersObj.getAsJsonArray("counter_names");
        unchangeableCounters.clear();
        counterNamesArray.forEach(element -> unchangeableCounters.add(element.getAsString()));
        

        // --- Save file if any defaults were added or format updated ---
        if (needsSave) {
            saveConfig(config);
        }

        // --- Sync loaded config values to other utility classes ---
        CounterHelperUtil.applyConfig(maxCounterValue, unchangeableCounters);
    }

    /**
     * Adds a new item to the banned list and saves the configuration.
     * This is the safe, centralized way to modify the config from other parts of the mod.
     * @param itemId The full string ID of the item to ban (e.g., "minecraft:diamond").
     * @param affectsCreative True if this banned item should be removed from players in creative mode.
     * @param affectsOperators True if this banned item should be removed from operators.
     */
    public static void addBannedItemAndSave(String itemId, boolean affectsCreative, boolean affectsOperators) {
        JsonObject itemConfig = new JsonObject();
        itemConfig.addProperty("item_id", itemId);
        itemConfig.addProperty("affects_creative", affectsCreative);
        itemConfig.addProperty("affects_operators", affectsOperators);

        // Check if item already exists with different settings or is new
        JsonObject existingConfig = bannedItems.get(itemId);
        if (existingConfig == null || 
            existingConfig.get("affects_creative").getAsBoolean() != affectsCreative || 
            existingConfig.get("affects_operators").getAsBoolean() != affectsOperators) {
            
            bannedItems.put(itemId, itemConfig);
            saveCurrentConfigState();
        }
    }

    /**
     * Removes an item from the banned list and saves the configuration.
     * This is the safe, centralized way to modify the config from other parts of the mod.
     * @param itemId The full string ID of the item to unban (e.g., "minecraft:diamond").
     */
    public static void removeBannedItemAndSave(String itemId) {
        if (bannedItems.remove(itemId) != null) { // .remove() returns the previous value or null if not found
            saveCurrentConfigState();
        }
    }

    /**
     * Helper method to save the current state of all configuration variables to the file.
     */
    private static void saveCurrentConfigState() {
        JsonObject rootObj = new JsonObject();

        // Add banned_items
        JsonArray bannedItemsJsonArray = new JsonArray();
        for (JsonObject configObj : bannedItems.values()) {
            bannedItemsJsonArray.add(configObj);
        }
        rootObj.add("banned_items", bannedItemsJsonArray);

        // Add other settings
        rootObj.addProperty("maxCounterValue", maxCounterValue);
        JsonObject unchangeableCountersObj = new JsonObject();
        JsonArray unchangeableNames = new JsonArray();
        unchangeableCounters.forEach(unchangeableNames::add);
        unchangeableCountersObj.add("counter_names", unchangeableNames);
        rootObj.add("unchangeable_counters", unchangeableCountersObj);

        saveConfig(rootObj);
    }


    /**
     * Provides access to the set of banned item identifiers.
     * Item identifiers are in the format "namespace:path" (e.g., "minecraft:diamond_sword").
     *
     * @return an unmodifiable set of banned item identifiers.
     */
    public static Set<String> getBannedItems() {
        // Return the key set of the map, which represents the item IDs
        return Collections.unmodifiableSet(bannedItems.keySet());
    }

    /**
     * Retrieves the JSON object configuration for a given item ID.
     * @param itemId The ID of the item.
     * @return The JsonObject containing the item's config (e.g., affects_creative, affects_operators), or null if not found.
     */
    public static JsonObject getBannedItemProperties(String itemId) {
        return bannedItems.get(itemId);
    }

    private static void saveConfig(JsonObject config) {
        Gson GSON = new GsonBuilder().setPrettyPrinting().create();
        File configFile = Paths.get("config", "arythings.json").toFile();
        try {
            Files.write(configFile.toPath(), GSON.toJson(config).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            LOGGER.debug("Failed to write to config file.", e);
        }
    }

    /**
     * Registers commands, player join logic, and mod content.
     */
    private void registerMod() {
        // Register custom commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {ModCommands.registerCommands(dispatcher);});

        // On player join, initialize tick counters to 20000 if this is their first join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            onPlayerJoin(server, handler.getPlayer());
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
     * Ensures specific default counters exist for a joining player.
     * If a player does not have a required counter, it is initialized to its default value.
     * This method runs for every player join, guaranteeing counter presence.
     *
     * @param server The Minecraft server instance.
     * @param player The ServerPlayerEntity that just joined.
     */
    private void onPlayerJoin(MinecraftServer server, ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        
        // Define the list of counters and their default values/modes that every player should have.
        // This ensures that even if a player exists but somehow misses these counters (e.g., new mod version),
        // they will be initialized upon join.
        Map<String, CounterMode> requiredCounters = new HashMap<>();
        requiredCounters.put("luzzantum", CounterMode.TICK);
        requiredCounters.put("netiamond", CounterMode.TICK);
        
        // Default value for TICK mode counters (20000 as per previous logic)
        int defaultTickValue = 20000; 

        // Iterate through the required counters and ensure they exist for the player
        for (Map.Entry<String, CounterMode> entry : requiredCounters.entrySet()) {
            String counterName = entry.getKey();
            CounterMode defaultMode = entry.getValue();

            // Check if the player already has this specific counter
            if (!CounterHelperUtil.hasCounter(uuid, counterName)) {
                // If not, add it with its default mode and value
                int valueToSet = 0; // Default for MANUAL/CUSTOM
                if (defaultMode == CounterMode.TICK) {
                    valueToSet = defaultTickValue;
                }
                CounterHelperUtil.setCounter(uuid, counterName, valueToSet, defaultMode, server);
            }
        }
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
        if (!bannedItems.isEmpty()) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                for (int i = 0; i < player.getInventory().size(); i++) {
                    Identifier currentItemIdentifier = net.minecraft.registry.Registries.ITEM.getId(player.getInventory().getStack(i).getItem());
                    String currentItemString = currentItemIdentifier.toString();
                    
                    // Get the JSON object for the banned item's properties
                    JsonObject configJson = bannedItems.get(currentItemString);

                    // If the item is banned, apply the rules based on config
                    if (configJson != null) {
                        boolean affectsCreative = configJson.has("affects_creative") ? configJson.get("affects_creative").getAsBoolean() : true; // Default to true
                        boolean affectsOperators = configJson.has("affects_operators") ? configJson.get("affects_operators").getAsBoolean() : true; // Default to true

                        boolean shouldRemove = true; // Assume removal unless conditions prevent it

                        // Check if player is creative and if the item affects creative players
                        if (player.isCreative() && !affectsCreative) {
                            shouldRemove = false;
                        }
                        // Check if player is an operator (permission level 2 or higher) and if the item affects operators
                        // Note: A player can be both creative and an operator. We apply OR logic.
                        if (player.hasPermissionLevel(2) && !affectsOperators) {
                            shouldRemove = false;
                        }

                        if (shouldRemove) {
                            player.getInventory().setStack(i, net.minecraft.item.ItemStack.EMPTY);
                            player.sendMessage(Text.literal("Banned item(s) has been removed from your inventory.").formatted(Formatting.RED), true);
                        }
                    }
                }
            }
        }
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
