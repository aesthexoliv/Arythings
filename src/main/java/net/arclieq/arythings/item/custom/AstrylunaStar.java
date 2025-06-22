package net.arclieq.arythings.item.custom;

import java.util.List;

import net.arclieq.arythings.Arythings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

public class AstrylunaStar extends Item {
    
    public AstrylunaStar(Settings settings) {
        super(settings);
    }
    
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("Can be used to fix/upgrade multiple items").formatted(Arythings.GRAY));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
