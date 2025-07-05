package net.arclieq.arythings.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Collections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
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
    // maxCounterValue -> mCV, unchangeableCounters -> uC

    // GSON instance for serializing/deserializing player counter data
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // File name for player counter data
    static final String COUNTERS_DATA_FILE_NAME = "counters.json";

    // Main data structure: player UUID -> (counter name -> CounterData)
    private static final Map<UUID, Map<String, CounterData>> playerCounters = new HashMap<>();

    // Global configuration values for counters, now managed by Arythings.java and passed here.
    public static int mCV = 32767;
    private static Set<String> uC = new HashSet<>();

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
     * Applies configuration values loaded from the main mod class.
     * This centralizes config management in Arythings.java.
     * @param maxVal The maximum value for any counter.
     * @param unchangeable The set of counter names that cannot be changed or removed via commands.
     */
    public static void applyConfig(int maxVal, Set<String> unchangeable) {
        mCV = maxVal;
        uC = new HashSet<>(unchangeable); // Create a copy
    }

    /**
     * Provides access to the maximum counter value.
     * @return the maximum allowed value for counters.
     */
    public static int getMaxCounterValue() {
        return mCV;
    }

    /**
     * Provides access to the set of unchangeable counter names.
     * @return an unmodifiable set of unchangeable counter names.
     */
    public static Set<String> getUnchangeableCounters() {
        return Collections.unmodifiableSet(uC);
    }

    /**
     * Gets BOTH the mode and the value of a counter.
     * 
     * @param uuid
     * @param counter
     * @param server
     * @return Map.Entry<Integer, CounterMode>
     */
    public static Map.Entry<Integer, CounterMode> getCounter(UUID uuid, String counter, MinecraftServer server) {
        if (playerCounters.containsKey(uuid) && playerCounters.get(uuid).containsKey(counter)) {
            CounterData data = playerCounters.get(uuid).get(counter);
            // If mode is CUSTOM, retrieve from scoreboard, otherwise use stored value
            if (data.mode == CounterMode.CUSTOM) {
                return Map.entry(getCustomCounterFromScoreboard(server, uuid, counter), CounterMode.CUSTOM);
            }
            return Map.entry(data.value, data.mode);
        }

        return Map.entry(0, CounterMode.MANUAL);
    }

    /**
     * Helper method to find a counter's mode without having to use 'CounterHelperUtil.getCounter().getValue()'.
     * <p>Useful when just returning a </p>
     * 
     * @param uuid
     * @param counter
     * @param server
     * @return
     */
    public static CounterMode getCounterMode(UUID uuid, String counter, MinecraftServer server) {
        return getCounter(uuid, counter, server).getValue();
    }
    /**
     * 
     * 
     * @param uuid
     * @param counter
     * @param server
     * @return
     */
    public static int getCounterValue(UUID uuid, String counter, MinecraftServer server) {
        return getCounter(uuid, counter, server).getKey();
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
        File dataFile = server.getSavePath(WorldSavePath.ROOT).resolve("arythings").resolve(COUNTERS_DATA_FILE_NAME).toFile();
        if (dataFile.exists()) {
            try (FileReader reader = new FileReader(dataFile)) {
                Type type = new TypeToken<Map<UUID, Map<String, CounterData>>>(){}.getType();
                Map<UUID, Map<String, CounterData>> loadedData = GSON.fromJson(reader, type);
                if (loadedData != null) {
                    playerCounters.clear();
                    playerCounters.putAll(loadedData);
                }
            } catch (IOException e) {
                Arythings.LOGGER.debug("Failed to load player counters data, this is normal if the file is empty/new.", e);
            }
        } else {}
    }

    /**
     * Saves player counter data to the world's data directory.
     */
    public static void saveData(MinecraftServer server) {
        File dataDir = server.getSavePath(WorldSavePath.ROOT).resolve("arythings").toFile();
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File dataFile = new File(dataDir, COUNTERS_DATA_FILE_NAME);
        try {
            Files.write(dataFile.toPath(), GSON.toJson(playerCounters).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            Arythings.LOGGER.debug("Failed to save player counters data.", e);
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