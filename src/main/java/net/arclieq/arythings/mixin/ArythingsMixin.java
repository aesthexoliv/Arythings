package net.arclieq.arythings.mixin;

import net.arclieq.arythings.Arythings;
import net.arclieq.arythings.world.gen.ModWorldGeneration;
import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class ArythingsMixin {
	@Inject(at = @At("HEAD"), method = "loadWorld")
	private void init(CallbackInfo info) {
		Arythings.LOGGER.info("Loading " + Arythings.MOD_ID + "...");
		ModWorldGeneration.generateModWorldGen();
		Arythings.LOGGER.info("Finished!");
		// This code is injected into the start of MinecraftServer.loadWorld()V
	}
}
