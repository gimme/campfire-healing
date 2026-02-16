package dev.gimme.campfirehealing;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
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
        if (Main.INSTANCE.getServerConfig().getCampfireHealAmount(campfire) <= 0) return;
        var yLevel = campfire.getBlockPos().getY();
        if (yLevel < Main.INSTANCE.getServerConfig().getCampfireMinYLevel(campfire)) return;
        if (yLevel > Main.INSTANCE.getServerConfig().getCampfireMaxYLevel(campfire)) return;

        var tickTimer = campfireTickTimers.computeIfAbsent(campfire, k -> new TickTimer());

        var healRange = new AABB(campfire.getBlockPos()).inflate(Main.INSTANCE.getServerConfig().getCampfireRange());
        var playersInRange = level.getEntitiesOfClass(ServerPlayer.class, healRange);
        var playerRequirement = Main.INSTANCE.getServerConfig().getCampfireRequiredPlayers();

        if (playersInRange.size() >= playerRequirement) {
            tickTimer.increment();

            playersInRange.forEach(player -> applyRegenerationEffects(player, campfire));
        } else {
            tickTimer.decrement();
        }

        if (tickTimer.hasReachedSeconds(Main.INSTANCE.getServerConfig().getCampfireSecondsBetweenHeals())) {
            tickTimer.reset();

            playersInRange.forEach(player -> triggerHeal(player, campfire));
        }
    }

    /**
     * Triggers a campfire heal for the given player.
     */
    private static void triggerHeal(ServerPlayer player, CampfireBlockEntity campfire) {
        var healData = getAmountPlayerShouldHeal(player, campfire);
        if (healData == null) return;

        player.heal(healData.healAmount);
        player.causeFoodExhaustion(healData.exhaustionAmount);
    }

    /**
     * Returns how much the given player should heal (and exhaust) from a campfire right now, or null if they cannot heal.
     */
    @Nullable
    private static HealData getAmountPlayerShouldHeal(ServerPlayer player, CampfireBlockEntity campfire) {
        if (!player.isHurt()) return null;
        if (player.isCreative() || player.isSpectator()) return null;
        if (campfire.getBlockState().is(Blocks.SOUL_CAMPFIRE) && !SoulfireBehavior.isSoulFueled(campfire)) return null;

        var foodData = player.getFoodData();
        float healAmount = Main.INSTANCE.getServerConfig().getCampfireHealAmount(campfire);
        float exhaustionAmount = Main.INSTANCE.getServerConfig().getCampfireExhaustion(campfire);

        var foodLevelRequirement = campfire.getBlockState().is(Blocks.SOUL_CAMPFIRE)
            ? Main.INSTANCE.getServerConfig().getSoulfireRequiredFoodLevel()
            : Main.INSTANCE.getServerConfig().getCampfireRequiredFoodLevel();
        int foodLevelRequirement2 = Math.min(foodLevelRequirement + 2, 20);

        if (foodData.getFoodLevel() < foodLevelRequirement && exhaustionAmount > 0) return null;
        if (foodData.getFoodLevel() >= foodLevelRequirement2 && foodData.getSaturationLevel() > 0) {
            float saturationMultiplier = Main.INSTANCE.getServerConfig().getCampfireSaturationHealMultiplier();
            healAmount *= saturationMultiplier;
            exhaustionAmount *= saturationMultiplier;
        }

        float maxHealTo = player.getMaxHealth() * Main.INSTANCE.getServerConfig().getCampfireMaxHealToPercentage(campfire);
        var actualHealAmount = Math.min(healAmount, maxHealTo - player.getHealth());
        if (actualHealAmount <= 0) return null;

        return new HealData(actualHealAmount, exhaustionAmount);
    }

    private record HealData(float healAmount, float exhaustionAmount) {}

    /**
     * Applies effects to the given player that should apply while they are regenerating from a campfire.
     */
    private static void applyRegenerationEffects(ServerPlayer player, CampfireBlockEntity campfire) {
        if (getAmountPlayerShouldHeal(player, campfire) == null) return;
        boolean isFirstTick = !player.hasEffect(MobEffects.REGENERATION);
        indicatePlayerIsRegenerating(player);

        if (campfire.getBlockState().is(Blocks.SOUL_CAMPFIRE)) {
            SoulfireBehavior.onPlayerIsRegenerating(player, campfire, isFirstTick);
        }
    }

    /**
     * Shows a cosmetic Regeneration effect on the given player to indicate they are regenerating from a campfire.
     */
    private static void indicatePlayerIsRegenerating(ServerPlayer player) {
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
