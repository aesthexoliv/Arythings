package net.arclieq.arythings.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Collections; // Added for Collections.unmodifiableSet

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.arclieq.arythings.Arythings;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;

/**
 * Utility class for managing player counters with modes.
 */
public class CounterHelperUtil {

    // GSON instance for serializing/deserializing player counter data
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // File name for player counter data
    static final String COUNTERS_DATA_FILE_NAME = "counters.json";
    // File name for counter configuration
    static final String COUNTER_CONFIG_FILE_NAME = "counter_config.json";

    // Main data structure: player UUID -> (counter name -> CounterData)
    private static final Map<UUID, Map<String, CounterData>> playerCounters = new HashMap<>();

    // Global configuration values for counters, managed by CounterHelperUtil
    public static int maxCounterValue = 32767;
    private static Set<String> unchangeableCounters = new HashSet<>();

    // Enum for counter modes
    public enum CounterMode {
        MANUAL, TICK, CUSTOM
    }

    /**
     * Inner class to hold counter data (value and mode).
     */
    public static class CounterData {
        public int value;
        public CounterMode mode;

        public CounterData(int value, CounterMode mode) {
            this.value = value;
            this.mode = mode;
        }
    }

    /**
     * Loads CounterHelperUtil specific configuration from counter_config.json.
     * This includes maxCounterValue and unchangeable_counters.
     */
    public static void configureConfig() { // Renamed method
        // Ensure the config directory exists
        File configDir = Paths.get("config").toFile();
        if (!configDir.exists()) {
            boolean made = configDir.mkdirs();
            if (!made) {
                Arythings.LOGGER.error("Failed to create config directory: " + configDir.getAbsolutePath());
            }
        }

        Arythings.LOGGER.info("Loading config...");
        File configFile = Paths.get("config", COUNTER_CONFIG_FILE_NAME).toFile();

        // If file doesn't exist, create it with default structure
        if (!configFile.exists()) {
            JsonObject rootObj = new JsonObject();
            
            // Default maxCounterValue
            rootObj.addProperty("maxCounterValue", 32767);

            // Default unchangeable_counters
            JsonObject unchangeableDefaults = new JsonObject();
            unchangeableDefaults.add("counter_names", new JsonArray()); // Empty array for default
            rootObj.add("unchangeable_counters", unchangeableDefaults);

            try {
                Files.write(configFile.toPath(), GSON.toJson(rootObj).getBytes(), StandardOpenOption.CREATE_NEW);
                Arythings.LOGGER.info("Created default counter_config.json.");
            } catch (IOException e) {
                Arythings.LOGGER.error("Failed to write default counter_config.json: ", e);
            }
        }

        // Read existing config
        try (FileReader reader = new FileReader(configFile)) {
            JsonObject config = JsonParser.parseReader(reader).getAsJsonObject();

            // Load maxCounterValue
            if (config.has("maxCounterValue") && config.get("maxCounterValue").isJsonPrimitive()) {
                maxCounterValue = config.get("maxCounterValue").getAsInt();
            } else {
                // If not found or invalid, keep default
            }

            // Load unchangeable_counters
            if (config.has("unchangeable_counters") && config.get("unchangeable_counters").isJsonObject()) {
                JsonObject unchangeableCountersObj = config.getAsJsonObject("unchangeable_counters");
                if (unchangeableCountersObj.has("counter_names") && unchangeableCountersObj.get("counter_names").isJsonArray()) {
                    JsonArray counterNamesArray = unchangeableCountersObj.getAsJsonArray("counter_names");
                    unchangeableCounters.clear();
                    counterNamesArray.forEach(element -> unchangeableCounters.add(element.getAsString()));
                } else {
                    unchangeableCounters.clear();
                }
            } else {
                unchangeableCounters.clear();
            }

        } catch (IOException e) {
            Arythings.LOGGER.error("Error reading counter_config.json config file: ", e);
            maxCounterValue = 32767;
            unchangeableCounters.clear();
        } catch (Exception e) {
            Arythings.LOGGER.error("Error parsing counter_config.json config, using defaults: ", e);
            maxCounterValue = 32767;
            unchangeableCounters.clear();
        }
    }

    /**
     * Provides access to the maximum counter value.
     *
     * @return the maximum allowed value for counters.
     */
    public static int getMaxCounterValue() {
        return maxCounterValue;
    }

    /**
     * Provides access to the set of unchangeable counter names.
     *
     * @return an unmodifiable set of unchangeable counter names.
     */
    public static Set<String> getUnchangeableCounters() {
        return Collections.unmodifiableSet(unchangeableCounters);
    }

    /**
     * Gets the value of a counter for a player.
     */
    public static int getCounterValue(UUID playerUUID, String counterName, MinecraftServer server) {
        if (playerCounters.containsKey(playerUUID) && playerCounters.get(playerUUID).containsKey(counterName)) {
            CounterData data = playerCounters.get(playerUUID).get(counterName);
            // If mode is CUSTOM, retrieve from scoreboard, otherwise use stored value
            if (data.mode == CounterMode.CUSTOM) {
                return getCustomCounterFromScoreboard(server, playerUUID, counterName);
            }
            return data.value;
        }
        return 0; // Default value if counter not found
    }

