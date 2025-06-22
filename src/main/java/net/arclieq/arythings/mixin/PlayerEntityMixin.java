package net.arclieq.arythings.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.arclieq.arythings.item.custom.AstrylunaShieldItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

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
