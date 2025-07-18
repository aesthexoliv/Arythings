package net.arclieq.arythings.item.custom;

import java.util.List;

import net.arclieq.arythings.Arythings;
import net.arclieq.arythings.item.ModItems;
import net.arclieq.arythings.item.ModToolMaterials;
import net.arclieq.arythings.util.CounterHelperUtil;
import net.arclieq.arythings.util.CounterHelperUtil.CounterMode;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ModShovelItem extends ShovelItem {
    
    public ModShovelItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }
    @Override
    public boolean hasGlint(ItemStack stack) {
        if(this.getMaterial() == ModToolMaterials.LUMIT) {
            return true;
        } else if(this.getMaterial() == ModToolMaterials.LUZZANTUM && stack.getItem() == ModItems.UPGRADED_LUZZANTUM_SHOVEL) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(!world.isClient() && stack.getItem() == ModItems.NETIAMOND_SHOVEL) {
            StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.HASTE, 9600, 1, false, true), effect1 = new StatusEffectInstance(StatusEffects.NIGHT_VISION, 9600, 0, false, true);
            int getCounter = CounterHelperUtil.getCounterValue(user.getUuid(), "netiamond", world.getServer());
            int seconds = 900 - getCounter / 20;
		    int minutes = seconds / 60;
		    seconds = seconds - (minutes * 60);
                // Holding shift check
			    if (Screen.hasShiftDown()) {
                    // 15m cooldown check
                    if(getCounter >= 18000) {
                        user.playSound(SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK);
                        user.addStatusEffect(effect, user);
                        user.addStatusEffect(effect1, user);
                        CounterHelperUtil.setCounter(user.getUuid(), "netiamond", 0, CounterMode.TICK, world.getServer());
                        ItemUsage.consumeHeldItem(world, user, hand);
                    } else {
                        user.playSound(SoundEvents.BLOCK_GLASS_BREAK);
                        user.sendMessage(Text.literal("Please wait " + minutes + "m, " + seconds + "s!").formatted(Arythings.RED), true);
                        Arythings.LOGGER.debug("UUID " + user.getUuidAsString() + ": " + getCounter + " tick(s) has passed for counter 'netiamond'.");
                    }
			    }
        }
        return TypedActionResult.fail(user.getStackInHand(hand));
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if(Screen.hasShiftDown()) {
        if (this.getMaterial() == ModToolMaterials.NETIAMOND) {
            tooltip.add(Text.literal("Shift + Right click:").formatted(Arythings.AQUA));
            tooltip.add(Text.literal("Haste II 8:00 and Night Vision 8:00.").formatted(Arythings.AQUA));
            tooltip.add(Text.literal("Cooldown: 15m").formatted(Arythings.AQUA));
            tooltip.add(Text.literal(""));
            tooltip.add(Text.translatable("tooltip.arythings.netiamond_cooldown").formatted(Arythings.RED));
        } else if(this.getMaterial() == ModToolMaterials.LUMIT) {
            tooltip.add(Text.translatable("tooltip.arythings.upgradeable_item").formatted(Arythings.AQUA));
        } else if(this.getMaterial() == ModToolMaterials.MYTHRIL) {
            tooltip.add(Text.literal("A Mythril Shovel a day, keeps the grass away!").formatted(Arythings.AQUA));
        } else if(this.getMaterial() == ModToolMaterials.LUZZANTUM) {
            tooltip.add(Text.literal("Luzzantum Shovel is a pretty fast shovel tool,").formatted(Arythings.AQUA));
            tooltip.add(Text.literal("and it can be upgraded with an Astryluna Star.").formatted(Arythings.AQUA));
        }
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
    
}
