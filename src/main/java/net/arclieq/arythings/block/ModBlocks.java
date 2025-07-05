package net.arclieq.arythings.block;

import net.arclieq.arythings.Arythings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class ModBlocks {

    public static final Block NETIAMOND_BLOCK = registerBlock("netiamond_block", 
    new Block(AbstractBlock.Settings.create().strength(10.0F, 1200.0F)
    .requiresTool().sounds(BlockSoundGroup.NETHERITE))),

    NETHER_NETIAMOND_ORE = registerBlock("nether_netiamond_ore", 
    new ExperienceDroppingBlock(UniformIntProvider.create(5, 8), 
    AbstractBlock.Settings.create().strength(10F, 1200F)
    .requiresTool().sounds(BlockSoundGroup.NETHERRACK))),

    MYTHRIL_BLOCK = registerBlock("mythril_block", 
    new Block(AbstractBlock.Settings.create().strength(6.0F, 6.0F)
    .requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK))),

    MYTHRIL_ORE = registerBlock("mythril_ore", 
    new ExperienceDroppingBlock(UniformIntProvider.create(3, 7),
    AbstractBlock.Settings.create().strength(3.5F, 3.0F)
    .requiresTool().sounds(BlockSoundGroup.STONE))),

    DEEPSLATE_MYTHRIL_ORE = registerBlock("deepslate_mythril_ore", 
    new ExperienceDroppingBlock(UniformIntProvider.create(3, 7), 
    AbstractBlock.Settings.create().strength(4.5F, 3.0F)
    .requiresTool().sounds(BlockSoundGroup.DEEPSLATE))),

    LUZZANTUM_BLOCK = registerBlock("luzzantum_block", 
    new Block(AbstractBlock.Settings.create().strength(8F, 300.0F)
    .requiresTool().sounds(BlockSoundGroup.ANCIENT_DEBRIS))),

    LUZZANTUM_ORE = registerBlock("luzzantum_ore", 
    new ExperienceDroppingBlock(UniformIntProvider.create(3, 7),
    AbstractBlock.Settings.create().strength(5.5F, 3.0F)
    .requiresTool().sounds(BlockSoundGroup.STONE))),

    DEEPSLATE_LUZZANTUM_ORE = registerBlock("deepslate_luzzantum_ore", 
    new ExperienceDroppingBlock(UniformIntProvider.create(5, 8),
    AbstractBlock.Settings.create().strength(7.5F, 3.0F)
    .requiresTool().sounds(BlockSoundGroup.DEEPSLATE))),

    NETHER_LUZZANTUM_ORE = registerBlock("nether_luzzantum_ore",
    new ExperienceDroppingBlock(UniformIntProvider.create(4, 7),
    AbstractBlock.Settings.create().strength(8.6F, 3.0F)
    .requiresTool().sounds(BlockSoundGroup.NETHERRACK))),

    LUMIT_BLOCK = registerBlock("lumit_block", 
    new Block(AbstractBlock.Settings.create().strength(5F, 400.0F)
    .requiresTool().sounds(BlockSoundGroup.AMETHYST_CLUSTER))),

    LUMIT_ORE = registerBlock("lumit_ore", 
    new ExperienceDroppingBlock(UniformIntProvider.create(2, 6),
    AbstractBlock.Settings.create().strength(5.5F, 3.0F)
    .requiresTool().sounds(BlockSoundGroup.STONE))),

    DEEPSLATE_LUMIT_ORE = registerBlock("deepslate_lumit_ore", 
    new ExperienceDroppingBlock(UniformIntProvider.create(4, 8),
    AbstractBlock.Settings.create().strength(6.8F, 3.0F)
    .requiresTool().sounds(BlockSoundGroup.DEEPSLATE))),
    
    ZAZUM_BLOCK = registerBlock("zazum_block", 
    new Block(AbstractBlock.Settings.create().strength(10F, 60F)
    .requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK))),
    
    ZAZUM_ORE = registerBlock("zazum_ore",
    new ExperienceDroppingBlock(UniformIntProvider.create(2, 6),
    AbstractBlock.Settings.create().strength(5.0F, 6.0F)
    .requiresTool().sounds(BlockSoundGroup.STONE))),

    DEEPSLATE_ZAZUM_ORE = registerBlock("deepslate_zazum_ore",
    new ExperienceDroppingBlock(UniformIntProvider.create(3, 7),
    AbstractBlock.Settings.create().strength(7.5F, 6.0F)
    .requiresTool().sounds(BlockSoundGroup.DEEPSLATE)));




    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Arythings.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(Arythings.MOD_ID, name),
        new BlockItem(block, new Item.Settings()));
    }
    /**
     * Registers all blocks in the mod.
     * This method is called during the mod initialization phase.
     */
    public static void registerModBlocks() {
        Arythings.LOGGER.debug("Registering " + Arythings.MOD_ID + " blocks...");
    }
}
