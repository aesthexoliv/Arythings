package net.arclieq.arythings.mixin;


import net.arclieq.arythings.Arythings;
import net.minecraft.command.argument.ItemStackArgument;

import net.minecraft.registry.Registries;
import net.minecraft.server.command.GiveCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(GiveCommand.class)
public abstract class GiveCommandMixin {

    @Inject(
        // Target the modern execute method signature for the /give command
        method = "execute",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void onGiveCommand(ServerCommandSource source, ItemStackArgument itemInput, Collection<ServerPlayerEntity> targets, int count, CallbackInfoReturnable<Integer> cir) {
        try {
            Identifier itemId = Registries.ITEM.getId(itemInput.getItem());
            // Check against the global banned items list
            if (Arythings.getBannedItems().contains(itemId.toString())) {
                source.sendError(Text.literal("This item is banned by server configuration and cannot be given: " + itemId));
                cir.setReturnValue(0); // Cancel the command
            }
        } finally {}
    }
}