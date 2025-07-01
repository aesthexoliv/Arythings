package net.arclieq.arythings.mixin;

import net.arclieq.arythings.Arythings;
import net.arclieq.arythings.item.custom.AstrylunaShieldItem;
import net.arclieq.arythings.item.custom.UpgradedMace;
import net.arclieq.arythings.util.ConfigManager;
import net.arclieq.arythings.world.gen.ModWorldGeneration;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.GiveCommand;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.minecraft.item.Item;
import net.minecraft.item.MaceItem;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.registry.Registries;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.util.Identifier;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.tooltip.TooltipType;

import java.util.List;
import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// MinecraftServer mixin.
@Mixin(MinecraftServer.class)
public class ArythingsMixin {
	@Inject(at = @At("HEAD"), method = "loadWorld")
	private void init(CallbackInfo info) {
		Arythings.LOGGER.info("Loading " + Arythings.MOD_ID + "...");
		ModWorldGeneration.generateModWorldGen();
        ConfigManager.loadAndSyncConfig();
		Arythings.LOGGER.info("Finished!");
	}

    // PlayerEntity mixin.
	@Mixin(PlayerEntity.class)
    public static abstract class PlayerEntityMixin extends LivingEntity {
        protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
            super(entityType, world);
        }

        @Inject(method = "takeShieldHit", at = @At("HEAD"), cancellable = true)
        private void onTakeShieldHit(LivingEntity attacker, CallbackInfo ci) {
            ItemStack activeItem = this.getActiveItem();
            if(activeItem.getItem() instanceof AstrylunaShieldItem) {
                ci.cancel();
                if(attacker instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) attacker;
                    player.sendMessage(Text.literal("You are protected by the Astryluna Shield!").formatted(Formatting.GREEN), true);
                }
            }
        }
    }

    // GiveCommand mixin. Banned items will not be given, if affects_operators is true.
    // And if affects_creative is true, but affects_operators is false, 
    // it will still be removed from the inventory.
	@Mixin(GiveCommand.class)
    public static abstract class GiveCommandMixin {
        @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
        private static void onGiveCommand(ServerCommandSource source, ItemStackArgument itemInput, Collection<ServerPlayerEntity> targets, int count, CallbackInfoReturnable<Integer> cir) {
            Identifier itemId = Registries.ITEM.getId(itemInput.getItem());
            String stringId = itemId.toString();

            if(ConfigManager.getBannedItems().contains(stringId)) {
                boolean aC = ConfigManager.getDefaultAffectsCreative();
                boolean aO = ConfigManager.getDefaultAffectsOperators();

                for (ServerPlayerEntity target : targets) {
                    boolean ban = true;
                    if(target.isCreative() && !aC) {
                        ban = false;
                    }
                    if(target.hasPermissionLevel(2) && !aO) {
                        ban = false;
                    }

                    if(ban) {
                        source.sendError(Text.literal("Item '" + itemId.toString() + "' is banned for player '"
                            + target.getName().getString() + "' and cannot be given!"));
                        cir.setReturnValue(0);
                        cir.cancel();
                        return;
                    }
                }
            }
        }
    }

    // Item mixin. Used for appending a tooltip to MaceItem (NOT for UpgradedMace, even though it extends MaceItem)
	@Mixin(Item.class)
    public static class MaceMixin {
    @Inject(method = "appendTooltip", at = @At("TAIL"))
    private void arythings$appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo info) {
        if((Object)this instanceof MaceItem != (Object)this instanceof UpgradedMace) {
            tooltip.add(Text.literal("Can be upgraded...").formatted(Formatting.BLUE));
        }
    }
}
}
