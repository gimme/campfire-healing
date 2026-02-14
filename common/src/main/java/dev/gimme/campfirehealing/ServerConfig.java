package dev.gimme.campfirehealing;

public interface ServerConfig {

    float getNaturalRegenSpeedMultiplier();
    float getNaturalRegenMaxHealToPercentage();

    float getCampfireHealAmount();
    float getCampfireExhaustion();
    float getCampfireSecondsBetweenHeals();
    float getCampfireMaxHealToPercentage();
    float getCampfireSaturatedHealMultiplier();
    int getCampfireRequiredPlayers();
    float getCampfireRange();
    int getCampfireMinYLevelOverworld();
    int getCampfireMinYLevelNether();
    int getCampfireMinYLevelOther();
}
