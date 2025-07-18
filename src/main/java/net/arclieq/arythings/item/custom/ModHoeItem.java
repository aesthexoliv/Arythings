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
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ModHoeItem extends HoeItem {
    
    public ModHoeItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }
    @Override
    public boolean hasGlint(ItemStack stack) {
        if(this.getMaterial() == ModToolMaterials.LUMIT) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(!world.isClient() && stack.getItem() == ModItems.NETIAMOND_HOE) {
            StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.HASTE, 9600, 0, false, true),
                effect2 = new StatusEffectInstance(StatusEffects.LUCK, 4800, 0, false, true);
            int counterValue = CounterHelperUtil.getCounterValue(user.getUuid(), "netiamond", world.getServer());
            int seconds = 600 - counterValue / 20;
            int minutes = counterValue / 60;
            seconds = seconds - (minutes * 60);

            if(counterValue > 12000) {
                user.playSound(SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK);
                user.addStatusEffect(effect);
                user.addStatusEffect(effect2);
                CounterHelperUtil.setCounter(user.getUuid(), "netiamond", 0, CounterMode.TICK, world.getServer());
            } else {
                user.playSound(SoundEvents.BLOCK_GLASS_BREAK);
                user.sendMessage(Text.literal("Wait " + minutes + "m, " + seconds + "s!"));
                Arythings.LOGGER.debug("UUID " + user.getUuidAsString() + ": " + counterValue + " tick(s) has passed for counter 'netiamond'.");
            }
        }
        return TypedActionResult.fail(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if(Screen.hasShiftDown()) {
        if (this.getMaterial() == ModToolMaterials.NETIAMOND) {
            tooltip.add(Text.literal("Shift + Right click: Haste I 8:00, Luck I 4:00").formatted(Arythings.GREEN));
            tooltip.add(Text.literal("Cooldown: 10m").formatted(Arythings.RED));
            tooltip.add(Text.translatable("tooltip.arythings.netiamond_cooldown").formatted(Arythings.RED));
        } else if(this.getMaterial() == ModToolMaterials.LUMIT) {
            tooltip.add(Text.translatable("tooltip.arythings.upgradeable_item").formatted(Arythings.AQUA));
        } else if(this.getMaterial() == ModToolMaterials.MYTHRIL) {
            tooltip.add(Text.literal("Farming with a Mythril Hoe... sounds weird, no?").formatted(Arythings.AQUA));
        } else if(this.getMaterial() == ModToolMaterials.LUZZANTUM) {
            tooltip.add(Text.translatable("tooltip.arythings.upgradeable_item").formatted(Arythings.AQUA));
        }
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
    
}
