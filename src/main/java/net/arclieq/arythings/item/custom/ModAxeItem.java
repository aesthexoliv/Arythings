package net.arclieq.arythings.item.custom;

import java.util.List;

import net.arclieq.arythings.Arythings;
import net.arclieq.arythings.item.ModItems;
import net.arclieq.arythings.item.ModToolMaterials;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

public class ModAxeItem extends AxeItem {
    public static int messageCounter = 0;
    public ModAxeItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }
    @Override
    public boolean hasGlint(ItemStack stack) {
        if(this.getMaterial() == ModToolMaterials.LUMIT) {
            return true;
        } else if(this.getMaterial() == ModToolMaterials.LUZZANTUM && stack.getItem() == ModItems.UPGRADED_LUZZANTUM_AXE) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if(Screen.hasShiftDown()) {
            if(this.getMaterial() == ModToolMaterials.LUMIT) {
            tooltip.add(Text.literal("An extra useful tip: you can over-enchant this item...").formatted(Arythings.AQUA));
            tooltip.add(Text.literal("But, a bad thing about doing that,").formatted(Arythings.AQUA));
            tooltip.add(Text.literal("is that it will curse itself if over-enchanted too much.").formatted(Arythings.RED));
            tooltip.add(Text.literal("Be careful!").formatted(Arythings.RED));
        } else if(this.getMaterial() == ModToolMaterials.MYTHRIL) {
            tooltip.add(Text.literal("More good starter gear! Woo...").formatted(Arythings.AQUA));
        } else if(this.getMaterial() == ModToolMaterials.LUZZANTUM) {
            tooltip.add(Text.literal("Deep beyond the nether, you can get the §kAstryluna§r§6 Star to create this item.").formatted(Arythings.AQUA));
            tooltip.add(Text.literal("And, you can upgrade this with it, but... is it really worth it?").formatted(Arythings.AQUA));
        }
        } else {
            tooltip.add(Text.literal("Hold SHIFT for more info!").formatted(Arythings.AQUA));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
    
}
