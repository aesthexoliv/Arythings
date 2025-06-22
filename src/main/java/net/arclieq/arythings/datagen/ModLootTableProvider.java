package net.arclieq.arythings.datagen;

import java.util.concurrent.CompletableFuture;

import net.arclieq.arythings.block.ModBlocks;
import net.arclieq.arythings.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }
    @Override
    public void generate() {
        addDrop(ModBlocks.NETIAMOND_BLOCK);
        addDrop(ModBlocks.MYTHRIL_BLOCK);
        addDrop(ModBlocks.LUZZANTUM_BLOCK);
        addDrop(ModBlocks.LUMIT_BLOCK);

        addDrop(ModBlocks.NETHER_NETIAMOND_ORE, oreDrops(ModBlocks.NETHER_NETIAMOND_ORE, ModItems.NETIAMOND));
        addDrop(ModBlocks.NETHER_LUZZANTUM_ORE, oreDrops(ModBlocks.NETHER_LUZZANTUM_ORE, ModItems.LUZZANTUM_INGOT));
        addDrop(ModBlocks.LUZZANTUM_ORE, oreDrops(ModBlocks.LUZZANTUM_ORE, ModItems.LUZZANTUM_INGOT));
        
        addDrop(ModBlocks.DEEPSLATE_MYTHRIL_ORE, multipleOreDrops(ModBlocks.DEEPSLATE_MYTHRIL_ORE, ModItems.MYTHRIL_INGOT, 1, 4));
        addDrop(ModBlocks.MYTHRIL_ORE, multipleOreDrops(ModBlocks.MYTHRIL_ORE, ModItems.MYTHRIL_INGOT, 1, 3));
        addDrop(ModBlocks.DEEPSLATE_LUZZANTUM_ORE, multipleOreDrops(ModBlocks.DEEPSLATE_LUZZANTUM_ORE, ModItems.LUZZANTUM_INGOT, 1, 2));
        addDrop(ModBlocks.LUMIT_ORE, multipleOreDrops(ModBlocks.LUMIT_ORE, ModItems.LUMIT_SHARD, 1, 2));
        addDrop(ModBlocks.DEEPSLATE_LUMIT_ORE, multipleOreDrops(ModBlocks.DEEPSLATE_LUMIT_ORE, ModItems.LUMIT_SHARD, 1, 3));
        
    }
    public LootTable.Builder multipleOreDrops(Block blockDrop, Item item, float minDrops, float maxDrops) {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return this.dropsWithSilkTouch(blockDrop, this.applyExplosionDecay(blockDrop, ((LeafEntry.Builder<?>)
                ItemEntry.builder(item).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(minDrops, maxDrops))))
                .apply(ApplyBonusLootFunction.oreDrops(impl.getOrThrow(Enchantments.FORTUNE)))));
    }
}
