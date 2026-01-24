package dev.gimme.campfirehealing;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Handles passive regeneration behavior around campfires.
 */
public class CampfirePassiveRegenBehavior {

    private static final Map<CampfireBlockEntity, TickTimer> campfireTickTimers = new WeakHashMap<>();

    /**
     * Ticks campfire regeneration logic.
     */
    public static void tickCampfireRegen(ServerLevel level, CampfireBlockEntity campfire) {
        if (ServerConfig.INSTANCE.getCampfireHealAmount() <= 0) return;
        if (campfire.getBlockPos().getY() < ServerConfig.INSTANCE.getCampfireMinYLevel()) return;

        var tickTimer = campfireTickTimers.computeIfAbsent(campfire, k -> new TickTimer());

        var healRange = new AABB(campfire.getBlockPos()).inflate(ServerConfig.INSTANCE.getCampfireRange());
        var playersInRange = level.getEntitiesOfClass(ServerPlayer.class, healRange);
        var playerRequirement = ServerConfig.INSTANCE.getCampfireRequiredPlayers();

        if (playersInRange.size() >= playerRequirement) {
            tickTimer.increment();

            playersInRange.forEach(CampfirePassiveRegenBehavior::indicateIfPlayerIsRegenerating);
        } else {
            tickTimer.decrement();
        }

        if (tickTimer.hasReachedSeconds(ServerConfig.INSTANCE.getCampfireSecondsBetweenHeals())) {
            tickTimer.reset();

            playersInRange.forEach(CampfirePassiveRegenBehavior::triggerHeal);
        }
    }

    /**
     * Triggers a campfire regeneration heal for the given player.
     */
    private static void triggerHeal(ServerPlayer player) {
        var healAmount = getHealAmountForPlayer(player);
        if (healAmount == 0) return;

        player.heal(healAmount);
        player.causeFoodExhaustion(ServerConfig.INSTANCE.getCampfireHealExhaustion());
    }

    /**
     * Returns the amount the given player will be healed by a campfire if triggered.
     */
    private static float getHealAmountForPlayer(ServerPlayer player) {
        if (player.getFoodData().getFoodLevel() < 18 && ServerConfig.INSTANCE.getCampfireHealExhaustion() > 0) return 0;

        var maxHealTo = player.getMaxHealth() * ServerConfig.INSTANCE.getCampfireMaxHealToPercentage();
        return Math.min(ServerConfig.INSTANCE.getCampfireHealAmount(), maxHealTo - player.getHealth());
    }

    /**
     * Indicate to the given player if they are currently regenerating from a campfire.
     */
    private static void indicateIfPlayerIsRegenerating(ServerPlayer player) {
        if (getHealAmountForPlayer(player) == 0) return;
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 5, 0, true, true, true));
    }

    /**
     * Simple timer counting in-game ticks (20 ticks = 1 second).
     */
    private static class TickTimer {
        private int value = 0;

        void increment() {
            value++;
        }
        void decrement() {
            if (value > 0) value--;
        }
        void reset() {
            value = 0;
        }
        boolean hasReachedSeconds(float seconds) {
            return value >= seconds * 20;
        }
    }
}
