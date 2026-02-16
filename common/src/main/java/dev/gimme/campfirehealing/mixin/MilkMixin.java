package dev.gimme.campfirehealing.mixin;

import com.google.common.collect.Sets;
import dev.gimme.campfirehealing.InfestedRemovalQueue;
import dev.gimme.campfirehealing.Main;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.stream.Collectors;

@Mixin(ClearAllStatusEffectsConsumeEffect.class)
public class MilkMixin {

    /**
     * Prevents milk from clearing the "infested" effects.
     */
    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void disableMilkClearingInfected(Level level, ItemStack itemStack, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!Main.INSTANCE.getServerConfig().doesInfestedEndWhenSilverfishComeOut()) return;
        if (!Main.INSTANCE.getServerConfig().isMilkPreventedFromClearingInfested()) return;
        if (!(entity instanceof ServerPlayer player)) return;

        Set<Holder<MobEffect>> infestedEffects = InfestedRemovalQueue.getInfestedEffects(player)
            .map(MobEffectInstance::getEffect)
            .collect(Collectors.toSet());

        boolean anyEffectRemoved = false;
        Set<Holder<MobEffect>> playerEffects = Sets.newHashSet(player.getActiveEffectsMap().keySet());
        for (var effect : playerEffects) {
            if (infestedEffects.contains(effect)) continue;
            player.removeEffect(effect);
            anyEffectRemoved = true;
        }
        cir.setReturnValue(anyEffectRemoved);
    }
}
