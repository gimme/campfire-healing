package dev.gimme.campfirehealing.mixin.infested;

import com.llamalad7.mixinextras.sugar.Local;
import dev.gimme.campfirehealing.Main;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class LivingEntityDamageMixin {

    /**
     * Reduces the damage dealt by players if they have the "infested" effect.
     */
    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float modifyInfestedDamage(float amount, @Local(argsOnly = true) DamageSource source) {
        if (source.getEntity() instanceof ServerPlayer attacker && attacker.hasEffect(MobEffects.INFESTED)) {
            return amount * Main.INSTANCE.getServerConfig().getInfestedDamageMultiplier();
        }
        return amount;
    }
}
