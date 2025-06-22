package net.arclieq.arythings.item.custom;

import java.util.List;

import net.arclieq.arythings.Arythings;
import net.arclieq.arythings.item.ModToolMaterials;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

public class ModHoeItem extends HoeItem {
    public static int messageCounter = 0;

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
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if(this.getMaterial() == ModToolMaterials.LUMIT) {
            tooltip.add(Text.literal("No upgrade available!").formatted(Arythings.RED));
        } else if(this.getMaterial() == ModToolMaterials.MYTHRIL) {
            tooltip.add(Text.literal("No upgrade available!").formatted(Arythings.RED));
        } else if(this.getMaterial() == ModToolMaterials.LUZZANTUM) {
            tooltip.add(Text.literal("No upgrade available!").formatted(Arythings.RED));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
    
}
