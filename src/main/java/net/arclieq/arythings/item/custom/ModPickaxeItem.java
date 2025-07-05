package net.arclieq.arythings.item.custom;

import java.util.List;

import net.arclieq.arythings.Arythings;
import net.arclieq.arythings.util.CounterHelperUtil;
import net.arclieq.arythings.util.CounterHelperUtil.CounterMode;
import net.arclieq.arythings.item.ModItems;
import net.arclieq.arythings.item.ModToolMaterials;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ModPickaxeItem extends PickaxeItem {

    public ModPickaxeItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }
    @Override
    public boolean hasGlint(ItemStack stack) {
        if(this.getMaterial() == ModToolMaterials.LUMIT) {
            return true;
        } else if(this.getMaterial() == ModToolMaterials.LUZZANTUM && stack.getItem() == ModItems.UPGRADED_LUZZANTUM_PICKAXE) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(!world.isClient()) {
            if(this.getMaterial() == ModToolMaterials.LUMIT) {
                if(stack.getItem() == ModItems.LUMIT_PICKAXE && selected) {
                    if(entity instanceof PlayerEntity player) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 1, 1, false, true, true), player);
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 1, 0, false, true, true), player);
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 1, 0, false, true, true), player);
                    }
                }
            } else if(this.getMaterial() == ModToolMaterials.LUZZANTUM && stack.getItem() == ModItems.UPGRADED_LUZZANTUM_PICKAXE && selected) {
                    if(entity instanceof PlayerEntity player) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 0, 0, false, true, true), player);
                    }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user instanceof ServerPlayerEntity player) {
            int counterValue = CounterHelperUtil.getCounterValue(player.getUuid(), "luzzantum", world.getServer());
            int seconds = 720 - CounterHelperUtil.getCounterValue(player.getUuid(), "luzzantum", world.getServer()) / 20;
            int minutes = seconds / 60;
            seconds = seconds - (minutes * 60);
            if (!world.isClient() && stack.getItem() == ModItems.UPGRADED_LUZZANTUM_PICKAXE) {
                if (counterValue >= 14400) {
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 9600, 0, false, true, true), user);
                    CounterHelperUtil.setCounter(player.getUuid(), "luzzantum", 0, CounterMode.TICK, world.getServer());
                    return TypedActionResult.success(stack);
                } else {
                    user.sendMessage(Text.literal("You need to wait " + minutes + "m" + seconds + "s" + "!").formatted(Arythings.RED), true);
                }
            }
        }
        return TypedActionResult.fail(stack);
    }
    
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if(Screen.hasShiftDown()) {
            if(this.getMaterial() == ModToolMaterials.LUMIT) {
                tooltip.add(Text.literal("It shines in the dark, with its mysterious glowing properties...").formatted(Arythings.AQUA));
                tooltip.add(Text.literal("It is said that whoever has this item... can have haste,").formatted(Arythings.AQUA));
                tooltip.add(Text.literal("but... it's unknown what other consenquences it could behold...").formatted(Arythings.RED));
                tooltip.add(Text.literal("Can be upgraded with an Astryluna Star.").formatted(Arythings.RED));
            } else if(this.getMaterial() == ModToolMaterials.MYTHRIL) {
                tooltip.add(Text.literal("It's just another starter pickaxe at the end of the day...").formatted(Arythings.AQUA));
            } else if(this.getMaterial() == ModToolMaterials.LUZZANTUM) {
                tooltip.add(Text.literal("Deep beyond the depths of the nether, you can find the §kAstryluna§r§6 Star...").formatted(Arythings.AQUA));
                tooltip.add(Text.literal("Which, is used to create this and other items.").formatted(Arythings.AQUA));
                tooltip.add(Text.literal("And... once you create an item, you can also upgrade it, but it has a downside;").formatted(Arythings.AQUA));
                tooltip.add(Text.literal("Most upgraded items will have weird effects...").formatted(Arythings.RED));
            } else if(stack.getItem() == ModItems.UPGRADED_LUZZANTUM_PICKAXE) {
                tooltip.add(Text.literal("Upgraded Luzzantum Pickaxe: Gives you Haste I while holding in main hand,").formatted(Arythings.AQUA));
                tooltip.add(Text.literal("And gives you Speed I, Resistance II for 8m (Right click)").formatted(Arythings.RED));
                tooltip.add(Text.literal("Cooldown: 12m").formatted(Arythings.RED));
                tooltip.add(Text.literal("Reminder: Upgraded Luzzantum material item(s)' cooldown are connected.").formatted(Arythings.RED));
            }
        } else {
            tooltip.add(Text.literal("Hold SHIFT for more info!").formatted(Arythings.AQUA));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
}
