package dev.gimme.campfirehealing;

import net.minecraft.server.level.ServerPlayer;

/**
 * Helper class for natural regeneration calculations.
 */
public class NaturalRegenHelper {

    /**
     * Calculates the number of ticks required between natural regeneration heals. The base is 80 ticks (4 seconds).
     */
    public static float getRegenTicks() {
        float regenSpeedMultiplier = Main.INSTANCE.getServerConfig().getNaturalRegenSpeedMultiplier();
        return 80 / regenSpeedMultiplier;
    }

    /**
     * Checks if this mod's regen feature is completely disabled.
     */
    public static boolean isModFeatureDisabled() {
        return Main.INSTANCE.getServerConfig().getNaturalRegenSpeedMultiplier() < 0;
    }

    /**
     * Checks if regeneration should be completely stopped for the given player.
     */
    public static boolean isRegenStopped(ServerPlayer player) {
        float maxHealToPercentage = Main.INSTANCE.getServerConfig().getNaturalRegenMaxHealToPercentage();
        int displayedHealth = (int) Math.ceil(player.getHealth());
        int maxHealTo = Math.round(player.getMaxHealth() * maxHealToPercentage);
        return displayedHealth >= maxHealTo;
    }
}
