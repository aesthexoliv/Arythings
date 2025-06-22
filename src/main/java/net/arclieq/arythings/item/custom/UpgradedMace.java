package net.arclieq.arythings.item.custom;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class UpgradedMace extends MaceItem {

   public UpgradedMace(net.minecraft.item.Item.Settings settings) {
      super(settings);
   }

   @Override
   public boolean hasGlint(ItemStack stack) {
       return true;
   }
   @Override
   public boolean isEnchantable(ItemStack stack) {
       return true;
   }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("Awaiting usages...").formatted(Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, type);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
      ItemStack itemStack = user.getMainHandStack();
      float yaw = user.getYaw(), pitch = user.getPitch(), f = 2.0f;
      double motionX = (double)(-MathHelper.sin(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI) * f);
      double motionZ = (double)(MathHelper.cos(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI) * f);
      user.addVelocity(0, 2, 0);
      user.setVelocity(motionX, 0, motionZ);
      return TypedActionResult.success(itemStack, world.isClient());
   }
}