    /**
     * Gets the mode of a counter for a player.
     */
    public static CounterMode getCounterMode(UUID playerUUID, String counterName) {
        if (playerCounters.containsKey(playerUUID) && playerCounters.get(playerUUID).containsKey(counterName)) {
            return playerCounters.get(playerUUID).get(counterName).mode;
        }
        return CounterMode.MANUAL; // Default mode if counter not found
    }

    /**
     * Sets the value and mode of a counter for a player.
     */
    public static void setCounter(UUID playerUUID, String counterName, int value, CounterMode mode, MinecraftServer server) {
        playerCounters.computeIfAbsent(playerUUID, k -> new HashMap<>())
                      .put(counterName, new CounterData(value, mode));
        // If mode is CUSTOM, sync to scoreboard
        if (mode == CounterMode.CUSTOM) {
            syncCustomCounterToScoreboard(server, playerUUID, counterName, value);
        }
    }

    /**
     * Checks if a player has a specific counter.
     */
    public static boolean hasCounter(UUID playerUUID, String counterName) {
        return playerCounters.containsKey(playerUUID) && playerCounters.get(playerUUID).containsKey(counterName);
    }

    /**
     * Removes a counter for a player.
     * Returns true if the counter was found and removed, false otherwise.
     */
    public static boolean removeCounter(UUID playerUUID, String counterName, MinecraftServer server) {
        if (hasCounter(playerUUID, counterName)) {
            playerCounters.get(playerUUID).remove(counterName);
            // Remove from scoreboard if it was a CUSTOM counter
            Scoreboard scoreboard = server.getScoreboard();
            ScoreboardObjective objective = scoreboard.getNullableObjective(counterName);
            if (objective != null) {
                scoreboard.removeObjective(objective);
            }
            // If the player has no more counters, remove their UUID entry
            if (playerCounters.get(playerUUID).isEmpty()) {
                playerCounters.remove(playerUUID);
            }
            return true;
        }
        return false;
    }

    /**
     * Clears all counters for all players.
     */
    public static void clearAllCounters() {
        playerCounters.clear();
    }

    /**
     * Loads player counter data from the world's data directory.
     */
    public static void loadData(MinecraftServer server) {
        File dataFile = server.getSavePath(WorldSavePath.ROOT).resolve("data").resolve(COUNTERS_DATA_FILE_NAME).toFile();
        if (dataFile.exists()) {
            try (FileReader reader = new FileReader(dataFile)) {
                Type type = new TypeToken<Map<UUID, Map<String, CounterData>>>(){}.getType();
                Map<UUID, Map<String, CounterData>> loadedData = GSON.fromJson(reader, type);
                if (loadedData != null) {
                    playerCounters.clear();
                    playerCounters.putAll(loadedData);
                    Arythings.LOGGER.info("Loaded player counters data.");
                }
            } catch (IOException e) {
                Arythings.LOGGER.error("Failed to load player counters data: ", e);
            }
        } else {
            Arythings.LOGGER.info("No player counters data file found, starting fresh.");
        }
    }

    /**
     * Saves player counter data to the world's data directory.
     */
    public static void saveData(MinecraftServer server) {
        File dataDir = server.getSavePath(WorldSavePath.ROOT).resolve("data").toFile();
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File dataFile = new File(dataDir, COUNTERS_DATA_FILE_NAME);
        try {
            Files.write(dataFile.toPath(), GSON.toJson(playerCounters).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            Arythings.LOGGER.info("Saved player counters data.");
        } catch (IOException e) {
            Arythings.LOGGER.error("Failed to save player counters data: ", e);
        }
    }

    /**
     * Provides access to the raw playerCounters map.
     */
    public static Map<UUID, Map<String, CounterData>> getAllPlayerCounters() {
        return playerCounters;
    }

    /**
     * Syncs a CUSTOM counter to the scoreboard for a player.
     */
    public static void syncCustomCounterToScoreboard(MinecraftServer server, UUID playerUUID, String counterName, int value) {
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(counterName);
        if (objective == null) {
            objective = scoreboard.addObjective(counterName, ScoreboardCriterion.DUMMY, Text.literal(counterName), ScoreboardCriterion.RenderType.INTEGER, true, null);
        }
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUUID);
        if (player != null) {
            scoreboard.getOrCreateScore(player, objective).setScore(value);
        }
    }
    /**
     * Reads a CUSTOM counter value from the scoreboard for a player.
     */
    public static int getCustomCounterFromScoreboard(MinecraftServer server, UUID playerUUID, String counterName) {
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(counterName);
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUUID);
        if (objective != null && player != null) {
            return scoreboard.getScore(player, objective).getScore();
        }
        return 0; // Default value if not found
    }
}