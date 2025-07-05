package net.arclieq.arythings;

import net.arclieq.arythings.block.ModBlocks;
import net.arclieq.arythings.item.ModItemGroups;
import net.arclieq.arythings.item.ModItems;
import net.arclieq.arythings.util.CounterHelperUtil;
import net.arclieq.arythings.util.ConfigManager;
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

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

/**
 * The main class of the Arythings mod.
 * Handles initialization, event logic, etc
 */
public class Arythings implements ModInitializer {
    // Last changed: 30/06/2025
    /* TODO LIST:
     * - Add custom enchants (25/07/2025)
     * - Add upgraded mace usages (01/08/2025)
     * - Add luzzantum right click methods (01/08/2025)
     * - Create custom structures (01/08/2025)
     * - Add Upgraded Lumit tool textures (01/08/2025)
     * - Add Zazum textures (01/08/2025)
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

    @Override public void onInitialize() {loadMod();}

    /**
     * Loads the WHOLE mod, from config to registries.
     */
    private void loadMod() {
        LOGGER.info("Loading mod, please wait!");
        
        // Load all configurations from a single, unified method
        ConfigManager.loadAndSyncConfig();

        // Register server lifecycle events
        LOGGER.info("Loading events, please wait.");

        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);

        LOGGER.info("Loading mod context...");

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {ModCommands.registerCommands(dispatcher);});
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> onPlayerJoin(server, handler.getPlayer()));

        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModItemGroups.registerItemGroups();
        ModWorldGeneration.generateModWorldGen();
        LOGGER.info("Finished!");
    }

    /**
     * Ensures all counters present on the server (either pre-defined or existing for other players)
     * are initialized for a joining player if they don't already have them, preserving existing modes.
     *
     * @param server The Minecraft server instance.
     * @param player The ServerPlayerEntity that just joined.
     */
    private void onPlayerJoin(MinecraftServer server, ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        // CM = counterModes
        Map<String, CounterMode> CM = new HashMap<>();
        CM.put("luzzantum", CounterMode.TICK);
        CM.put("netiamond", CounterMode.TICK);

        for (Map<String, CounterHelperUtil.CounterData> playerCounters : CounterHelperUtil.getAllPlayerCounters().values()) {
            playerCounters.forEach((name, data) -> CM.put(name, data.mode));
        }
        
        int defaultTickValue = 20000; 
        for (Map.Entry<String, CounterMode> entry : CM.entrySet()) {
            String counterName = entry.getKey();
            CounterMode knownMode = entry.getValue();

            if (!CounterHelperUtil.hasCounter(uuid, counterName)) {
                int valueToSet = 0;
                if (knownMode == CounterMode.TICK) {
                    valueToSet = defaultTickValue;
                }
                CounterHelperUtil.setCounter(uuid, counterName, valueToSet, knownMode, server);
            }
        }
    }


    /**
     * Called when the server starts.
     * Loads counter data from file.
     */
    private void onServerStarted(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            for (String counter : CounterHelperUtil.getAllPlayerCounters().getOrDefault(player.getUuid(), Collections.emptyMap()).keySet()) {
                if(CounterHelperUtil.getCounterValue(player.getUuid(), counter, server) > CounterHelperUtil.getMaxCounterValue()) {
                    CounterMode mode = CounterHelperUtil.getCounterMode(player.getUuid(), counter, server);
                    CounterHelperUtil.setCounter(player.getUuid(), counter, CounterHelperUtil.getMaxCounterValue(), mode, server);
                }
            }
        }
        CounterHelperUtil.loadData(server);
    }

    /**
     * Called when the server is stopping.
     * Saves counter data to file.
     */
    private void onServerStopping(MinecraftServer server) {
        CounterHelperUtil.saveData(server);
    }

    /**
     * Called every server tick.
     * Periodically saves counter data to file (every 5 minutes).
     */
    private void onServerTick(MinecraftServer server) {
        if (!ConfigManager.bI.isEmpty()) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                for (int i = 0; i < player.getInventory().size(); i++) {
                    Identifier currentItemIdentifier = net.minecraft.registry.Registries.ITEM.getId(player.getInventory().getStack(i).getItem());
                    String currentItemString = currentItemIdentifier.toString();
                    
                    if(ConfigManager.getBannedItems().contains(currentItemString)) {
                        boolean affectsCreative = ConfigManager.getDefaultAffectsCreative();
                        boolean affectsOperators = ConfigManager.getDefaultAffectsOperators();


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
