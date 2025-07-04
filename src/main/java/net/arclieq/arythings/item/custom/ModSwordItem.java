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
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
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
        if (user instanceof ServerPlayerEntity player) {
            int counterValue = CounterHelperUtil.getCounterValue(player.getUuid(), "luzzantum", world.getServer());
            
            int seconds = 600 - (counterValue / 20);
            int minutes = seconds / 60;
            seconds = seconds - (minutes * 60);
            // Check for upgraded Luzzantum Sword on the server side
            if (!world.isClient() && stack.getItem() == ModItems.UPGRADED_LUZZANTUM_SWORD) {
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
        if (Screen.hasShiftDown()) {
            if (this.getMaterial() == ModToolMaterials.LUMIT) {
                tooltip.add(Text.literal("This sword contains great power!...").formatted(Arythings.AQUA));
                tooltip.add(Text.literal("yet it is still below Netherite...").formatted(Arythings.AQUA));
                tooltip.add(Text.literal("Can be upgraded with an Astryluna Star.").formatted(Arythings.AQUA));
            } else if (this.getMaterial() == ModToolMaterials.MYTHRIL) {
                tooltip.add(Text.literal("A great starter sword...").formatted(Arythings.AQUA));
                tooltip.add(Text.literal("It's surprisingly good for one...").formatted(Arythings.AQUA));
            } else if (this.getMaterial() == ModToolMaterials.LUZZANTUM) {
                tooltip.add(Text.literal("A great late game sword, but Netherite is still better.").formatted(Arythings.AQUA));
                tooltip.add(Text.literal("It has an extra ability that can be unlocked with an Astryluna Star.").formatted(Arythings.AQUA));
            } else if (stack.getItem() == ModItems.UPGRADED_LUZZANTUM_SWORD) {
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
