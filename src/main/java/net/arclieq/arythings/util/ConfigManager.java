package net.arclieq.arythings.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.arclieq.arythings.Arythings; // Import Arythings for its LOGGER

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages the mod's configuration for banned items and counter settings.
 * Configuration is stored in 'config/arythings.json'.
 */
public class ConfigManager {

    // Internal storage for global configuration values
    public static Set<String> bannedItems = new HashSet<>();
    private static int maxCounterValue = 32767;
    private static Set<String> unchangeableCounters = new HashSet<>();
    private static boolean defaultAffectsOperators = false;
    private static boolean defaultAffectsCreative = true;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FILE_NAME = "arythings.json";

    /**
     * Loads configurations from 'arythings.json'. If the file doesn't exist
     * or is invalid, a new one is created with default settings.
     * Missing settings in an existing file are also added.
     *
     * Configuration structure:
     * {
     * "counter_settings": {
     * "maxCounterValue": integer,
     * "unchangeable_counters": ["counter_name1", "counter_name2"]
     * },
     * "ban_settings": {
     * "affects_creative": boolean,
     * "affects_operators": boolean,
     * "banned_items": ["item_id1", "item_id2"]
     * }
     * }
     */
    public static void loadAndSyncConfig() {
        File configDir = Paths.get("config").toFile();
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        Arythings.LOGGER.info("Loading mod configuration...");
        File configFile = Paths.get("config", CONFIG_FILE_NAME).toFile();
        JsonObject config;
        boolean needsSave = false;

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                JsonElement parsed = JsonParser.parseReader(reader);
                if (parsed.isJsonObject()) {
                    config = parsed.getAsJsonObject();
                } else {
                    config = new JsonObject();
                    needsSave = true;
                }
            } catch (Exception e) {
                Arythings.LOGGER.debug("Could not parse " + CONFIG_FILE_NAME + ", creating a new one.", e);
                config = new JsonObject();
                needsSave = true;
            }
        } else {
            config = new JsonObject();
            needsSave = true;
        }

        JsonObject counterSettingsObj;
        if (!config.has("counter_settings") || !config.get("counter_settings").isJsonObject()) {
            counterSettingsObj = new JsonObject();
            config.add("counter_settings", counterSettingsObj);
            needsSave = true;
        } else {
            counterSettingsObj = config.getAsJsonObject("counter_settings");
        }

        if (!counterSettingsObj.has("maxCounterValue")) {
            counterSettingsObj.addProperty("maxCounterValue", 32767);
            needsSave = true;
        }
        maxCounterValue = counterSettingsObj.get("maxCounterValue").getAsInt();
        Arythings.LOGGER.debug("Loading maxCounterValue: " + maxCounterValue);

        if (!counterSettingsObj.has("unchangeable_counters") || !counterSettingsObj.get("unchangeable_counters").isJsonArray()) {
            counterSettingsObj.add("unchangeable_counters", new JsonArray());
            needsSave = true;
        }
        JsonArray unchangeableCounterNamesArray = counterSettingsObj.getAsJsonArray("unchangeable_counters");

        boolean foundNetiamond = false;
        boolean foundLuzzantum = false;
        for (JsonElement element : unchangeableCounterNamesArray) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                String countername = element.getAsString();
                if ("netiamond".equals(countername)) {
                    foundNetiamond = true;
                }
                if ("luzzantum".equals(countername)) {
                    foundLuzzantum = true;
                }
            } else {
                needsSave = true;
            }
        }
        if (!foundNetiamond) {
            unchangeableCounterNamesArray.add("netiamond");
            needsSave = true;
        }
        if (!foundLuzzantum) {
            unchangeableCounterNamesArray.add("luzzantum");
            needsSave = true;
        }

        unchangeableCounters.clear();
        unchangeableCounterNamesArray.forEach(element -> {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                unchangeableCounters.add(element.getAsString());
            }
        });

        JsonObject banSettingsObj;
        if (!config.has("ban_settings") || !config.get("ban_settings").isJsonObject()) {
            banSettingsObj = new JsonObject();
            config.add("ban_settings", banSettingsObj);
            needsSave = true;
        } else {
            banSettingsObj = config.getAsJsonObject("ban_settings");
        }

        if (!banSettingsObj.has("affects_creative")) {
            banSettingsObj.addProperty("affects_creative", true);
            needsSave = true;
        }
        defaultAffectsCreative = banSettingsObj.get("affects_creative").getAsBoolean();
        Arythings.LOGGER.debug("Loading defaultAffectsCreative: " + defaultAffectsCreative);

        if (!banSettingsObj.has("affects_operators")) {
            banSettingsObj.addProperty("affects_operators", false);
            needsSave = true;
        }
        defaultAffectsOperators = banSettingsObj.get("affects_operators").getAsBoolean();
        Arythings.LOGGER.debug("Loading defaultAffectsOperators: " + defaultAffectsOperators);


        if (!banSettingsObj.has("banned_items") || !banSettingsObj.get("banned_items").isJsonArray()) {
            banSettingsObj.add("banned_items", new JsonArray());
            needsSave = true;
        }

        bannedItems.clear();
        JsonArray bannedItemsArray = banSettingsObj.getAsJsonArray("banned_items");
        for (JsonElement element : bannedItemsArray) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                bannedItems.add(element.getAsString());
            } else {
                needsSave = true;
            }
        }
        Arythings.LOGGER.debug("Loading banned_items count: " + bannedItems.size());

        if (needsSave) {
            saveConfig(config);
        }

        CounterHelperUtil.applyConfig(maxCounterValue, unchangeableCounters);
        Arythings.LOGGER.info("Configuration loaded successfully!");
    }

    /**
     * Adds a new item to the banned list and saves the configuration.
     * @param itemId The full string ID of the item to ban (e.g., "minecraft:diamond").
     */
    public static void addBannedItemAndSave(String itemId) {
        if (!bannedItems.contains(itemId)) {
            bannedItems.add(itemId);
            saveCurrentConfigState();
        }
    }

    /**
     * Removes an item from the banned list and saves the configuration.
     * @param itemId The full string ID of the item to unban (e.g., "minecraft:diamond").
     */
    public static void removeBannedItemAndSave(String itemId) {
        if (bannedItems.remove(itemId)) {
            saveCurrentConfigState();
        }
    }

    /**
     * Helper method to save the current state of all configuration variables to the file.
     */
    private static void saveCurrentConfigState() {
        JsonObject rootObj = new JsonObject();

        JsonObject counterSettingsObj = new JsonObject();
        counterSettingsObj.addProperty("maxCounterValue", maxCounterValue);
        JsonArray unchangeableNames = new JsonArray();
        unchangeableCounters.forEach(unchangeableNames::add);
        counterSettingsObj.add("unchangeable_counters", unchangeableNames);
        rootObj.add("counter_settings", counterSettingsObj);

        JsonObject banSettingsObj = new JsonObject();
        banSettingsObj.addProperty("affects_creative", defaultAffectsCreative);
        banSettingsObj.addProperty("affects_operators", defaultAffectsOperators);
        JsonArray bannedItemsJsonArray = new JsonArray();
        bannedItems.forEach(bannedItemsJsonArray::add);
        banSettingsObj.add("banned_items", bannedItemsJsonArray);
        rootObj.add("ban_settings", banSettingsObj);

        saveConfig(rootObj);
    }

    /**
     * Provides access to the set of banned item identifiers.
     * Item identifiers are in the format "namespace:path" (e.g., "minecraft:diamond_sword").
     * @return An unmodifiable set of banned item identifiers.
     */
    public static Set<String> getBannedItems() {
        return Collections.unmodifiableSet(bannedItems);
    }

    /**
     * Retrieves the global setting for whether banned items affect operators.
     * @return True if banned items affect operators by default, false otherwise.
     */
    public static boolean getDefaultAffectsOperators() {
        return defaultAffectsOperators;
    }

    /**
     * Retrieves the global setting for whether banned items affect creative players.
     * @return True if banned items affect creative players by default, false otherwise.
     */
    public static boolean getDefaultAffectsCreative() {
        return defaultAffectsCreative;
    }

    /**
     * Retrieves the current maximum counter value.
     * @return The maximum value a counter can reach.
     */
    public static int getMaxCounterValue() {
        return maxCounterValue;
    }

    /**
     * Provides access to the set of unchangeable counter names.
     * @return An unmodifiable set of unchangeable counter names.
     */
    public static Set<String> getUnchangeableCounters() {
        return Collections.unmodifiableSet(unchangeableCounters);
    }

    /**
     * Internal helper to save the given JsonObject to the config file.
     * @param config The JsonObject representing the entire mod configuration.
     */
    private static void saveConfig(JsonObject config) {
        File configFile = Paths.get("config", CONFIG_FILE_NAME).toFile();
        try {
            Files.write(configFile.toPath(), GSON.toJson(config).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            Arythings.LOGGER.debug("Error: ", e);
        }
    }
}
