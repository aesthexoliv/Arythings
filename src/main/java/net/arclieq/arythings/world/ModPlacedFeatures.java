package net.arclieq.arythings.world;

import java.util.List;

import net.arclieq.arythings.Arythings;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

public class ModPlacedFeatures {
    public static final RegistryKey<PlacedFeature> MYTHRIL_ORE_PLACED_KEY = registerKey("mythril_ore_placed_key");
    public static final RegistryKey<PlacedFeature> LUZZANTUM_ORE_PLACED_KEY = registerKey("luzzantum_ore_placed_key");
    public static final RegistryKey<PlacedFeature> LUMIT_ORE_PLACED_KEY = registerKey("lumit_ore_placed_key");
    public static final RegistryKey<PlacedFeature> NETHER_LUZZANTUM_ORE_PLACED_KEY = registerKey("nether_luzzantum_ore_placed_key");
    public static final RegistryKey<PlacedFeature> NETHER_NETIAMOND_ORE_PLACED_KEY = registerKey("nether_netiamond_ore_placed_key");


    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatures = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, MYTHRIL_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.MYTHRIL_ORE_KEY),
        ModOrePlacement.modifierWithCount(8, HeightRangePlacementModifier.uniform(YOffset.fixed(-80), YOffset.fixed(50))));

        register(context, LUZZANTUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.LUZZANTUM_ORE_KEY),
        ModOrePlacement.modifierWithCount(6, HeightRangePlacementModifier.uniform(YOffset.fixed(-80), YOffset.fixed(20))));

        register(context, NETHER_NETIAMOND_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.NETHER_NETIAMOND_ORE_KEY),
        ModOrePlacement.modifierWithRarity(48, HeightRangePlacementModifier.uniform(YOffset.fixed(-80), YOffset.fixed(30))));

        register(context, NETHER_LUZZANTUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.NETHER_LUZZANTUM_ORE_KEY),
        ModOrePlacement.modifierWithCount(5, HeightRangePlacementModifier.uniform(YOffset.fixed(-80), YOffset.fixed(50))));

        register(context, LUMIT_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.LUMIT_ORE_KEY), 
        ModOrePlacement.modifierWithCount(7, HeightRangePlacementModifier.uniform(YOffset.fixed(-80), YOffset.fixed(40))));
    }
    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Arythings.MOD_ID, name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> config, 
    List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(config, List.copyOf(modifiers)));
    }
    
    @SuppressWarnings("unused")
    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
    RegistryEntry<ConfiguredFeature<?, ?>> config, PlacementModifier... modifiers) {
        register(context, key, config, List.of(modifiers));
    }
}
