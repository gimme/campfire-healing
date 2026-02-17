package dev.gimme.campfirehealing.mixin;

import com.google.common.collect.Sets;
import dev.gimme.campfirehealing.InfestedRemovalQueue;
import dev.gimme.campfirehealing.Main;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.stream.Collectors;

@Mixin(ClearAllStatusEffectsConsumeEffect.class)
public class MilkMixin {

    /**
     * Prevents milk from clearing the "infested" effects, or allows it to "extract" the Silverfish, depending on the config.
     */
    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void disableMilkClearingInfected(Level level, ItemStack itemStack, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!Main.INSTANCE.getServerConfig().doesInfestedEndWhenSilverfishComeOut()) return;
        if (!(entity instanceof ServerPlayer player)) return;
        var serverLevel = (ServerLevel) level;

        Set<Holder<MobEffect>> infestedEffects = InfestedRemovalQueue.getInfestedEffects(player)
            .map(MobEffectInstance::getEffect)
            .collect(Collectors.toSet());

        Set<Holder<MobEffect>> playerEffects = Sets.newHashSet(player.getActiveEffectsMap().keySet());
        boolean anyEffectRemoved = false;

        for (var effect : playerEffects) {
            if (infestedEffects.contains(effect)) {
                if (!Main.INSTANCE.getServerConfig().doesMilkCureInfested()) continue;

                if (effect.equals(MobEffects.INFESTED)) {
                    campfire_healing$extractSilverfish(serverLevel, player);
                }
            }

            player.removeEffect(effect);
            anyEffectRemoved = true;
        }

        cir.setReturnValue(anyEffectRemoved);
    }

    /**
     * Spawns 1-2 Silverfish coming out of the player.
     */
    @Unique
    private static void campfire_healing$extractSilverfish(ServerLevel serverLevel, ServerPlayer player) {
        int spawnCount = Mth.randomBetweenInclusive(serverLevel.getRandom(), 1, 2);
        for (int i = 0; i < spawnCount; i++) {
            campfire_healing$spawnSilverfish(serverLevel, player, player.getX(), player.getY() + player.getBbHeight() / 2.0, player.getZ());
        }
    }

    @Unique
    private static void campfire_healing$spawnSilverfish(ServerLevel level, LivingEntity entity, double x, double y, double z) {
        Silverfish silverfish = EntityType.SILVERFISH.create(level, EntitySpawnReason.TRIGGERED);
        if (silverfish != null) {
            RandomSource randomsource = entity.getRandom();
            float f1 = Mth.randomBetween(randomsource, (float) (-Math.PI / 2), (float) (Math.PI / 2));
            Vector3f vector3f = entity.getLookAngle().toVector3f().mul(0.3F).mul(1.0F, 1.5F, 1.0F).rotateY(f1);
            silverfish.snapTo(x, y, z, level.getRandom().nextFloat() * 360.0F, 0.0F);
            silverfish.setDeltaMovement(new Vec3(vector3f));
            level.addFreshEntity(silverfish);
            silverfish.playSound(SoundEvents.SILVERFISH_HURT);
        }
    }
}
