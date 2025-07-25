package net.arclieq.arythings.item;

import net.arclieq.arythings.Arythings;
import net.arclieq.arythings.item.custom.*;
import net.minecraft.item.*;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.registry.Registries;

/**
 * Registers all custom items for the Arythings mod.
 * Grouped by material/type for clarity.
 */
public class ModItems {
    // Upgraded Mace
     public static final Item UPGRADED_MACE = registerItem("upgraded_mace", new UpgradedMace(new Item.Settings().maxDamage(1210)
                .attributeModifiers(MaceItem.createAttributeModifiers()).rarity(Rarity.EPIC))),

    // --- Netiamond Items --- \\
     NETIAMOND = registerItem("netiamond", new Item(new Item.Settings().fireproof().rarity(Rarity.RARE))),

    // Netiamond Tools
     NETIAMOND_SWORD = registerItem("netiamond_sword",
            new ModSwordItem(ModToolMaterials.NETIAMOND, new Item.Settings().fireproof()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.NETIAMOND, 5, -2.0F))
                    .rarity(Rarity.RARE))),

     NETIAMOND_PICKAXE = registerItem("netiamond_pickaxe",
            new ModPickaxeItem(ModToolMaterials.NETIAMOND, new Item.Settings().fireproof()
                    .attributeModifiers(PickaxeItem.createAttributeModifiers(ModToolMaterials.NETIAMOND, 1.3F, -2.5F))
                    .rarity(Rarity.RARE))),

     NETIAMOND_AXE = registerItem("netiamond_axe",
            new ModAxeItem(ModToolMaterials.NETIAMOND, new Item.Settings().fireproof()
                    .attributeModifiers(AxeItem.createAttributeModifiers(ModToolMaterials.NETIAMOND, 6.0F, -2.8F))
                    .rarity(Rarity.RARE))),

     NETIAMOND_SHOVEL = registerItem("netiamond_shovel",
            new ModShovelItem(ModToolMaterials.NETIAMOND, new Item.Settings().fireproof()
                    .attributeModifiers(ShovelItem.createAttributeModifiers(ModToolMaterials.NETIAMOND, 1.7F, -2.5F))
                    .rarity(Rarity.RARE))),
                    
     NETIAMOND_HOE = registerItem("netiamond_hoe",
            new ModHoeItem(ModToolMaterials.NETIAMOND, new Item.Settings().fireproof()
                    .attributeModifiers(HoeItem.createAttributeModifiers(ModToolMaterials.NETIAMOND, -1.7F, 0F))
                    .rarity(Rarity.RARE))),

    // Netiamond Armor
     NETIAMOND_HELMET = registerItem("netiamond_helmet",
            new ModArmorItem(ModArmorMaterials.NETIAMOND, ArmorItem.Type.HELMET,
                    new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(40)).fireproof()
                    .rarity(Rarity.RARE))),

     NETIAMOND_CHESTPLATE = registerItem("netiamond_chestplate",
            new ModArmorItem(ModArmorMaterials.NETIAMOND, ArmorItem.Type.CHESTPLATE,
                    new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(40)).fireproof()
                    .rarity(Rarity.RARE))),

     NETIAMOND_LEGGINGS = registerItem("netiamond_leggings",
            new ModArmorItem(ModArmorMaterials.NETIAMOND, ArmorItem.Type.LEGGINGS,
                    new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(40)).fireproof()
                    .rarity(Rarity.RARE))),

     NETIAMOND_BOOTS = registerItem("netiamond_boots",
            new ModArmorItem(ModArmorMaterials.NETIAMOND, ArmorItem.Type.BOOTS,
                    new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(40)).fireproof()
                    .rarity(Rarity.RARE))),

    // Broken Netiamond Armor
     BROKEN_NETIAMOND_HELMET = registerItem("broken_netiamond_helmet", new BrokenArmorItem(new Item.Settings())),
     BROKEN_NETIAMOND_CHESTPLATE = registerItem("broken_netiamond_chestplate", new BrokenArmorItem(new Item.Settings())),
     BROKEN_NETIAMOND_LEGGINGS = registerItem("broken_netiamond_leggings", new BrokenArmorItem(new Item.Settings())),
     BROKEN_NETIAMOND_BOOTS = registerItem("broken_netiamond_boots", new BrokenArmorItem(new Item.Settings())),

    // --- Luzzantum Items --- \\
     LUZZANTUM_INGOT = registerItem("luzzantum_ingot", new Item(new Item.Settings()
                        .rarity(Rarity.UNCOMMON))),

    // Luzzantum Tools
     LUZZANTUM_SWORD = registerItem("luzzantum_sword",
            new ModSwordItem(ModToolMaterials.LUZZANTUM, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.LUZZANTUM, 3, -2.3F))
                    .rarity(Rarity.UNCOMMON))),

     LUZZANTUM_PICKAXE = registerItem("luzzantum_pickaxe",
            new ModPickaxeItem(ModToolMaterials.LUZZANTUM, new Item.Settings()
                    .attributeModifiers(PickaxeItem.createAttributeModifiers(ModToolMaterials.LUZZANTUM, 0.8F, -2.5F))
                    .rarity(Rarity.UNCOMMON))),

     LUZZANTUM_AXE = registerItem("luzzantum_axe",
            new ModAxeItem(ModToolMaterials.LUZZANTUM, new Item.Settings()
                    .attributeModifiers(AxeItem.createAttributeModifiers(ModToolMaterials.LUZZANTUM, 4.6F, -3.0F))
                    .rarity(Rarity.UNCOMMON))),

     LUZZANTUM_SHOVEL = registerItem("luzzantum_shovel",
            new ModShovelItem(ModToolMaterials.LUZZANTUM, new Item.Settings()
                    .attributeModifiers(ShovelItem.createAttributeModifiers(ModToolMaterials.LUZZANTUM, 1.2F, -3.0F))
                    .rarity(Rarity.UNCOMMON))),

     LUZZANTUM_HOE = registerItem("luzzantum_hoe",
            new ModHoeItem(ModToolMaterials.LUZZANTUM, new Item.Settings()
                    .attributeModifiers(HoeItem.createAttributeModifiers(ModToolMaterials.LUZZANTUM, -3.6F, 0F))
                    .rarity(Rarity.UNCOMMON))),

    // Upgraded Luzzantum Tools
     UPGRADED_LUZZANTUM_SWORD = registerItem("upgraded_luzzantum_sword",
            new ModSwordItem(ModToolMaterials.LUZZANTUM, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.LUZZANTUM, 4, -2.1F))
                    .rarity(Rarity.RARE))),

     UPGRADED_LUZZANTUM_PICKAXE = registerItem("upgraded_luzzantum_pickaxe",
            new ModPickaxeItem(ModToolMaterials.LUZZANTUM, new Item.Settings()
                    .attributeModifiers(PickaxeItem.createAttributeModifiers(ModToolMaterials.LUZZANTUM, 1.1F, -2.4F))
                    .rarity(Rarity.RARE))),

     UPGRADED_LUZZANTUM_AXE = registerItem("upgraded_luzzantum_axe",
            new ModAxeItem(ModToolMaterials.LUZZANTUM, new Item.Settings()
                    .attributeModifiers(AxeItem.createAttributeModifiers(ModToolMaterials.LUZZANTUM, 5.0F, -2.8F))
                    .rarity(Rarity.RARE))),

     UPGRADED_LUZZANTUM_SHOVEL = registerItem("upgraded_luzzantum_shovel",
            new ModShovelItem(ModToolMaterials.LUZZANTUM, new Item.Settings()
                    .attributeModifiers(ShovelItem.createAttributeModifiers(ModToolMaterials.LUZZANTUM, 1.4F, -2.8F))
                    .rarity(Rarity.RARE))),

     UPGRADED_LUZZANTUM_HOE = registerItem("upgraded_luzzantum_hoe", 
            new ModHoeItem(ModToolMaterials.LUZZANTUM, new Item.Settings()
                    .attributeModifiers(HoeItem.createAttributeModifiers(ModToolMaterials.LUZZANTUM, -3.9F, 0F)))),

    // Luzzantum Armor
     LUZZANTUM_HELMET = registerItem("luzzantum_helmet",
            new ModArmorItem(ModArmorMaterials.LUZZANTUM, ArmorItem.Type.HELMET,
                    new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(24))
                    .rarity(Rarity.UNCOMMON))),

     LUZZANTUM_CHESTPLATE = registerItem("luzzantum_chestplate",
            new ModArmorItem(ModArmorMaterials.LUZZANTUM, ArmorItem.Type.CHESTPLATE,
                    new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(24))
                    .rarity(Rarity.UNCOMMON))),

     LUZZANTUM_LEGGINGS = registerItem("luzzantum_leggings",
            new ModArmorItem(ModArmorMaterials.LUZZANTUM, ArmorItem.Type.LEGGINGS,
                    new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(24))
                    .rarity(Rarity.UNCOMMON))),

     LUZZANTUM_BOOTS = registerItem("luzzantum_boots",
            new ModArmorItem(ModArmorMaterials.LUZZANTUM, ArmorItem.Type.BOOTS,
                    new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(24))
                    .rarity(Rarity.UNCOMMON))),

    // Broken Luzzantum Armor
     BROKEN_LUZZANTUM_HELMET = registerItem("broken_luzzantum_helmet", new BrokenArmorItem(new Item.Settings())),
     BROKEN_LUZZANTUM_CHESTPLATE = registerItem("broken_luzzantum_chestplate", new BrokenArmorItem(new Item.Settings())),
     BROKEN_LUZZANTUM_LEGGINGS = registerItem("broken_luzzantum_leggings", new BrokenArmorItem(new Item.Settings())),
     BROKEN_LUZZANTUM_BOOTS = registerItem("broken_luzzantum_boots", new BrokenArmorItem(new Item.Settings())),

    // --- Lumit Items --- \\
     LUMIT_SHARD = registerItem("lumit_shard", new LumitItem(new Item.Settings().rarity(Rarity.UNCOMMON))),

    // Lumit Tools
     LUMIT_SWORD = registerItem("lumit_sword",
            new ModSwordItem(ModToolMaterials.LUMIT, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.LUMIT, 3, -2.4F))
                    .rarity(Rarity.UNCOMMON))),

     LUMIT_PICKAXE = registerItem("lumit_pickaxe",
            new ModPickaxeItem(ModToolMaterials.LUMIT, new Item.Settings()
                    .attributeModifiers(PickaxeItem.createAttributeModifiers(ModToolMaterials.LUMIT, 0.6F, -2.5F))
                    .rarity(Rarity.UNCOMMON))),

     LUMIT_AXE = registerItem("lumit_axe",
            new ModAxeItem(ModToolMaterials.LUMIT, new Item.Settings()
                    .attributeModifiers(AxeItem.createAttributeModifiers(ModToolMaterials.LUMIT, 5.3F, -3.0F))
                    .rarity(Rarity.UNCOMMON))),

     LUMIT_SHOVEL = registerItem("lumit_shovel",
            new ModShovelItem(ModToolMaterials.LUMIT, new Item.Settings()
                    .attributeModifiers(ShovelItem.createAttributeModifiers(ModToolMaterials.LUMIT, 1.4F, -3.0F))
                    .rarity(Rarity.UNCOMMON))),

     LUMIT_HOE = registerItem("lumit_hoe",
            new ModHoeItem(ModToolMaterials.LUMIT, new Item.Settings()
                    .attributeModifiers(HoeItem.createAttributeModifiers(ModToolMaterials.LUMIT, -3.0F, 0F))
                    .rarity(Rarity.UNCOMMON))),

    // Upgraded Lumit Tools
     UPGRADED_LUMIT_SWORD = registerItem("upgraded_lumit_sword",
            new ModSwordItem(ModToolMaterials.LUMIT, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.LUMIT, 4, -2.1F))
                    .rarity(Rarity.RARE))),

     UPGRADED_LUMIT_PICKAXE = registerItem("upgraded_lumit_pickaxe",
            new ModPickaxeItem(ModToolMaterials.LUMIT, new Item.Settings()
                    .attributeModifiers(PickaxeItem.createAttributeModifiers(ModToolMaterials.LUMIT, 1.1F, -2.4F))
                    .rarity(Rarity.RARE))),

     UPGRADED_LUMIT_AXE = registerItem("upgraded_lumit_axe",
        new ModAxeItem(ModToolMaterials.LUMIT, new Item.Settings()
                .attributeModifiers(AxeItem.createAttributeModifiers(ModToolMaterials.LUMIT, 6.0F, -2.7F))
                .rarity(Rarity.RARE))),

     UPGRADED_LUMIT_SHOVEL = registerItem("upgraded_lumit_shovel",
        new ModShovelItem(ModToolMaterials.LUMIT, new Item.Settings()
                .attributeModifiers(ShovelItem.createAttributeModifiers(ModToolMaterials.LUMIT, 1.6F, -2.7F))
                .rarity(Rarity.RARE))),

     UPGRADED_LUMIT_HOE = registerItem("upgraded_lumit_hoe",
        new ModHoeItem(ModToolMaterials.LUMIT, new Item.Settings()
                .attributeModifiers(HoeItem.createAttributeModifiers(ModToolMaterials.LUMIT, -3.5F, 0F))
                .rarity(Rarity.RARE))),
    

    // Lumit Armor
     LUMIT_HELMET = registerItem("lumit_helmet",
            new ModArmorItem(ModArmorMaterials.LUMIT, ArmorItem.Type.HELMET,
                    new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(32))
                    .rarity(Rarity.UNCOMMON))),

     LUMIT_CHESTPLATE = registerItem("lumit_chestplate",
            new ModArmorItem(ModArmorMaterials.LUMIT, ArmorItem.Type.CHESTPLATE,
                    new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(32))
                    .rarity(Rarity.UNCOMMON))),

     LUMIT_LEGGINGS = registerItem("lumit_leggings",
            new ModArmorItem(ModArmorMaterials.LUMIT, ArmorItem.Type.LEGGINGS,
                    new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(32))
                    .rarity(Rarity.UNCOMMON))),

     LUMIT_BOOTS = registerItem("lumit_boots",
            new ModArmorItem(ModArmorMaterials.LUMIT, ArmorItem.Type.BOOTS,
                    new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(32))
                    .rarity(Rarity.UNCOMMON))),

    // --- Mythril Items --- \\
     MYTHRIL_INGOT = registerItem("mythril_ingot", new Item(new Item.Settings())),

    // Mythril Tools
     MYTHRIL_SWORD = registerItem("mythril_sword",
            new ModSwordItem(ModToolMaterials.MYTHRIL, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.MYTHRIL, 3, -2.4F)))),

     MYTHRIL_PICKAXE = registerItem("mythril_pickaxe",
            new ModPickaxeItem(ModToolMaterials.MYTHRIL, new Item.Settings()
                    .attributeModifiers(PickaxeItem.createAttributeModifiers(ModToolMaterials.MYTHRIL, 0.6F, -2.5F)))),

     MYTHRIL_AXE = registerItem("mythril_axe",
            new ModAxeItem(ModToolMaterials.MYTHRIL, new Item.Settings()
                    .attributeModifiers(AxeItem.createAttributeModifiers(ModToolMaterials.MYTHRIL, 5.3F, -3.0F)))),

     MYTHRIL_SHOVEL = registerItem("mythril_shovel",
            new ModShovelItem(ModToolMaterials.MYTHRIL, new Item.Settings()
                    .attributeModifiers(ShovelItem.createAttributeModifiers(ModToolMaterials.MYTHRIL, 1.0F, -3.0F)))),

     MYTHRIL_HOE = registerItem("mythril_hoe",
            new ModHoeItem(ModToolMaterials.MYTHRIL, new Item.Settings()
                    .attributeModifiers(HoeItem.createAttributeModifiers(ModToolMaterials.MYTHRIL, -3.0F, 0F)))),

    // Mythril Armor
     MYTHRIL_HELMET = registerItem("mythril_helmet",
            new ModArmorItem(ModArmorMaterials.MYTHRIL, ArmorItem.Type.HELMET,
                    new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(30)))),
     MYTHRIL_CHESTPLATE = registerItem("mythril_chestplate",
            new ModArmorItem(ModArmorMaterials.MYTHRIL, ArmorItem.Type.CHESTPLATE,
                    new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(30)))),
     MYTHRIL_LEGGINGS = registerItem("mythril_leggings",
            new ModArmorItem(ModArmorMaterials.MYTHRIL, ArmorItem.Type.LEGGINGS,
                    new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(30)))),
     MYTHRIL_BOOTS = registerItem("mythril_boots",
            new ModArmorItem(ModArmorMaterials.MYTHRIL, ArmorItem.Type.BOOTS,
                    new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(30)))),


    // --- Zazum Items --- \\

     ZAZUM_INGOT = registerItem("zazum_ingot", new Item(new Item.Settings().rarity(Rarity.UNCOMMON))),

    // Zazum Tools (will change attribute modifiers later)
     ZAZUM_SWORD = registerItem("zazum_sword", 
                new ModSwordItem(ModToolMaterials.ZAZUM, new Item.Settings()
                        .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.ZAZUM, 0, 0)).rarity(Rarity.UNCOMMON))),

     ZAZUM_PICKAXE = registerItem("zazum_pickaxe", 
                new ModPickaxeItem(ModToolMaterials.ZAZUM, new Item.Settings()
                        .attributeModifiers(PickaxeItem.createAttributeModifiers(ModToolMaterials.ZAZUM, 0, 0)).rarity(Rarity.UNCOMMON))),

     ZAZUM_AXE = registerItem("zazum_axe", 
                new ModAxeItem(ModToolMaterials.ZAZUM, new Item.Settings()
                        .attributeModifiers(AxeItem.createAttributeModifiers(ModToolMaterials.ZAZUM, 0, 0)).rarity(Rarity.UNCOMMON))),

     ZAZUM_SHOVEL = registerItem("zazum_shovel", 
                new ModShovelItem(ModToolMaterials.ZAZUM, new Item.Settings()
                        .attributeModifiers(ShovelItem.createAttributeModifiers(ModToolMaterials.ZAZUM, 0, 0)).rarity(Rarity.UNCOMMON))),

     ZAZUM_HOE = registerItem("zazum_hoe", 
                new ModHoeItem(ModToolMaterials.ZAZUM, new Item.Settings()
                        .attributeModifiers(HoeItem.createAttributeModifiers(ModToolMaterials.ZAZUM, 0, 0)).rarity(Rarity.UNCOMMON))),

    // Zazum Armor (will change max damage later)
     ZAZUM_HELMET = registerItem("zazum_helmet",
            new ModArmorItem(ModArmorMaterials.ZAZUM, ArmorItem.Type.HELMET,
                    new Item.Settings().maxDamage(0).rarity(Rarity.UNCOMMON))),

     ZAZUM_CHESTPLATE = registerItem("zazum_chestplate",
            new ModArmorItem(ModArmorMaterials.ZAZUM, ArmorItem.Type.CHESTPLATE,
                    new Item.Settings().maxDamage(0).rarity(Rarity.UNCOMMON))),

     ZAZUM_LEGGINGS = registerItem("zazum_leggings",
            new ModArmorItem(ModArmorMaterials.ZAZUM, ArmorItem.Type.LEGGINGS,
                    new Item.Settings().maxDamage(0).rarity(Rarity.UNCOMMON))),

     ZAZUM_BOOTS = registerItem("zazum_boots",
            new ModArmorItem(ModArmorMaterials.ZAZUM, ArmorItem.Type.BOOTS,
                    new Item.Settings().maxDamage(0).rarity(Rarity.UNCOMMON))),

    // --- Special Items ---
     ASTRYLUNA_STAR = registerItem("astryluna_star", new AstrylunaStar(new Item.Settings().rarity(Rarity.EPIC))),

     ASTRYLUNA_SHIELD = registerItem("astryluna_shield", new AstrylunaShieldItem(new Item.Settings()
             .maxDamage(794).rarity(Rarity.EPIC)));
             
    // --- Registration Helper ---
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Arythings.MOD_ID, name), item);
    }

    /**
     * Call this in your mod initializer to register all items.
     */
    public static void registerModItems() {
        Arythings.LOGGER.debug("Registering items for " + Arythings.MOD_ID + "...");
    }
}