package net.arclieq.arythings.item.custom;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.arclieq.arythings.Arythings;
import net.arclieq.arythings.item.ModArmorMaterials;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class ModArmorItem extends ArmorItem {
private static final Map<RegistryEntry<ArmorMaterial>, List<StatusEffectInstance>> MATERIAL_TO_EFFECT_MAP =
            (new ImmutableMap.Builder<RegistryEntry<ArmorMaterial>, List<StatusEffectInstance>>())
                    .put(ModArmorMaterials.NETIAMOND,
                            List.of(new StatusEffectInstance(StatusEffects.SPEED, 1, 0, false, true),
                                    new StatusEffectInstance(StatusEffects.RESISTANCE, 1, 1, false, true)))
                    .put(ModArmorMaterials.LUZZANTUM,
                            List.of(new StatusEffectInstance(StatusEffects.RESISTANCE, 1, 0, false, true),
                                    new StatusEffectInstance(StatusEffects.STRENGTH, 1, 0, false, true)))
                    .put(ModArmorMaterials.LUMIT,
                            List.of(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 1, 0, false, true),
                                    new StatusEffectInstance(StatusEffects.REGENERATION, 1, 1, false, true)))
                    .put(ModArmorMaterials.MYTHRIL, 
                            List.of(new StatusEffectInstance(StatusEffects.REGENERATION, 1, 0, false, true))).build();

    public ModArmorItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, settings);
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return this.getMaterial() == ModArmorMaterials.LUMIT || super.hasGlint(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(!world.isClient()) {
            if(entity instanceof PlayerEntity player) {
                if(hasFullSuitOfArmorOn(player)) {
                    evaluateArmorEffects(player);
                    if(hasCorrectArmorOn(ModArmorMaterials.NETIAMOND, player)) {
                        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(24);
                    }
                } else if(player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).getBaseValue() == 24 && !hasFullSuitOfArmorOn(player) && !hasCorrectArmorOn(ModArmorMaterials.NETIAMOND, player)) {
                    player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20);
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private void evaluateArmorEffects(PlayerEntity player) {
        for (Map.Entry<RegistryEntry<ArmorMaterial>, List<StatusEffectInstance>> entry : MATERIAL_TO_EFFECT_MAP.entrySet()) {
            RegistryEntry<ArmorMaterial> mapArmorMaterial = entry.getKey();
            List<StatusEffectInstance> mapStatusEffects = entry.getValue();

            if(hasCorrectArmorOn(mapArmorMaterial, player)) {
                addStatusEffectForMaterial(player, mapArmorMaterial, mapStatusEffects);
            }
        }
    }

    private void addStatusEffectForMaterial(PlayerEntity player, RegistryEntry<ArmorMaterial> mapArmorMaterial, List<StatusEffectInstance> mapStatusEffect) {
        boolean hasPlayerEffect = mapStatusEffect.stream().allMatch(statusEffectInstance -> player.hasStatusEffect(statusEffectInstance.getEffectType()));

        if(!hasPlayerEffect) {
            for (StatusEffectInstance instance : mapStatusEffect) {
                player.addStatusEffect(new StatusEffectInstance(instance.getEffectType(),
                        instance.getDuration(), instance.getAmplifier(), instance.isAmbient(), instance.shouldShowParticles()));
            }
        }
    }

    private boolean hasFullSuitOfArmorOn(PlayerEntity player) {
        ItemStack boots = player.getInventory().getArmorStack(0);
        ItemStack leggings = player.getInventory().getArmorStack(1);
        ItemStack breastplate = player.getInventory().getArmorStack(2);
        ItemStack helmet = player.getInventory().getArmorStack(3);

        return !helmet.isEmpty() && !breastplate.isEmpty()
                && !leggings.isEmpty() && !boots.isEmpty();
    }

    private boolean hasCorrectArmorOn(RegistryEntry<ArmorMaterial> material, PlayerEntity player) {
        for (ItemStack armorStack: player.getInventory().armor) {
            if(!(armorStack.getItem() instanceof ArmorItem)) {
                return false;
            }
        }

        ArmorItem boots = ((ArmorItem)player.getInventory().getArmorStack(0).getItem());
        ArmorItem leggings = ((ArmorItem)player.getInventory().getArmorStack(1).getItem());
        ArmorItem breastplate = ((ArmorItem)player.getInventory().getArmorStack(2).getItem());
        ArmorItem helmet = ((ArmorItem)player.getInventory().getArmorStack(3).getItem());

        return helmet.getMaterial() == material && breastplate.getMaterial() == material &&
                leggings.getMaterial() == material && boots.getMaterial() == material;
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if(Screen.hasShiftDown()) {
        if(this.getMaterial() == ModArmorMaterials.LUZZANTUM) {
            tooltip.add(Text.literal("Full set bonus: Resistance I, Strength I").formatted(Arythings.AQUA));
        } else if(this.getMaterial() == ModArmorMaterials.MYTHRIL) {
            tooltip.add(Text.literal("Full set bonus: Regeneration I").formatted(Arythings.AQUA));
        } else if(this.getMaterial() == ModArmorMaterials.LUMIT) {
            tooltip.add(Text.literal("Full set bonus: Fire Resistance I, Regeneration II").formatted(Arythings.AQUA));
        } else if(this.getMaterial() == ModArmorMaterials.NETIAMOND) {
            tooltip.add(Text.literal("Full set bonus: Speed I, Resistance II, +2 extra hearts").formatted(Arythings.AQUA));
        }
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
}
