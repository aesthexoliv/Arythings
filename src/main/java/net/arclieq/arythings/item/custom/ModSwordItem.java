package net.arclieq.arythings.item.custom;

import java.util.List;

import net.arclieq.arythings.Arythings;
import net.arclieq.arythings.util.CounterHelperUtil;
import net.arclieq.arythings.util.CounterHelperUtil.CounterMode;
import net.arclieq.arythings.item.ModItems;
import net.arclieq.arythings.item.ModToolMaterials;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ModSwordItem extends SwordItem {

    public ModSwordItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }
    @Override
    public boolean hasGlint(ItemStack stack) {
        if(this.getMaterial() == ModToolMaterials.LUMIT) {
            return true;
        } else if(this.getMaterial() == ModToolMaterials.LUZZANTUM && stack.getItem() == ModItems.UPGRADED_LUZZANTUM_SWORD) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        // Check for Upgraded Luzzantum Sword on the server side
        if (!world.isClient() && stack.getItem() == ModItems.UPGRADED_LUZZANTUM_SWORD) {
            int counterValue = CounterHelperUtil.getCounterValue(user.getUuid(), "luzzantum", world.getServer());
            
            int seconds = 600 - (counterValue / 20);
            int minutes = seconds / 60;
            seconds = seconds - (minutes * 60);
            if (counterValue >= 12000) {
                int random = (int) (Math.random() * 2) + 1;
                switch (random) {
                    case 1:
                        user.sendMessage(Text.literal("You have been granted Strength II for 5 minutes!").formatted(Arythings.GREEN), true);
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 6000, 1, false, true, true), user);
                        break;
                    case 2:
                        user.sendMessage(Text.literal("You have been granted Speed II for 5 minutes!").formatted(Arythings.GREEN), true);
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 6000, 1, false, true, true), user);
                        break;
                    }
                CounterHelperUtil.setCounter(user.getUuid(), "luzzantum", 0, CounterMode.TICK, world.getServer());

                return TypedActionResult.success(stack);
            } else {
                user.sendMessage(Text.literal("You need to wait " + minutes + "m" + seconds + "s" + "!").formatted(Arythings.RED), true);
                Arythings.LOGGER.debug("UUID " + user.getUuidAsString() + ": " + counterValue + " tick(s) has passed for counter 'luzzantum'.");
            }
        }

        // Checks if item is netiamond sword and world is server
        if(!world.isClient() && stack.getItem() == ModItems.NETIAMOND_SWORD) {
            StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.RESISTANCE, 4800, 1, false, true),
            effect1 = new StatusEffectInstance(StatusEffects.STRENGTH, 9600, 1, false, true),effect2 = new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 3600, 1, false, true),
            effect3 = new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 9600, 0, false, true),effect4 = new StatusEffectInstance(StatusEffects.REGENERATION, 9600, 2, false, true);

            int getCounter = CounterHelperUtil.getCounterValue(user.getUuid(), "netiamond", world.getServer());
            int seconds = 900 - getCounter / 20;
		    int seconds1 = 600 - getCounter / 20;
		    int minutes = seconds / 60;
	        int minutes1 = seconds1 / 60;
		    seconds1 = seconds1 - (minutes1 * 60);
		    seconds = seconds - (minutes * 60);
            
                // Holding shift check
			    if (Screen.hasShiftDown()) {
                    // 10m cooldown check
                    if(getCounter >= 12000) {
                        // RESISTANCE + STRENGTH
                        user.playSound(SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK);
                        user.addStatusEffect(effect, user);
                        user.addStatusEffect(effect1, user);
                        CounterHelperUtil.setCounter(user.getUuid(), "netiamond", 0, CounterMode.TICK, world.getServer());
                        ItemUsage.consumeHeldItem(world, user, hand);
                    } else {
                        user.playSound(SoundEvents.BLOCK_GLASS_BREAK);
                        user.sendMessage(Text.literal("Please wait " + minutes1 + "m, " + seconds1 + "s!").formatted(Arythings.RED), true);
                        Arythings.LOGGER.debug("UUID " + user.getUuidAsString() + ": " + getCounter + " tick(s) has passed for counter 'netiamond'.");
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
                            CounterHelperUtil.setCounter(user.getUuid(), "netiamond", 0, CounterMode.TICK, world.getServer());
                            ItemUsage.consumeHeldItem(world, user, hand);
                        } else {
                            user.playSound(SoundEvents.BLOCK_GLASS_BREAK);
                            user.sendMessage(Text.literal("Please wait " + minutes + "m, " + seconds + "s!").formatted(Arythings.RED), true);
                            Arythings.LOGGER.debug("UUID " + user.getUuidAsString() + ": " + getCounter + " tick(s) has passed for counter 'netiamond'.");
                        }
                    }
                }
        }

        return TypedActionResult.fail(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (Screen.hasShiftDown()) {
            if (this.getMaterial() == ModToolMaterials.NETIAMOND) {
                tooltip.add(Text.literal("CTRL + Right click:").formatted(Arythings.AQUA)); 
                tooltip.add(Text.literal("Health Boost II 3:00, Fire Resistance 8:00, and Regeneration III 8:00.").formatted(Arythings.AQUA));
                tooltip.add(Text.literal("Cooldown: 15m").formatted(Arythings.AQUA));
                tooltip.add(Text.literal(""));
                tooltip.add(Text.literal("Shift + Right click:").formatted(Arythings.AQUA)); 
                tooltip.add(Text.literal("Resistance II 4:00 and Strength II 8:00.").formatted(Arythings.AQUA));
                tooltip.add(Text.literal("Cooldown: 10m").formatted(Arythings.AQUA));
                tooltip.add(Text.literal(""));
                tooltip.add(Text.translatable("tooltip.arythings.netiamond_cooldown").formatted(Arythings.RED));
            } else if (this.getMaterial() == ModToolMaterials.LUMIT) {
                tooltip.add(Text.literal("This sword contains great power!...").formatted(Arythings.AQUA));
                tooltip.add(Text.literal("yet it is still below Netherite...").formatted(Arythings.AQUA));
                tooltip.add(Text.translatable("tooltip.arythings.upgradeable_item").formatted(Arythings.AQUA));
            } else if (this.getMaterial() == ModToolMaterials.MYTHRIL) {
                tooltip.add(Text.literal("A great starter sword...").formatted(Arythings.AQUA));
                tooltip.add(Text.literal("It's surprisingly good for one...").formatted(Arythings.AQUA));
            } else if (this.getMaterial() == ModToolMaterials.LUZZANTUM) {
                tooltip.add(Text.literal("A great late game sword, but Netherite is still better.").formatted(Arythings.AQUA));
                tooltip.add(Text.literal("It has an extra ability that can be unlocked with an Astryluna Star.").formatted(Arythings.AQUA));
            } else if (stack.isOf(ModItems.UPGRADED_LUZZANTUM_SWORD)) {
                tooltip.add(Text.literal("Upgrade:").formatted(Arythings.RED));
                tooltip.add(Text.literal("Right click to roll Strength II for 5 minutes,").formatted(Arythings.RED));
                tooltip.add(Text.literal("or Speed II for 5 minutes!").formatted(Arythings.RED));
                tooltip.add(Text.literal("Cooldown: 10m").formatted(Arythings.RED));
                tooltip.add(Text.literal("Reminder: Upgraded material item(s)' cooldown are connected.").formatted(Arythings.RED));
            }
        } else {
            tooltip.add(Text.literal("Hold SHIFT for more info!").formatted(Arythings.AQUA));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }

}
