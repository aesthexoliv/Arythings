package net.arclieq.arythings.item;

import com.google.common.base.Suppliers;
import java.util.Objects;
import java.util.function.Supplier;

import net.arclieq.arythings.util.ModTags;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;

public enum ModToolMaterials implements ToolMaterial {
   NETIAMOND(ModTags.Blocks.INCORRECT_FOR_NETIAMOND_TOOL, 3592, 13.0F, 5.5F, 20, () -> {
      return Ingredient.ofItems(new ItemConvertible[]{ModItems.NETIAMOND});
   }),
   LUMIT(ModTags.Blocks.INCORRECT_FOR_LUMIT_TOOL, 2076, 8.9F, 4.1F, 18, () -> {
      return Ingredient.ofItems(new ItemConvertible[]{ModItems.LUMIT_SHARD});
   }),
   MYTHRIL(ModTags.Blocks.INCORRECT_FOR_MYTHRIL_TOOL, 1535, 6.8F, 3.0F, 13, () -> {
      return Ingredient.ofItems(new ItemConvertible[]{ModItems.MYTHRIL_INGOT});
   }),
   LUZZANTUM(ModTags.Blocks.INCORRECT_FOR_LUZZANTUM_TOOL, 1731, 8.7F, 3.8F, 17, () -> {
      return Ingredient.ofItems(new ItemConvertible[]{ModItems.LUZZANTUM_INGOT});
   });

   private final TagKey<Block> inverseTag;
   private final int itemDurability;
   private final float miningSpeed;
   private final float attackDamage;
   private final int enchantability;
   private final Supplier<Object> repairIngredient;

   private ModToolMaterials(final TagKey<Block> inverseTag, final int itemDurability, final float miningSpeed, final float attackDamage, final int enchantability, final Supplier<Ingredient> repairIngredient) {
      this.inverseTag = inverseTag;
      this.itemDurability = itemDurability;
      this.miningSpeed = miningSpeed;
      this.attackDamage = attackDamage;
      this.enchantability = enchantability;
      Objects.requireNonNull(repairIngredient);
      this.repairIngredient = Suppliers.memoize(repairIngredient::get);
   }

   public int getDurability() {
      return this.itemDurability;
   }

   public float getMiningSpeedMultiplier() {
      return this.miningSpeed;
   }

   public float getAttackDamage() {
      return this.attackDamage;
   }

   public TagKey<Block> getInverseTag() {
      return this.inverseTag;
   }

   public int getEnchantability() {
      return this.enchantability;
   }

   public Ingredient getRepairIngredient() {
      return (Ingredient)this.repairIngredient.get();
   }
}