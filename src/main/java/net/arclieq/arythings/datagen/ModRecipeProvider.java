package net.arclieq.arythings.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.arclieq.arythings.block.ModBlocks;
import net.arclieq.arythings.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    /**
     * Creates a SmithingTransformRecipeJsonBuilder that upgrades an item using an Astryluna Star.
     * <p>This method is used to create recipes that upgrade items to their more powerful versions using the Astryluna Star.</p>
     * @param exporter
     * @param input
     * @param category
     * @param result
     * @param materialItem
     */
    public static void offerAstrylunaSmithingRecipe(RecipeExporter exporter, Item input, RecipeCategory category, Item result, ItemConvertible materialItem) { 
        SmithingTransformRecipeJsonBuilder.create(
        Ingredient.ofItems(ModItems.ASTRYLUNA_STAR),
        Ingredient.ofItems(input), 
        Ingredient.ofItems(materialItem),
        category, 
        result
        ).criterion("has_correct_material", conditionsFromItem(materialItem))
        .offerTo(exporter, getItemPath(result) + "_smithing");
    }

    
    /**
     * Creates a smithing recipe that upgrades an item using a template, input item, and material item.
     * @param exporter
     * @param template
     * @param input
     * @param itemMaterial
     * @param result
     * @param category
     */
    public static void offerSmithingRecipe(RecipeExporter exporter, Item template, Item input, Item itemMaterial, Item result, RecipeCategory category) {
        SmithingTransformRecipeJsonBuilder.create(
            Ingredient.ofItems(template),
            Ingredient.ofItems(input),
            Ingredient.ofItems(itemMaterial),
            category,
            result
        )
        .criterion("has_correct_material", conditionsFromItem(itemMaterial))
        .offerTo(exporter, getItemPath(result) + "_smithing");
    }

    @Override
    public void generate(RecipeExporter exporter) {

        // Important basic items

        Item lz = ModItems.LUZZANTUM_INGOT;
        Item n = ModItems.NETIAMOND;
        Item lm = ModItems.LUMIT_SHARD;
        Item m = ModItems.MYTHRIL_INGOT;
        Item A = ModItems.ASTRYLUNA_STAR;

        // Upgrading recipes & fixing broken items & smithing recipe

        offerAstrylunaSmithingRecipe(exporter, Items.MACE, RecipeCategory.MISC, ModItems.UPGRADED_MACE, Items.HEAVY_CORE);
        offerAstrylunaSmithingRecipe(exporter, ModItems.LUZZANTUM_SWORD, RecipeCategory.MISC, ModItems.UPGRADED_LUZZANTUM_SWORD, lz);
        offerAstrylunaSmithingRecipe(exporter, ModItems.LUZZANTUM_PICKAXE, RecipeCategory.MISC, ModItems.UPGRADED_LUZZANTUM_PICKAXE, lz);
        offerAstrylunaSmithingRecipe(exporter, ModItems.LUZZANTUM_AXE, RecipeCategory.MISC, ModItems.UPGRADED_LUZZANTUM_AXE, lz);
        offerAstrylunaSmithingRecipe(exporter, ModItems.LUZZANTUM_SHOVEL, RecipeCategory.MISC, ModItems.UPGRADED_LUZZANTUM_SHOVEL, lz);

        offerAstrylunaSmithingRecipe(exporter, ModItems.BROKEN_LUZZANTUM_HELMET, RecipeCategory.MISC, ModItems.LUZZANTUM_HELMET, lz);
        offerAstrylunaSmithingRecipe(exporter, ModItems.BROKEN_LUZZANTUM_CHESTPLATE, RecipeCategory.MISC, ModItems.LUZZANTUM_CHESTPLATE, lz);
        offerAstrylunaSmithingRecipe(exporter, ModItems.BROKEN_LUZZANTUM_LEGGINGS, RecipeCategory.MISC, ModItems.LUZZANTUM_LEGGINGS, lz);
        offerAstrylunaSmithingRecipe(exporter, ModItems.BROKEN_LUZZANTUM_BOOTS, RecipeCategory.MISC, ModItems.LUZZANTUM_BOOTS, lz);
        offerAstrylunaSmithingRecipe(exporter, ModItems.BROKEN_NETIAMOND_HELMET, RecipeCategory.MISC, ModItems.NETIAMOND_HELMET, n);
        offerAstrylunaSmithingRecipe(exporter, ModItems.BROKEN_NETIAMOND_CHESTPLATE, RecipeCategory.MISC, ModItems.NETIAMOND_CHESTPLATE, n);
        offerAstrylunaSmithingRecipe(exporter, ModItems.BROKEN_NETIAMOND_LEGGINGS, RecipeCategory.MISC, ModItems.NETIAMOND_LEGGINGS, n);
        offerAstrylunaSmithingRecipe(exporter, ModItems.BROKEN_NETIAMOND_BOOTS, RecipeCategory.MISC, ModItems.NETIAMOND_BOOTS, n);

        offerSmithingRecipe(exporter, /* template: */ Items.AIR, /* input: */ Items.SHIELD, /* itemMaterial: */ ModItems.ASTRYLUNA_STAR, /* result: */ ModItems.ASTRYLUNA_SHIELD, RecipeCategory.MISC);

        // Blasting recipes

        offerBlasting(exporter, List.of(ModBlocks.LUZZANTUM_ORE), RecipeCategory.MISC, lz, 0.5F, 100, "luzzantum_ingot");
        offerBlasting(exporter, List.of(ModBlocks.DEEPSLATE_LUZZANTUM_ORE), RecipeCategory.MISC, lz, 0.7F, 100, "luzzantum_ingot");
        offerBlasting(exporter, List.of(ModBlocks.NETHER_LUZZANTUM_ORE), RecipeCategory.MISC, lz, 0.68F, 100, "luzzantum_ingot");
        offerBlasting(exporter, List.of(ModBlocks.NETHER_NETIAMOND_ORE), RecipeCategory.MISC, n, 0.8F, 100, "netiamond");
        offerBlasting(exporter, List.of(ModBlocks.LUMIT_ORE), RecipeCategory.MISC, lm, 0.48F, 100, "lumit_shard");
        offerBlasting(exporter, List.of(ModBlocks.DEEPSLATE_LUMIT_ORE), RecipeCategory.MISC, lm, 0.57F, 100, "lumit_shard");
        offerBlasting(exporter, List.of(ModBlocks.MYTHRIL_ORE), RecipeCategory.MISC, m, 0.43F, 100, "mythril_ingot");
        offerBlasting(exporter, List.of(ModBlocks.DEEPSLATE_MYTHRIL_ORE), RecipeCategory.MISC, m, 0.56F, 100, "mythril_ingot");

        // Smelting recipes

        offerSmelting(exporter, List.of(ModBlocks.MYTHRIL_ORE), RecipeCategory.MISC, m, 0.56f, 200, "mythril_ingot");
        offerSmelting(exporter, List.of(ModBlocks.DEEPSLATE_MYTHRIL_ORE), RecipeCategory.MISC, m, 0.6f, 200, "mythril_ingot");
        offerSmelting(exporter, List.of(ModBlocks.LUZZANTUM_ORE), RecipeCategory.MISC, lz, 1.0f, 200, "luzzantum_ingot");
        offerSmelting(exporter, List.of(ModBlocks.DEEPSLATE_LUZZANTUM_ORE), RecipeCategory.MISC, lz, 1.3f, 200, "luzzantum_ingot");
        offerSmelting(exporter, List.of(ModBlocks.NETHER_LUZZANTUM_ORE), RecipeCategory.MISC, lz, 1.0f, 200, "luzzantum_ingot");
        offerSmelting(exporter, List.of(ModBlocks.NETHER_NETIAMOND_ORE), RecipeCategory.MISC, n, 1.0f, 200, "netiamond");
        offerSmelting(exporter, List.of(ModBlocks.LUMIT_ORE), RecipeCategory.MISC, lm, 0.61f, 200, "lumit_shard");
        offerSmelting(exporter, List.of(ModBlocks.DEEPSLATE_LUMIT_ORE), RecipeCategory.MISC, lm, 0.73f, 200, "lumit_shard");

        // Compacting recipes (block -> ingot, ingot -> block)

        offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, n, RecipeCategory.DECORATIONS, ModBlocks.NETIAMOND_BLOCK);
        offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, m, RecipeCategory.DECORATIONS, ModBlocks.MYTHRIL_BLOCK);
        offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, lz, RecipeCategory.DECORATIONS, ModBlocks.LUZZANTUM_BLOCK);
        offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, lm, RecipeCategory.DECORATIONS, ModBlocks.LUMIT_BLOCK);

        // Mythril Armor

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.MYTHRIL_CHESTPLATE)
        .pattern("R R")
        .pattern("RRR")
        .pattern("RRR")
        .input('R', m)
        .criterion(hasItem(m), conditionsFromItem(m)).offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.MYTHRIL_LEGGINGS)
        .pattern("RRR")
        .pattern("R R")
        .pattern("R R")
        .input('R', m)
        .criterion(hasItem(m), conditionsFromItem(m)).offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.MYTHRIL_BOOTS)
        .pattern("R R")
        .pattern("R R")
        .input('R', m)
        .criterion(hasItem(m), conditionsFromItem(m)).offerTo(exporter);

        // Lumit Armor

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.LUMIT_HELMET)
        .pattern("LAL")
        .pattern("L L")
        .input('L', lm)
        .input('A', A)
        .criterion(hasItem(lm), conditionsFromItem(lm)).offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.LUMIT_CHESTPLATE)
        .pattern("L L")
        .pattern("LAL")
        .pattern("LLL")
        .input('L', lm)
        .input('A', A)
        .criterion(hasItem(lm), conditionsFromItem(lm)).offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.LUMIT_LEGGINGS)
        .pattern("ALA")
        .pattern("L L")
        .pattern("L L")
        .input('L', lm)
        .input('A', A)
        .criterion(hasItem(lm), conditionsFromItem(lm)).offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.LUMIT_BOOTS)
        .pattern("L L")
        .pattern("A A")
        .input('L', lm)
        .input('A', A)
        .criterion(hasItem(lm), conditionsFromItem(lm)).offerTo(exporter);

    }
    
}
