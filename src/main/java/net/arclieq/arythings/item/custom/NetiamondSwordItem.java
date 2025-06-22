package net.arclieq.arythings.item.custom;

import java.util.List;
import net.arclieq.arythings.Arythings;
import net.arclieq.arythings.item.ModItems;
import net.arclieq.arythings.util.CounterHelperUtil;
import net.arclieq.arythings.util.CounterHelperUtil.CounterMode;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class NetiamondSwordItem extends SwordItem {

    public NetiamondSwordItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    // This is ALL the code for calculating the minutes, seconds, 
    // Adding status effects, checking if you are holding the item and if you are holding SHIFT/CTRL...
    // please help me
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.RESISTANCE, 4800, 1, false, true),
        effect1 = new StatusEffectInstance(StatusEffects.STRENGTH, 9600, 1, false, true),effect2 = new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 3600, 1, false, true),
        effect3 = new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 9600, 0, false, true),effect4 = new StatusEffectInstance(StatusEffects.REGENERATION, 9600, 2, false, true);
        ItemStack stack = user.getStackInHand(hand);
        if(user instanceof ServerPlayerEntity player) {
        int getCounter = CounterHelperUtil.getCounterValue(player.getUuid(), "netiamond", world.getServer());
        
        int seconds = 900 - getCounter / 20;
		int seconds1 = 600 - getCounter / 20;
		int minutes = seconds / 60;
	    int minutes1 = seconds1 / 60;
		seconds1 = seconds1 - (minutes1 * 60);
		seconds = seconds - (minutes * 60);
        // Checks if item is correct and world is server, a.k.a !world.isClient() (I dont understand it...)
        if (!world.isClient() && stack.getItem() == ModItems.NETIAMOND_SWORD) {
            // Holding shift check
			if (Screen.hasShiftDown()) {
                // 10m cooldown check
                if(getCounter >= 12000) {
                    // RESISTANCE + STRENGTH
                    user.playSound(SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK);
                    user.addStatusEffect(effect, user);
                    user.addStatusEffect(effect1, user);
                    CounterHelperUtil.setCounter(player.getUuid(), "netiamond", 0, CounterMode.TICK, world.getServer());
                    
                    ItemUsage.consumeHeldItem(world, user, hand);
                } else {
                        user.playSound(SoundEvents.BLOCK_GLASS_BREAK);
                        user.sendMessage(Text.literal("Please wait " + minutes1 + "m, " + seconds1 + "s!").formatted(Arythings.RED), true);
                        Arythings.LOGGER.debug("Netiamond Sword -- " + getCounter + " ticks has passed.");
                }
			} else {
                // Holding CTRL check
                if(Screen.hasControlDown()) {
                    // 15m cooldown check
                    if(getCounter >= 18000) {
                        user.playSound(SoundEvents.BLOCK_AMETHYST_CLUSTER_FALL);
                        user.addStatusEffect(effect2, user);
                        user.addStatusEffect(effect3, user);
                        user.addStatusEffect(effect4, user);
                        CounterHelperUtil.setCounter(player.getUuid(), "netiamond", 0, CounterMode.TICK, world.getServer());
                        
                        ItemUsage.consumeHeldItem(world, user, hand);
                    } else {
                            user.playSound(SoundEvents.BLOCK_GLASS_BREAK);
                            user.sendMessage(Text.literal("Please wait " + minutes + "m, " + seconds + "s!").formatted(Arythings.RED), true);
                            Arythings.LOGGER.debug("Netiamond Sword -- " + getCounter + " ticks has passed.");
                    }
                }
            }
        }
    }
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
            user.clearStatusEffects();
        });
        return TypedActionResult.fail(stack);
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if(Screen.hasShiftDown()) {
            tooltip.add(Text.literal("CTRL + Right click:").formatted(Arythings.AQUA)); 
            tooltip.add(Text.literal("Health Boost II 3:00, Fire Resistance 8:00, and Regeneration III 8:00.").formatted(Arythings.AQUA));
            tooltip.add(Text.literal("Cooldown: 15m").formatted(Arythings.AQUA));
            tooltip.add(Text.literal(""));
            tooltip.add(Text.literal("Shift + Right click:").formatted(Arythings.AQUA)); 
            tooltip.add(Text.literal("Resistance II 4:00 and Strength II 8:00.").formatted(Arythings.AQUA));
            tooltip.add(Text.literal("Cooldown: 10m").formatted(Arythings.AQUA));
            tooltip.add(Text.literal(""));
            tooltip.add(Text.literal("Reminder: Neitamond cooldowns are §oALL§r§c connected.").formatted(Arythings.RED));        
        } else {
            tooltip.add(Text.literal("Hold SHIFT for more information!").formatted(Arythings.AQUA));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
}