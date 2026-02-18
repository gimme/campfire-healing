package dev.gimme.campfirehealing.mixin.infested;

import dev.gimme.campfirehealing.domain.infested.InfestedRemovalQueue;
import dev.gimme.campfirehealing.Main;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = {"net.minecraft.world.effect.InfestedMobEffect"})
public class InfestedMixin {

    /**
     * Queues the player to have their "infested" effects removed when silverfish spawn.
     */
    @Inject(method = "spawnSilverfish", at = @At("RETURN"))
    private void clearInfestedEffectWhenSilverfishSpawn(ServerLevel level, LivingEntity entity, double x, double y, double z, CallbackInfo ci) {
        if (!Main.INSTANCE.getServerConfig().doesInfestedEndWhenSilverfishComeOut()) return;
        if (!(entity instanceof ServerPlayer player)) return;

        InfestedRemovalQueue.queue(player);
    }
}
