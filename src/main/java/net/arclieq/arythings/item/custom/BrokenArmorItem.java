package net.arclieq.arythings.item.custom;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BrokenArmorItem extends Item {

    public BrokenArmorItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("A broken armor type... How can it be fixed?").formatted(Formatting.AQUA));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
