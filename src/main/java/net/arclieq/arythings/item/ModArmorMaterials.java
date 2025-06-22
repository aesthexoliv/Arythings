package net.arclieq.arythings.item;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorItem.Type;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ModArmorMaterials {
   
   public static final RegistryEntry<ArmorMaterial> NETIAMOND;
   public static final RegistryEntry<ArmorMaterial> MYTHRIL;
   public static final RegistryEntry<ArmorMaterial> LUZZANTUM;
   public static final RegistryEntry<ArmorMaterial> LUMIT;

   public ModArmorMaterials() {
   }

   public static RegistryEntry<ArmorMaterial> getDefault(Registry<ArmorMaterial> registry) {
      return NETIAMOND;
   }

   private static RegistryEntry<ArmorMaterial> register(String id, EnumMap<ArmorItem.Type, Integer> defense, int enchantability, RegistryEntry<SoundEvent> equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
      List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(Identifier.ofVanilla(id)));
      return register(id, defense, enchantability, equipSound, toughness, knockbackResistance, repairIngredient, list);
   }

   private static RegistryEntry<ArmorMaterial> register(String id, EnumMap<ArmorItem.Type, Integer> defense, int enchantability, RegistryEntry<SoundEvent> equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient, List<ArmorMaterial.Layer> layers) {
      EnumMap<ArmorItem.Type, Integer> enumMap = new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class);
      ArmorItem.Type[] var9 = Type.values();
      int var10 = var9.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         ArmorItem.Type type = var9[var11];
         enumMap.put(type, (Integer)defense.get(type));
      }

      return Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.ofVanilla(id), new ArmorMaterial(enumMap, enchantability, equipSound, repairIngredient, layers, toughness, knockbackResistance));
   }

   static {
      LUZZANTUM = register("luzzantum", Util.make(new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class), (EnumMap<ArmorItem.Type, Integer> map) -> {
         map.put(Type.BOOTS, 3);
         map.put(Type.LEGGINGS, 6);
         map.put(Type.CHESTPLATE, 8);
         map.put(Type.HELMET, 3);
         map.put(Type.BODY, 11);
      }), 17, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.6F, 0.1F, () -> {
         return Ingredient.ofItems(new ItemConvertible[]{ModItems.LUZZANTUM_INGOT});
      });
      MYTHRIL = register("mythril", Util.make(new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class), (EnumMap<ArmorItem.Type, Integer> map) -> {
         map.put(Type.BOOTS, 2);
         map.put(Type.LEGGINGS, 5);
         map.put(Type.CHESTPLATE, 7);
         map.put(Type.HELMET, 2);
         map.put(Type.BODY, 9);
      }), 13, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 1.6F, 0.0F, () -> {
         return Ingredient.ofItems(new ItemConvertible[]{ModItems.MYTHRIL_INGOT});
      });
      LUMIT = register("lumit", Util.make(new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class), (EnumMap<ArmorItem.Type, Integer> map) -> {
         map.put(Type.BOOTS, 4);
         map.put(Type.LEGGINGS, 7);
         map.put(Type.CHESTPLATE, 9);
         map.put(Type.HELMET, 4);
         map.put(Type.BODY, 12);
      }), 18, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 3.4F, 0.11F, () -> {
         return Ingredient.ofItems(new ItemConvertible[]{ModItems.LUMIT_SHARD});
      });
      NETIAMOND = register("netiamond", Util.make(new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class), (EnumMap<ArmorItem.Type, Integer> map) -> {
         map.put(Type.BOOTS, 6);
         map.put(Type.LEGGINGS, 9);
         map.put(Type.CHESTPLATE, 11);
         map.put(Type.HELMET, 6);
         map.put(Type.BODY, 14);
      }), 20, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 6.5F, 0.36F, () -> {
         return Ingredient.ofItems(new ItemConvertible[]{ModItems.NETIAMOND});
      });
   }
}
