package net.arclieq.arythings.item;

import net.arclieq.arythings.Arythings;
import net.arclieq.arythings.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup ARYTHINGS_GROUP = Registry.register(Registries.ITEM_GROUP, 
    Identifier.of(Arythings.MOD_ID, "arythings"), FabricItemGroup.builder().icon(() -> 
    new ItemStack(ModItems.NETIAMOND)).displayName(Text.literal("Arythings"))
    .entries((displayContext, entries) -> {
        entries.add(ModItems.NETIAMOND);
        entries.add(ModItems.LUZZANTUM_INGOT);
        entries.add(ModItems.MYTHRIL_INGOT);
        entries.add(ModItems.LUMIT_SHARD);
        entries.add(ModItems.ASTRYLUNA_STAR);
        entries.add(ModBlocks.LUMIT_BLOCK);
        entries.add(ModBlocks.LUMIT_ORE);
        entries.add(ModBlocks.DEEPSLATE_LUMIT_ORE);
        entries.add(ModBlocks.NETIAMOND_BLOCK);
        entries.add(ModBlocks.NETHER_NETIAMOND_ORE);
        entries.add(ModBlocks.MYTHRIL_BLOCK);
        entries.add(ModBlocks.MYTHRIL_ORE);
        entries.add(ModBlocks.DEEPSLATE_MYTHRIL_ORE);
        entries.add(ModBlocks.LUZZANTUM_BLOCK);
        entries.add(ModBlocks.LUZZANTUM_ORE);
        entries.add(ModBlocks.DEEPSLATE_LUZZANTUM_ORE);
        entries.add(ModItems.BROKEN_NETIAMOND_HELMET);
        entries.add(ModItems.BROKEN_NETIAMOND_CHESTPLATE);
        entries.add(ModItems.BROKEN_NETIAMOND_LEGGINGS);
        entries.add(ModItems.BROKEN_NETIAMOND_BOOTS);
        entries.add(ModItems.BROKEN_LUZZANTUM_HELMET);
        entries.add(ModItems.BROKEN_LUZZANTUM_CHESTPLATE);
        entries.add(ModItems.BROKEN_LUZZANTUM_LEGGINGS);
        entries.add(ModItems.BROKEN_LUZZANTUM_BOOTS);
    })
    .build());
    public static final ItemGroup ARYTHINGS_COMBAT_GROUP = Registry.register(Registries.ITEM_GROUP, 
    Identifier.of(Arythings.MOD_ID, "arythings_combat"), FabricItemGroup.builder().icon(() -> 
    new ItemStack(ModItems.NETIAMOND_CHESTPLATE)).displayName(Text.literal("Arythings Combat"))
    .entries((displayContext, entries) -> {
        entries.add(ModItems.UPGRADED_MACE);
        entries.add(ModItems.NETIAMOND_SWORD);
        entries.add(ModItems.LUZZANTUM_SWORD);
        entries.add(ModItems.LUMIT_SWORD);
        entries.add(ModItems.MYTHRIL_SWORD);
        entries.add(ModItems.NETIAMOND_AXE);
        entries.add(ModItems.LUZZANTUM_AXE);
        entries.add(ModItems.MYTHRIL_AXE);
        entries.add(ModItems.LUMIT_AXE);
        entries.add(ModItems.UPGRADED_LUZZANTUM_SWORD);
        entries.add(ModItems.UPGRADED_LUZZANTUM_AXE);
        entries.add(ModItems.NETIAMOND_HELMET);
        entries.add(ModItems.NETIAMOND_CHESTPLATE);
        entries.add(ModItems.NETIAMOND_LEGGINGS);
        entries.add(ModItems.NETIAMOND_BOOTS);
        entries.add(ModItems.LUZZANTUM_HELMET);
        entries.add(ModItems.LUZZANTUM_CHESTPLATE);
        entries.add(ModItems.LUZZANTUM_LEGGINGS);
        entries.add(ModItems.LUZZANTUM_BOOTS);
        entries.add(ModItems.MYTHRIL_HELMET);
        entries.add(ModItems.MYTHRIL_CHESTPLATE);
        entries.add(ModItems.MYTHRIL_LEGGINGS);
        entries.add(ModItems.MYTHRIL_BOOTS);
        entries.add(ModItems.LUMIT_HELMET);
        entries.add(ModItems.LUMIT_CHESTPLATE); 
        entries.add(ModItems.LUMIT_LEGGINGS);
        entries.add(ModItems.LUMIT_BOOTS);
    })
    .build());
    public static final ItemGroup ARYTHINGS_TOOLS_GROUP = Registry.register(Registries.ITEM_GROUP, 
    Identifier.of(Arythings.MOD_ID, "arythings_tools"), FabricItemGroup.builder().icon(() -> 
    new ItemStack(ModItems.NETIAMOND_PICKAXE)).displayName(Text.literal("Arythings Tools"))
    .entries((displayContext, entries) -> {
        entries.add(ModItems.NETIAMOND_PICKAXE);
        entries.add(ModItems.NETIAMOND_AXE);
        entries.add(ModItems.NETIAMOND_SHOVEL);
        entries.add(ModItems.NETIAMOND_HOE);
        entries.add(ModItems.LUZZANTUM_PICKAXE);
        entries.add(ModItems.LUZZANTUM_AXE);
        entries.add(ModItems.LUZZANTUM_SHOVEL);
        entries.add(ModItems.LUZZANTUM_HOE);
        entries.add(ModItems.LUMIT_PICKAXE);
        entries.add(ModItems.LUMIT_AXE);
        entries.add(ModItems.LUMIT_SHOVEL);
        entries.add(ModItems.LUMIT_HOE);
        entries.add(ModItems.MYTHRIL_PICKAXE);
        entries.add(ModItems.MYTHRIL_AXE);
        entries.add(ModItems.MYTHRIL_SHOVEL);
        entries.add(ModItems.MYTHRIL_HOE);
        entries.add(ModItems.UPGRADED_LUZZANTUM_PICKAXE);
        entries.add(ModItems.UPGRADED_LUZZANTUM_AXE);
        entries.add(ModItems.UPGRADED_LUZZANTUM_SHOVEL);
    })
    .build());
    /**
     * Registers item groups for the mod.
     */
    public static void registerItemGroups() {
        Arythings.LOGGER.debug("Creating item groups for " + Arythings.MOD_ID + "...");
    }
}
