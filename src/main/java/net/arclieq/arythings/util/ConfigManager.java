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
 * Manages the configuration for Arythings' ban settings and counter settings.
 * Configuration is stored in 'config/arythings.json'.
 * <p>Legend:</p>
 * <p>bI -> bannedItems, mCV -> maxCounterValue, uC -> unchangeableCounters, aO -> affectsOperators, aC -> affectsCreative.</p>
 */
public class ConfigManager {
    public static Set<String> bI = new HashSet<>();
    private static int mCV = 32767;
    private static Set<String> uC = new HashSet<>();
    private static boolean aO = false;
    private static boolean aC = true;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG = "arythings.json";

    /**
     * Loads configurations from 'arythings.json'. If the file doesn't exist
     * or is invalid, a new one is created with default settings.
     * Missing settings in an existing file are also added.
     */
    public static void loadAndSyncConfig() {
        /*
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
        // configDir is only used once (I'm pretty sure...?), so just use configDir instead of cD
        // And also, it *would* be pretty confusing if it was cD...
        File configDir = Paths.get("config").toFile();
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        Arythings.LOGGER.info("Loading configuration...");
        // This 'File' configFile is an exception (no need for cF),
        // because configFile is only used for Files.write and FileReader in this method. a.k.a, it *should* be fine
        File configFile = Paths.get("config", CONFIG).toFile();
        // Just use config instead, using only c.(...) will be confusing IMO.
        JsonObject config;
        // needsSave -> nS, it is used alot, and if I just use needsSave,
        // it would be fine as well but it's way more easier to use 'nS = true;'
        boolean nS = false;

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                JsonElement parsed = JsonParser.parseReader(reader);
                if (parsed.isJsonObject()) {
                    config = parsed.getAsJsonObject();
                } else {
                    config = new JsonObject();
                    nS = true;
                }
            } catch (Exception e) {
                Arythings.LOGGER.debug("Could not parse " + CONFIG + ", creating a new one.", e);
                config = new JsonObject();
                nS = true;
            }
        } else {
            config = new JsonObject();
            nS = true;
        }

        // Handling counter_settings, with mCV and uC being added. counterSettingsObj -> cSO
        JsonObject cSO;
        if (!config.has("counter_settings") || !config.get("counter_settings").isJsonObject()) {
            cSO = new JsonObject();
            config.add("counter_settings", cSO);
            nS = true;
        } else {
            cSO = config.getAsJsonObject("counter_settings");
        }

        if (!cSO.has("maxCounterValue")) {
            cSO.addProperty("maxCounterValue", 32767);
            nS = true;
        }
        mCV = cSO.get("maxCounterValue").getAsInt();
        Arythings.LOGGER.debug("Loading maxCounterValue: " + mCV);

        if (!cSO.has("unchangeable_counters") || !cSO.get("unchangeable_counters").isJsonArray()) {
            cSO.add("unchangeable_counters", new JsonArray());
            nS = true;
        }

        // Find "netiamond" and "luzzantum" in config and add if not found; unchangeableCounterNamesArray -> ucNA 
        JsonArray ucNA = cSO.getAsJsonArray("unchangeable_counters");
        boolean foundNetiamond = false;
        boolean foundLuzzantum = false;
        for (JsonElement element : ucNA) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                String countername = element.getAsString();
                if ("netiamond".equals(countername)) {
                    foundNetiamond = true;
                }
                if ("luzzantum".equals(countername)) {
                    foundLuzzantum = true;
                }
            } else {
                nS = true;
            }
        }
        if (!foundNetiamond) {
            ucNA.add("netiamond");
            nS = true;
        }
        if (!foundLuzzantum) {
            ucNA.add("luzzantum");
            nS = true;
        }

        uC.clear();
        ucNA.forEach(element -> {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                uC.add(element.getAsString());
            }
        });

        // banSettingsObj -> bSO
        JsonObject bSO;
        if (!config.has("ban_settings") || !config.get("ban_settings").isJsonObject()) {
            bSO = new JsonObject();
            config.add("ban_settings", bSO);
            nS = true;
        } else {
            bSO = config.getAsJsonObject("ban_settings");
        }

        if (!bSO.has("affects_creative")) {
            bSO.addProperty("affects_creative", true);
            nS = true;
        }
        aC = bSO.get("affects_creative").getAsBoolean();
        Arythings.LOGGER.debug("Loading affectsCreative: " + aC);

        if (!bSO.has("affects_operators")) {
            bSO.addProperty("affects_operators", false);
            nS = true;
        }
        aO = bSO.get("affects_operators").getAsBoolean();
        Arythings.LOGGER.debug("Loading affectsOperators: " + aO);


        if (!bSO.has("banned_items") || !bSO.get("banned_items").isJsonArray()) {
            bSO.add("banned_items", new JsonArray());
            nS = true;
        }

        bI.clear();
        JsonArray bIArray = bSO.getAsJsonArray("banned_items");
        for (JsonElement element : bIArray) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                bI.add(element.getAsString());
            } else {
                nS = true;
            }
        }
        Arythings.LOGGER.debug("Loading banned_items count: " + bI.size());

        if (nS) {
            saveConfig(config);
        }

        CounterHelperUtil.applyConfig(mCV, uC);
        Arythings.LOGGER.info("Configuration loaded successfully!");
    }

    /**
     * Adds a new item to the banned list and saves the configuration.
     * @param itemId The full string ID of the item to ban (e.g., "minecraft:diamond").
     */
    public static void addBannedItem(String itemId) {
        if (!bI.contains(itemId)) {
            bI.add(itemId);
            saveConfigState();
        }
    }

    /**
     * Removes an item from the banned list and saves the configuration.
     * @param itemId The full string ID of the item to unban (e.g., "minecraft:diamond").
     */
    public static void removeBannedItem(String itemId) {
        if (bI.remove(itemId)) {
            saveConfigState();
        }
    }

    /**
     * Helper method to save the current state of all configuration variables to the file.
     */
    private static void saveConfigState() {
        // rootObj -> rO
        JsonObject rO = new JsonObject();

        // counterSettingsObj -> cSO
        JsonObject cSO = new JsonObject();
        cSO.addProperty("maxCounterValue", mCV);

        // unchangeableNames -> uN
        JsonArray uN = new JsonArray();
        uC.forEach(uN::add);
        cSO.add("unchangeable_counters", uN);
        rO.add("counter_settings", cSO);

        // banSettingsObj -> bSO
        JsonObject bSO = new JsonObject();
        bSO.addProperty("affects_creative", aC);
        bSO.addProperty("affects_operators", aO);

        // bannedItemsJsonArray -> bIA
        JsonArray bIA = new JsonArray();
        bI.forEach(bIA::add);
        bSO.add("banned_items", bIA);
        rO.add("ban_settings", bSO);

        saveConfig(rO);
    }

    /**
     * Provides access to the set of banned item identifiers.
     * Item identifiers are in the format "namespace:path" (e.g., "minecraft:diamond_sword").
     * @return An unmodifiable set of banned item identifiers.
     */
    public static Set<String> getBannedItems() {
        return Collections.unmodifiableSet(bI);
    }

    /**
     * Retrieves the setting for whether banned items affect operators.
     * @return True if banned items affect operators by default, false otherwise.
     */
    public static boolean getDefaultAffectsOperators() {
        return aO;
    }

    /**
     * Retrieves the setting for whether banned items affect creative players.
     * @return True if banned items affect creative players by default, false otherwise.
     */
    public static boolean getDefaultAffectsCreative() {
        return aC;
    }

    /**
     * Retrieves the current maximum counter value.
     * @return The maximum value a counter can reach.
     */
    public static int getMaxCounterValue() {
        return mCV;
    }

    /**
     * Provides access to the set of unchangeable counter names.
     * @return An unmodifiable set of unchangeable counter names.
     */
    public static Set<String> getUnchangeableCounters() {
        return Collections.unmodifiableSet(uC);
    }

    /**
     * Internal helper to save the given JsonObject to the config file.
     * @param config The JsonObject representing the entire mod configuration.
     */
    private static void saveConfig(JsonObject config) {
        // This 'File' configFile is an exception (no need for cF),
        // Because configFile is only used for Files.write in this method. a.k.a, it *should* be fine
        File configFile = Paths.get("config", CONFIG).toFile();
        try {
            Files.write(configFile.toPath(), GSON.toJson(config).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            Arythings.LOGGER.debug("Error: ", e);
        }
    }
}
