package net.arclieq.arythings.item.custom;

import java.util.List;

import net.arclieq.arythings.Arythings;
import net.arclieq.arythings.item.ModItems;
import net.arclieq.arythings.item.ModToolMaterials;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

public class ModShovelItem extends ShovelItem {
    public static int messageCounter = 0;
    public ModShovelItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }
    @Override
    public boolean hasGlint(ItemStack stack) {
        if(this.getMaterial() == ModToolMaterials.LUMIT) {
            return true;
        } else if(this.getMaterial() == ModToolMaterials.LUZZANTUM && stack.getItem() == ModItems.UPGRADED_LUZZANTUM_SHOVEL) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if(Screen.hasShiftDown()) {
            if(this.getMaterial() == ModToolMaterials.LUMIT) {
            tooltip.add(Text.literal("You can upgrade it with an §kAstryluna§r§6 Star.").formatted(Arythings.AQUA));
        } else if(this.getMaterial() == ModToolMaterials.MYTHRIL) {
            tooltip.add(Text.literal("A Mythril Shovel a day, keeps the grass away!").formatted(Arythings.AQUA));
            tooltip.add(Text.literal("Upgrade-able with an §kAstryluna§r§6 Star.").formatted(Arythings.AQUA));
        } else if(this.getMaterial() == ModToolMaterials.LUZZANTUM) {
            tooltip.add(Text.literal("Luzzantum Shovel is a useful item that is pretty fast at mining grass.").formatted(Arythings.AQUA));
            tooltip.add(Text.literal("It can be upgraded with an §kAstryluna§r§6 Star.").formatted(Arythings.AQUA));
        }
        } else {
            tooltip.add(Text.literal("Hold SHIFT for more info!").formatted(Arythings.AQUA));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
    
}
