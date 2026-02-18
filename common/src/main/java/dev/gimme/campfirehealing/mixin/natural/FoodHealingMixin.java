package dev.gimme.campfirehealing.mixin.natural;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.gimme.campfirehealing.domain.natural.NaturalRegenHelper.getRegenTicks;
import static dev.gimme.campfirehealing.domain.natural.NaturalRegenHelper.isModFeatureDisabled;
import static dev.gimme.campfirehealing.domain.natural.NaturalRegenHelper.isRegenStopped;

/**
 * Modifies natural health regeneration speed.
 */
@Mixin(FoodData.class)
public class FoodHealingMixin {

    @Shadow
    private int tickTimer;

    /**
     * Slows down natural health regeneration from saturation.
     */
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;heal(F)V", ordinal = 0), cancellable = true)
    private void beforeBigHeal(ServerPlayer player, CallbackInfo ci) {
        if (isModFeatureDisabled()) return;

        float healRatio = Math.min(((FoodData) (Object) this).getSaturationLevel(), 6.0f) / 6.0f;

        if (isRegenStopped(player) || this.tickTimer < (int) (healRatio * getRegenTicks())) {
            ci.cancel();
        }
    }

    /**
     * Slows down natural health regeneration from having high food level.
     */
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;heal(F)V", ordinal = 1), cancellable = true)
    private void beforeSmallHeal(ServerPlayer player, CallbackInfo ci) {
        if (isModFeatureDisabled()) return;

        if (isRegenStopped(player) || this.tickTimer < (int) getRegenTicks()) {
            ci.cancel();
        }
    }
}
