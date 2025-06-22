package net.arclieq.arythings.item.custom;

import java.util.List;

import net.arclieq.arythings.Arythings;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

public class AstrylunaShieldItem extends ShieldItem {

    public AstrylunaShieldItem(Settings settings) {
        super(settings);
    }
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("Created by combining a mythical star, lies this item.").formatted(Arythings.GRAY));
        tooltip.add(Text.literal("This can protect you from your troubles and save you...").formatted(Arythings.GRAY));
        super.appendTooltip(stack, context, tooltip, type);
    }
    
}
