package net.arclieq.arythings.world.gen;

import net.arclieq.arythings.Arythings;

public class ModWorldGeneration {
    /**
     * This method is called when the world generation is being created.
     * It is used to register the world generation features.
     */
    public static void generateModWorldGen() {
        Arythings.LOGGER.debug("Creating " + Arythings.MOD_ID + " world generation...");
        ModOreGeneration.generateOres();
    }
    
}
