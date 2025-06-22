package net.arclieq.arythings.world;

import java.util.List;

import net.arclieq.arythings.Arythings;
import net.arclieq.arythings.block.ModBlocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class ModConfiguredFeatures {

    public static final RegistryKey<ConfiguredFeature<?, ?>> NETHER_NETIAMOND_ORE_KEY = registerKey("nether_netiamond_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> NETHER_LUZZANTUM_ORE_KEY = registerKey("nether_luzzantum_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> MYTHRIL_ORE_KEY = registerKey("mythril_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LUZZANTUM_ORE_KEY = registerKey("luzzantum_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LUMIT_ORE_KEY = registerKey("lumit_ore");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest netherReplaceables = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);

        List<OreFeatureConfig.Target> overworldMythrilOres = 
        List.of(OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.MYTHRIL_ORE.getDefaultState()),
        OreFeatureConfig.createTarget(deepslateReplaceables, ModBlocks.DEEPSLATE_MYTHRIL_ORE.getDefaultState()));

        List<OreFeatureConfig.Target> overworldLuzzantumOres =
        List.of(OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.LUZZANTUM_ORE.getDefaultState()),
        OreFeatureConfig.createTarget(deepslateReplaceables, ModBlocks.DEEPSLATE_LUZZANTUM_ORE.getDefaultState()));

        List<OreFeatureConfig.Target> overworldLumitOres =
        List.of(OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.LUMIT_ORE.getDefaultState()),
        OreFeatureConfig.createTarget(deepslateReplaceables, ModBlocks.DEEPSLATE_LUMIT_ORE.getDefaultState()));

        List<OreFeatureConfig.Target> netherLuzzantumOres =
        List.of(OreFeatureConfig.createTarget(netherReplaceables, ModBlocks.NETHER_LUZZANTUM_ORE.getDefaultState()));

        List<OreFeatureConfig.Target> netherNetiamondOres = 
        List.of(OreFeatureConfig.createTarget(netherReplaceables, ModBlocks.NETHER_NETIAMOND_ORE.getDefaultState()));

        register(context, MYTHRIL_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldMythrilOres, 6));
        register(context, LUMIT_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldLumitOres, 4));
        register(context, LUZZANTUM_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldLuzzantumOres, 3));
        register(context, NETHER_LUZZANTUM_ORE_KEY, Feature.ORE, new OreFeatureConfig(netherLuzzantumOres, 4));
        register(context, NETHER_NETIAMOND_ORE_KEY, Feature.SCATTERED_ORE, new OreFeatureConfig(netherNetiamondOres, 2));
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(Arythings.MOD_ID, name));

    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
    RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        context.register(key, new ConfiguredFeature<>(feature, config));
    }
    
}
