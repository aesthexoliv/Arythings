package net.arclieq.arythings.item.custom;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class LumitItem extends Item{

    public LumitItem(Settings settings) {
        super(settings);
    }
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("Lumit is a rare and powerful material, known for its glowing.").formatted(Formatting.AQUA, Formatting.ITALIC));
        super.appendTooltip(stack, context, tooltip, type);
    }

}
