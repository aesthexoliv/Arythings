package net.arclieq.arythings.item.custom;

import java.util.List;
import net.arclieq.arythings.Arythings;
import net.arclieq.arythings.item.ModItems;
import net.arclieq.arythings.util.CounterHelperUtil;
import net.arclieq.arythings.util.CounterHelperUtil.CounterMode;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class NetiamondPickaxeItem extends PickaxeItem {

    public NetiamondPickaxeItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }
    
    // Pickaxe right click method (adds Haste II and NV when SHIFT + Right click, adds ... when CTRL + Right click, etc.)
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.HASTE, 9600, 1, false, true), effect1 = new StatusEffectInstance(StatusEffects.NIGHT_VISION, 9600, 0, false, true);
        ItemStack stack = user.getStackInHand(hand);
        if (user instanceof ServerPlayerEntity player) {
            int getCounter = CounterHelperUtil.getCounterValue(player.getUuid(), "netiamond", world.getServer());
            int seconds = 900 - getCounter / 20;
            int minutes = seconds / 60;
            seconds = seconds - (minutes * 60);

            if (!world.isClient() && stack.getItem() == ModItems.NETIAMOND_PICKAXE) {
                // Holding shift check
                if (Screen.hasShiftDown()) {
                    // 15m cooldown check
                    if (getCounter >= 18000) {
                        user.playSound(SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK);
                        user.addStatusEffect(effect, user);
                        user.addStatusEffect(effect1, user);
                        CounterHelperUtil.setCounter(player.getUuid(), "netiamond", 0, CounterMode.TICK, world.getServer());
                        ItemUsage.consumeHeldItem(world, user, hand);
                    } else {
                        user.playSound(SoundEvents.BLOCK_GLASS_BREAK);
                        user.sendMessage(Text.literal("Please wait " + minutes + "m, " + seconds + "s!").formatted(Arythings.RED), true);
                        Arythings.LOGGER.debug("Netiamond Pickaxe -- " + getCounter + " ticks has passed.");
                    }
                }
            }
        }
        return TypedActionResult.fail(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.literal("Shift + Right click:").formatted(Arythings.AQUA));
            tooltip.add(Text.literal("Haste II 8:00 and Night Vision 8:00.").formatted(Arythings.AQUA));
            tooltip.add(Text.literal("Cooldown: 15m").formatted(Arythings.AQUA));
            tooltip.add(Text.literal(""));
            tooltip.add(Text.literal("Reminder: Netiamond cooldowns are §oALL§r§c connected.").formatted(Arythings.RED));
        } else {
            tooltip.add(Text.literal("Hold SHIFT for more information!").formatted(Arythings.AQUA));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
}