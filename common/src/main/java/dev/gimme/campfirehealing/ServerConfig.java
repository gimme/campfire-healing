package dev.gimme.campfirehealing;

public abstract class ServerConfig {

    public static ServerConfig INSTANCE;

    public abstract float getNaturalRegenSpeedMultiplier();
    public abstract float getNaturalRegenMaxHealToPercentage();

    public abstract float getCampfireHealAmount();
    public abstract float getCampfireHealExhaustion();
    public abstract float getCampfireSecondsBetweenHeals();
    public abstract float getCampfireMaxHealToPercentage();
    public abstract int getCampfireRequiredPlayers();
    public abstract float getCampfireRange();
    public abstract int getCampfireMinYLevel();
}
