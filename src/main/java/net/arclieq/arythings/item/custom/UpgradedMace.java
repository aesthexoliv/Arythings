package net.arclieq.arythings.item.custom;

import java.util.List;

import net.arclieq.arythings.Arythings;
import net.arclieq.arythings.item.ModItems;
import net.arclieq.arythings.util.CounterHelperUtil;
import net.arclieq.arythings.util.CounterHelperUtil.CounterMode;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
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
        if(Screen.hasShiftDown()) {
        tooltip.add(Text.literal("Right-click to fling yourself!").formatted(Arythings.GRAY));
        tooltip.add(Text.literal("You will be launched in the direction you are looking.").formatted(Arythings.GRAY));
        tooltip.add(Text.literal("Cooldown: 5 minutes").formatted(Arythings.GRAY));
        tooltip.add(Text.literal("Item will work like Totem of Undying, but can be used 2 times.").formatted(Arythings.AQUA));
        if (stack.getHolder() != null) {
            tooltip.add(Text.literal("Lives until break: " +
            CounterHelperUtil.getCounterValue(stack.getHolder().getUuid(), "lives", stack.getHolder().getServer()) 
            + " remain.").formatted(Arythings.RED));
        } else {}
        }
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient()) {
            int counterValue = CounterHelperUtil.getCounterValue(user.getUuid(), "astryluna_upgrade", world.getServer());
            int seconds = 300 - counterValue / 20;
            int minutes = seconds / 60;
            seconds = seconds - (minutes * 60);

            if(counterValue > 6000) {
                // Set the counter to 0
                CounterHelperUtil.setCounter(user.getUuid(), "astryluna_upgrade", 0, CounterMode.TICK, world.getServer());

                // Apply initial upward velocity
                user.addVelocity(user.getVelocity().add(0, 0.75D, 0)); // Added 0.75 for initial upward fling

                // Schedule a task to apply velocity after 1 tick
                world.getServer().execute(() -> {
                    float yaw = user.getYaw();
                    float pitch = user.getPitch();
                    float f = 1.5F;

                    // Calculate the horizontal motion based on yaw and pitch (I don't understand it even though I should...)
                    double motionX = -MathHelper.sin(yaw * (float) Math.PI / 180.0F) * MathHelper.cos(pitch * (float) Math.PI / 180.0F) * f;
                    double motionZ = MathHelper.cos(yaw * (float) Math.PI / 180.0F) * MathHelper.cos(pitch * (float) Math.PI / 180.0F) * f;
                    double motionY = -MathHelper.sin(pitch * (float) Math.PI / 180.0F) * f;

                    // Apply the velocity
                    user.setVelocity(motionX, motionY, motionZ);
                 });
                return TypedActionResult.success(itemStack, world.isClient());

            } else {
                user.sendMessage(Text.literal("Wait " + minutes + "m, " + seconds + "s!").formatted(Arythings.RED), true);
                Arythings.LOGGER.debug("UUID " + user.getUuidAsString() + ": " + counterValue + " tick(s) has passed for counter 'astryluna_upgrade'.");
            }
        }

        return TypedActionResult.fail(itemStack);
    }

    public static boolean tryUseLives(DamageSource source, ServerPlayerEntity serverPlayer, World world) {
            if(CounterHelperUtil.getCounterValue(serverPlayer.getUuid(), "lives", world.getServer()) != 0) {
                if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                    return false;
                } else {
                    ItemStack itemStack = null;
                    Hand[] var4 = Hand.values();
                    int var5 = var4.length;

                    for(int var6 = 0; var6 < var5; ++var6) {
                        Hand hand = var4[var6];
                        ItemStack itemStack2 = serverPlayer.getStackInHand(hand);
                        if (itemStack2.isOf(ModItems.UPGRADED_MACE)) {
                            itemStack = itemStack2.copy();
                            break;
                        }
                    }

                    if (itemStack != null) {
                        serverPlayer.setHealth(2.0F);
                        serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 60, 4));
                        serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1));
                        serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0));
                        serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 1200, 1));
                        CounterHelperUtil.setCounter(serverPlayer.getUuid(), "lives", 
                            (CounterHelperUtil.getCounterValue(serverPlayer.getUuid(), "lives", world.getServer()) - 1), 
                            CounterMode.MANUAL, world.getServer());
                    }

                    return true;
                }
            } else {
                ItemStack stack = serverPlayer.getStackInHand(serverPlayer.getActiveHand());
                serverPlayer.sendMessage(Text.literal("Upgraded Mace has no lives left...").formatted(Arythings.RED));
                stack.decrement(1);
                serverPlayer.playSound(SoundEvents.BLOCK_GLASS_BREAK);
            }
        return false;
    }
}
