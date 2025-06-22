package net.arclieq.arythings.item.custom;

import java.util.List;
import net.arclieq.arythings.Arythings;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

public class NetiamondHoeItem extends HoeItem {

    public NetiamondHoeItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }
    
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("You have achieved greatness! §c(not)").formatted(Arythings.GREEN));
        tooltip.add(Text.literal("With this item, humanity will thrive!").formatted(Arythings.GREEN));
        super.appendTooltip(stack, context, tooltip, type);
    }
}