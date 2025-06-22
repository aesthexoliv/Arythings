package net.arclieq.arythings.enchant;

import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.value.AddEnchantmentEffect;
import net.minecraft.item.Item;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

public class ModEnchantments {
    public static final RegistryKey<Enchantment> DENREACH = of("denreach");

    public static void bootstrap(Registerable<Enchantment> registry) {
        RegistryEntryLookup<Enchantment> registryEntryLookup2 = registry.getRegistryLookup(RegistryKeys.ENCHANTMENT);
        RegistryEntryLookup<Item> registryEntryLookup3 = registry.getRegistryLookup(RegistryKeys.ITEM);
        // lwk just stole the code from enchantments class LMAO
        register(registry, DENREACH, Enchantment.builder(Enchantment.definition(registryEntryLookup3.getOrThrow(ItemTags.MACE_ENCHANTABLE), 7, 10, 
        Enchantment.leveledCost(25, 13), Enchantment.leveledCost(85, 13), 0, new AttributeModifierSlot[]{AttributeModifierSlot.MAINHAND}))
        .exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE_SET))
        .addEffect(EnchantmentEffectComponentTypes.SMASH_DAMAGE_PER_FALLEN_BLOCK,
        new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(0.75F)))
        .addEffect(EnchantmentEffectComponentTypes.ARMOR_EFFECTIVENESS,
        new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(-0.25F))));

    }

    private static void register(Registerable<Enchantment> registry, RegistryKey<Enchantment> key, Enchantment.Builder builder) {
      registry.register(key, builder.build(key.getValue()));
    }
    
    private static RegistryKey<Enchantment> of(String id) {
      return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.ofVanilla(id));
   }
}