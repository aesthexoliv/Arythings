package net.arclieq.arythings.datagen;

import java.util.concurrent.CompletableFuture;

import net.arclieq.arythings.item.ModItems;
import net.arclieq.arythings.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.ItemTags;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ItemTags.MACE_ENCHANTABLE).add(ModItems.UPGRADED_MACE);
        getOrCreateTagBuilder(ItemTags.DURABILITY_ENCHANTABLE).add(ModItems.UPGRADED_MACE).add(ModItems.ASTRYLUNA_SHIELD);
        getOrCreateTagBuilder(ItemTags.FIRE_ASPECT_ENCHANTABLE).add(ModItems.UPGRADED_MACE);
        // Trimmable armor
        getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR)
        .add(ModItems.NETIAMOND_HELMET)
        .add(ModItems.NETIAMOND_CHESTPLATE)
        .add(ModItems.NETIAMOND_LEGGINGS)
        .add(ModItems.NETIAMOND_BOOTS)
        .add(ModItems.LUZZANTUM_HELMET)
        .add(ModItems.LUZZANTUM_CHESTPLATE)
        .add(ModItems.LUZZANTUM_LEGGINGS)
        .add(ModItems.LUZZANTUM_BOOTS)
        .add(ModItems.MYTHRIL_HELMET)
        .add(ModItems.MYTHRIL_CHESTPLATE)
        .add(ModItems.MYTHRIL_LEGGINGS)
        .add(ModItems.MYTHRIL_BOOTS)
        .add(ModItems.LUMIT_HELMET)
        .add(ModItems.LUMIT_CHESTPLATE)
        .add(ModItems.LUMIT_LEGGINGS)
        .add(ModItems.LUMIT_BOOTS);
        // Tools enchants
        getOrCreateTagBuilder(ItemTags.MINING_ENCHANTABLE)
        .add(ModItems.NETIAMOND_PICKAXE)
        .add(ModItems.NETIAMOND_AXE)
        .add(ModItems.NETIAMOND_SHOVEL)
        .add(ModItems.NETIAMOND_HOE)
        .add(ModItems.LUZZANTUM_PICKAXE)
        .add(ModItems.LUZZANTUM_AXE)
        .add(ModItems.LUZZANTUM_SHOVEL)
        .add(ModItems.LUZZANTUM_HOE)
        .add(ModItems.MYTHRIL_PICKAXE)
        .add(ModItems.MYTHRIL_AXE)
        .add(ModItems.MYTHRIL_SHOVEL)
        .add(ModItems.MYTHRIL_HOE)
        .add(ModItems.LUMIT_PICKAXE)
        .add(ModItems.LUMIT_AXE)
        .add(ModItems.LUMIT_SHOVEL)
        .add(ModItems.LUMIT_HOE)
        .add(ModItems.UPGRADED_LUZZANTUM_PICKAXE)
        .add(ModItems.UPGRADED_LUZZANTUM_AXE)
        .add(ModItems.UPGRADED_LUZZANTUM_SHOVEL);
        // Weapons enchants
        getOrCreateTagBuilder(ItemTags.WEAPON_ENCHANTABLE)
        .add(ModItems.NETIAMOND_SWORD)
        .add(ModItems.NETIAMOND_AXE)
        .add(ModItems.LUZZANTUM_SWORD)
        .add(ModItems.LUZZANTUM_AXE)
        .add(ModItems.MYTHRIL_SWORD)
        .add(ModItems.MYTHRIL_AXE)
        .add(ModItems.LUMIT_SWORD)
        .add(ModItems.LUMIT_AXE)
        .add(ModItems.UPGRADED_LUZZANTUM_SWORD)
        .add(ModItems.UPGRADED_LUZZANTUM_AXE)
        .add(ModItems.UPGRADED_MACE);
        // Sword enchants
        getOrCreateTagBuilder(ItemTags.SWORD_ENCHANTABLE).add(ModItems.NETIAMOND_SWORD).add(ModItems.LUZZANTUM_SWORD);
        // Other
        getOrCreateTagBuilder(ItemTags.SWORDS).add(ModItems.NETIAMOND_SWORD).add(ModItems.LUZZANTUM_SWORD).add(ModItems.MYTHRIL_SWORD).add(ModItems.LUMIT_SWORD);
        getOrCreateTagBuilder(ItemTags.PICKAXES).add(ModItems.NETIAMOND_PICKAXE).add(ModItems.LUZZANTUM_PICKAXE).add(ModItems.MYTHRIL_PICKAXE).add(ModItems.LUMIT_PICKAXE);
        getOrCreateTagBuilder(ItemTags.AXES).add(ModItems.NETIAMOND_AXE).add(ModItems.LUZZANTUM_AXE).add(ModItems.MYTHRIL_AXE).add(ModItems.LUMIT_AXE);
        getOrCreateTagBuilder(ItemTags.SHOVELS).add(ModItems.NETIAMOND_SHOVEL).add(ModItems.LUZZANTUM_SHOVEL).add(ModItems.MYTHRIL_SHOVEL).add(ModItems.LUMIT_SHOVEL);
        getOrCreateTagBuilder(ItemTags.HOES).add(ModItems.NETIAMOND_HOE).add(ModItems.LUZZANTUM_HOE).add(ModItems.MYTHRIL_HOE).add(ModItems.LUMIT_HOE);

        getOrCreateTagBuilder(ItemTags.HEAD_ARMOR)
        .add(ModItems.NETIAMOND_HELMET)
        .add(ModItems.LUZZANTUM_HELMET)
        .add(ModItems.MYTHRIL_HELMET)
        .add(ModItems.LUMIT_HELMET);
        getOrCreateTagBuilder(ItemTags.HEAD_ARMOR_ENCHANTABLE)
        .add(ModItems.NETIAMOND_HELMET)
        .add(ModItems.LUZZANTUM_HELMET)
        .add(ModItems.MYTHRIL_HELMET)
        .add(ModItems.LUMIT_HELMET);

        getOrCreateTagBuilder(ItemTags.CHEST_ARMOR)
        .add(ModItems.NETIAMOND_CHESTPLATE)
        .add(ModItems.LUZZANTUM_CHESTPLATE)
        .add(ModItems.MYTHRIL_CHESTPLATE)
        .add(ModItems.LUMIT_CHESTPLATE);
        getOrCreateTagBuilder(ItemTags.CHEST_ARMOR_ENCHANTABLE)
        .add(ModItems.NETIAMOND_CHESTPLATE)
        .add(ModItems.LUZZANTUM_CHESTPLATE)
        .add(ModItems.MYTHRIL_CHESTPLATE)
        .add(ModItems.LUMIT_CHESTPLATE);

        getOrCreateTagBuilder(ItemTags.LEG_ARMOR)
        .add(ModItems.NETIAMOND_LEGGINGS)
        .add(ModItems.LUZZANTUM_LEGGINGS)
        .add(ModItems.MYTHRIL_LEGGINGS)
        .add(ModItems.LUMIT_LEGGINGS);
        getOrCreateTagBuilder(ItemTags.LEG_ARMOR_ENCHANTABLE)
        .add(ModItems.NETIAMOND_LEGGINGS)
        .add(ModItems.LUZZANTUM_LEGGINGS)
        .add(ModItems.MYTHRIL_LEGGINGS)
        .add(ModItems.LUMIT_LEGGINGS);

        getOrCreateTagBuilder(ItemTags.FOOT_ARMOR)
        .add(ModItems.NETIAMOND_BOOTS)
        .add(ModItems.LUZZANTUM_BOOTS)
        .add(ModItems.MYTHRIL_BOOTS)
        .add(ModItems.LUMIT_BOOTS);
        getOrCreateTagBuilder(ItemTags.FOOT_ARMOR_ENCHANTABLE)
        .add(ModItems.NETIAMOND_BOOTS)
        .add(ModItems.LUZZANTUM_BOOTS)
        .add(ModItems.MYTHRIL_BOOTS)
        .add(ModItems.LUMIT_BOOTS);

        getOrCreateTagBuilder(ModTags.Items.TRANSFORMABLE_ITEMS)
        .add(Items.DIAMOND);
    }
    
}
