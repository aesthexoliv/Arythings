package net.arclieq.arythings.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "counters.json";

    // Main data structure: player UUID -> (counter name -> CounterData)
    private static final Map<UUID, Map<String, CounterData>> playerCounters = new HashMap<>();

    /**
     * Gets the value of a counter for a player.
     */
    public static int getCounterValue(UUID playerUUID, String counterName, MinecraftServer server) {
        CounterData data = playerCounters.computeIfAbsent(playerUUID, k -> new HashMap<>())
            .getOrDefault(counterName, new CounterData(0, CounterMode.MANUAL));
        if(data.mode == CounterMode.CUSTOM) {
            return getCustomCounterFromScoreboard(server, playerUUID, counterName);
        }
        return data.value;
    }

    /**
     * Gets the mode of a counter for a player.
     */
    public static CounterMode getCounterMode(UUID playerUUID, String counterName) {
        return playerCounters.computeIfAbsent(playerUUID, k -> new HashMap<>())
            .getOrDefault(counterName, new CounterData(0, CounterMode.MANUAL)).mode;
    }

    /**
     * Sets the value and mode of a counter for a player.
     */
    public static void setCounter(UUID playerUUID, String counterName, int value, CounterMode mode, MinecraftServer server) {
        int clampedValue = Math.min(value, maxCounterValue);
        playerCounters.computeIfAbsent(playerUUID, k -> new HashMap<>())
            .put(counterName, new CounterData(clampedValue, mode));
        if(mode == CounterMode.CUSTOM) {
            syncCustomCounterToScoreboard(server, playerUUID, counterName, clampedValue);
        }
    }

    /**
     * Increments the value of a counter for a player (only if mode is TICK).
     */
    public static void incrementTickCounters(UUID playerUUID) {
        Map<String, CounterData> counters = playerCounters.get(playerUUID);
        if (counters != null) {
            for (CounterData data : counters.values()) {
                if (data.mode == CounterMode.TICK) {
                    if(data.value < maxCounterValue) data.value++;
                }
            }
        }
    }

    public static boolean removeCounter(UUID playerUUID, String counterName, MinecraftServer server) {
        Map<String, CounterData> counters = playerCounters.get(playerUUID);
        if (counters != null && counters.remove(counterName) != null) {
            saveData(server);
            return true;
        }
        return false;
    }

    /**
     * Checks if a player already has a counter with the given name.
     */
    public static boolean hasCounter(UUID playerUUID, String counterName) {
        return playerCounters.containsKey(playerUUID) && playerCounters.get(playerUUID).containsKey(counterName);
    }

    /**
     * Loads counter data from file.
     */
    public static void loadData(MinecraftServer server) {
        File configFile = Paths.get("config", "arythings.json").toFile();
        String configJson = safeReadFile(configFile);
        if (configJson != null) {
            JsonObject obj = JsonParser.parseString(configJson).getAsJsonObject();
            if (obj.has("maxCounterValue")) {
                CounterHelperUtil.maxCounterValue = obj.get("maxCounterValue").getAsInt();
            }
        }
        File dataFile = getDataFile(server);
        String dataJson = safeReadFile(dataFile);
        if (dataJson != null) {
            Type type = new TypeToken<Map<UUID, Map<String, CounterData>>>(){}.getType();
            Map<UUID, Map<String, CounterData>> loadedData = GSON.fromJson(dataJson, type);
            if (loadedData != null) {
                playerCounters.clear();
                playerCounters.putAll(loadedData);
                Arythings.LOGGER.info("Loaded counter data from file.");
            }
        } else {
            Arythings.LOGGER.info("counters.json not found, creating one...");
            saveData(server); // Create a new file
        }
    }
    private static String safeReadFile(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            Arythings.LOGGER.error("Failed to read file: " + file.getAbsolutePath(), e);
            return null;
        }
    }

    /**
     * Saves counter data to file.
     */
    public static void saveData(MinecraftServer server) {
        File dataFile = getDataFile(server);
        try {
            String json = GSON.toJson(playerCounters);
            Files.write(dataFile.toPath(), json.getBytes());
            Arythings.LOGGER.info("Saved counter data to file.");
        } catch (IOException e) {
            Arythings.LOGGER.error("Failed to save counter data: ", e);
        }
    }

    /**
     * Gets the file where counter data is stored.
     */
    private static File getDataFile(MinecraftServer server) {
        return server.getSavePath(WorldSavePath.ROOT).resolve(FILE_NAME).toFile();
    }

    public static int maxCounterValue = 32767; // Default

    /**
     * Returns the max allowed value for counters.
     */
    public static int getMaxCounterValue() {
        return maxCounterValue;
    }

    /**
     * Enum for counter modes.
     */
    public enum CounterMode {
        TICK,    // Increments every tick
        MANUAL,  // Only changes via commands
        CUSTOM   // Reserved for future/custom logic
    }

    /**
     * Data class for a counter (value + mode).
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
     * Returns the full map of player counters (for advanced use).
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
