package net.arclieq.arythings.datagen;

import java.util.concurrent.CompletableFuture;

import net.arclieq.arythings.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BlockTags;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider{

    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(WrapperLookup wrapperLookup) {
        // Mineable with pickaxe
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
        .add(ModBlocks.NETIAMOND_BLOCK)
        .add(ModBlocks.NETHER_NETIAMOND_ORE)
        .add(ModBlocks.MYTHRIL_ORE)
        .add(ModBlocks.DEEPSLATE_MYTHRIL_ORE)
        .add(ModBlocks.MYTHRIL_BLOCK)
        .add(ModBlocks.LUZZANTUM_BLOCK)
        .add(ModBlocks.LUZZANTUM_ORE)
        .add(ModBlocks.DEEPSLATE_LUZZANTUM_ORE)
        .add(ModBlocks.NETHER_LUZZANTUM_ORE)
        .add(ModBlocks.LUMIT_BLOCK)
        .add(ModBlocks.LUMIT_ORE)
        .add(ModBlocks.DEEPSLATE_LUMIT_ORE);
        // Needs diamond tool
        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
        .add(ModBlocks.LUZZANTUM_BLOCK)
        .add(ModBlocks.DEEPSLATE_LUZZANTUM_ORE)
        .add(ModBlocks.LUZZANTUM_ORE)
        .add(ModBlocks.NETHER_LUZZANTUM_ORE)
        .add(ModBlocks.NETHER_NETIAMOND_ORE)
        .add(ModBlocks.NETIAMOND_BLOCK)
        .add(ModBlocks.LUMIT_BLOCK)
        .add(ModBlocks.LUMIT_ORE)
        .add(ModBlocks.DEEPSLATE_LUMIT_ORE);
        // Needs iron tool
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
        .add(ModBlocks.MYTHRIL_BLOCK)
        .add(ModBlocks.MYTHRIL_ORE)
        .add(ModBlocks.DEEPSLATE_MYTHRIL_ORE);
    }
    
}
