package dev.gimme.campfirehealing;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

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
        int minYLevel;
        if (level.dimensionType().skybox() == DimensionType.Skybox.OVERWORLD) {
            minYLevel = ServerConfig.INSTANCE.getCampfireMinYLevelOverworld();
        } else if (level.dimensionTypeRegistration().is(BuiltinDimensionTypes.NETHER)) {
            minYLevel = ServerConfig.INSTANCE.getCampfireMinYLevelNether();
        } else {
            minYLevel = ServerConfig.INSTANCE.getCampfireMinYLevelOther();
        }
        if (campfire.getBlockPos().getY() < minYLevel) return;


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
     * Triggers a campfire heal for the given player.
     */
    private static void triggerHeal(ServerPlayer player) {
        var healData = getAmountPlayerShouldHeal(player);
        if (healData == null) return;

        player.heal(healData.healAmount);
        player.causeFoodExhaustion(healData.exhaustionAmount);
    }

    /**
     * Returns how much the given player should heal (and exhaust) from a campfire right now, or null if they cannot heal.
     */
    @Nullable
    private static HealData getAmountPlayerShouldHeal(ServerPlayer player) {
        if (!player.isHurt()) return null;

        var foodData = player.getFoodData();
        float healAmount = ServerConfig.INSTANCE.getCampfireHealAmount();
        float exhaustionAmount = ServerConfig.INSTANCE.getCampfireExhaustion();

        if (foodData.getFoodLevel() < 18 && exhaustionAmount > 0) return null;
        if (foodData.getFoodLevel() >= 20 && foodData.getSaturationLevel() > 0) {
            healAmount *= ServerConfig.INSTANCE.getCampfireSaturatedHealMultiplier();
            exhaustionAmount *= ServerConfig.INSTANCE.getCampfireSaturatedHealMultiplier();
        }

        float maxHealTo = player.getMaxHealth() * ServerConfig.INSTANCE.getCampfireMaxHealToPercentage();
        var actualHealAmount = Math.min(healAmount, maxHealTo - player.getHealth());
        if (actualHealAmount == 0) return null;

        return new HealData(actualHealAmount, exhaustionAmount);
    }

    private record HealData(float healAmount, float exhaustionAmount) {}

    /**
     * Indicate to the given player if they are currently regenerating from a campfire.
     */
    private static void indicateIfPlayerIsRegenerating(ServerPlayer player) {
        if (getAmountPlayerShouldHeal(player) == null) return;
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
