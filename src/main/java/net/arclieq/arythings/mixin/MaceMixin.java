package net.arclieq.arythings.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.item.Item;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(Item.class)
public class MaceMixin {
    @Inject(method = "appendTooltip", at = @At("TAIL"))
    private void arythings$appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo info) {
        if((Object)this instanceof MaceItem) {
            tooltip.add(Text.literal("Can be upgraded...").formatted(Formatting.BLUE));
        }   
    }
}
