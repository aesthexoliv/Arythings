package net.arclieq.arythings.datagen;

import net.arclieq.arythings.block.ModBlocks;
import net.arclieq.arythings.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Items;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_LUZZANTUM_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.LUZZANTUM_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.LUZZANTUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_MYTHRIL_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MYTHRIL_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NETHER_NETIAMOND_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NETHER_LUZZANTUM_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MYTHRIL_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NETIAMOND_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.LUMIT_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.LUMIT_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_LUMIT_ORE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.UPGRADED_LUZZANTUM_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.UPGRADED_LUZZANTUM_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.UPGRADED_LUZZANTUM_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.UPGRADED_LUZZANTUM_SHOVEL, Models.HANDHELD);

        itemModelGenerator.register(ModItems.UPGRADED_LUMIT_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.UPGRADED_LUMIT_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.UPGRADED_LUMIT_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.UPGRADED_LUMIT_SHOVEL, Models.HANDHELD);
        
        itemModelGenerator.register(ModItems.UPGRADED_MACE, Items.MACE, Models.HANDHELD_MACE);

        itemModelGenerator.register(ModItems.LUMIT_SHARD, Models.GENERATED);
        itemModelGenerator.register(ModItems.LUZZANTUM_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.NETIAMOND, Models.GENERATED);
        itemModelGenerator.register(ModItems.ASTRYLUNA_STAR, Models.GENERATED);
        itemModelGenerator.register(ModItems.MYTHRIL_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.ZAZUM_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.NETIAMOND_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.NETIAMOND_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.NETIAMOND_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.NETIAMOND_HOE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.LUZZANTUM_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.LUZZANTUM_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.LUZZANTUM_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.LUZZANTUM_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.LUZZANTUM_HOE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.LUMIT_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.LUMIT_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.LUMIT_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.LUMIT_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.LUMIT_HOE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.MYTHRIL_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.MYTHRIL_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.MYTHRIL_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.MYTHRIL_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.MYTHRIL_HOE, Models.HANDHELD);

        itemModelGenerator.register(ModItems.BROKEN_LUZZANTUM_HELMET, Models.GENERATED);
        itemModelGenerator.register(ModItems.BROKEN_LUZZANTUM_CHESTPLATE, Models.GENERATED);
        itemModelGenerator.register(ModItems.BROKEN_LUZZANTUM_LEGGINGS, Models.GENERATED);
        itemModelGenerator.register(ModItems.BROKEN_LUZZANTUM_BOOTS, Models.GENERATED);
        itemModelGenerator.register(ModItems.BROKEN_NETIAMOND_HELMET, Models.GENERATED);
        itemModelGenerator.register(ModItems.BROKEN_NETIAMOND_CHESTPLATE, Models.GENERATED);
        itemModelGenerator.register(ModItems.BROKEN_NETIAMOND_LEGGINGS, Models.GENERATED);
        itemModelGenerator.register(ModItems.BROKEN_NETIAMOND_BOOTS, Models.GENERATED);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.NETIAMOND_HELMET);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.NETIAMOND_CHESTPLATE);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.NETIAMOND_LEGGINGS);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.NETIAMOND_BOOTS);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.LUZZANTUM_HELMET);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.LUZZANTUM_CHESTPLATE);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.LUZZANTUM_LEGGINGS);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.LUZZANTUM_BOOTS);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.LUMIT_HELMET);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.LUMIT_CHESTPLATE);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.LUMIT_LEGGINGS);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.LUMIT_BOOTS);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.MYTHRIL_HELMET);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.MYTHRIL_CHESTPLATE);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.MYTHRIL_LEGGINGS);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.MYTHRIL_BOOTS);
    }
    
}
