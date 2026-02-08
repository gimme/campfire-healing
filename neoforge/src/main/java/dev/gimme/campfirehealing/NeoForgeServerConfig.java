package dev.gimme.campfirehealing;

import net.neoforged.neoforge.common.ModConfigSpec;

public class NeoForgeServerConfig extends ServerConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.DoubleValue NATURAL_REGEN_SPEED_MULTIPLIER = BUILDER
            .comment("""
                    Multiplier for natural health regeneration speed. For example, setting this to 0.1 makes it take 40 seconds
                    per half heart instead of 4 seconds. Setting this to 0 disables natural regeneration entirely.
                    Note: with this feature enabled, saturation does not speed up natural regeneration.
                     Vanilla: -1""")
            .defineInRange("naturalRegenSpeedMultiplier", 0.0, -1.0, 1.0);

    private static final ModConfigSpec.DoubleValue NATURAL_REGEN_MAX_HEAL_TO_PERCENTAGE = BUILDER
            .comment("""
                    Maximum health percentage (0.0–1.0) up to which natural regeneration can heal players.
                    For example, setting this to 0.5 means players will only be healed by natural regeneration up to 5 hearts.
                    """)
            .defineInRange("naturalRegenMaxHealToPercentage", 1.0, 0.0, 1.0);

    private static final ModConfigSpec.DoubleValue CAMPFIRE_HEAL_AMOUNT = BUILDER
            .comment("""
                    Amount of health restored to each player when Campfire regeneration triggers.
                    For reference, natural regeneration in vanilla heals 1 (half heart) every 4 seconds (or every 0.5 seconds
                    when fully saturated).
                    Set to 0 to disable Campfire regeneration entirely.
                    """)
            .defineInRange("campfireHealAmount", 0.125, 0.0, 20.0);

    private static final ModConfigSpec.DoubleValue CAMPFIRE_EXHAUSTION = BUILDER
            .comment("""
                    Amount of exhaustion applied to players when they receive a heal from Campfire regeneration.
                    For reference, natural regeneration in vanilla applies 6.0 exhaustion per 1 (half heart) healed.
                    If this is above 0, players will only heal if they have foodLevel >= 18.
                    """)
            .defineInRange("campfireExhaustion", 0.75, 0.0, 40.0);

    private static final ModConfigSpec.DoubleValue CAMPFIRE_INTERVAL_SECONDS = BUILDER
            .comment("Seconds between each heal tick when Campfire regeneration is active.")
            .defineInRange("campfireHealIntervalSeconds", 0.5, 0.1, 10.0);

    private static final ModConfigSpec.DoubleValue CAMPFIRE_MAX_HEAL_TO_PERCENTAGE = BUILDER
            .comment("""
                    Maximum health percentage (0.0–1.0) up to which Campfires can heal players.
                    For example, setting this to 0.8 means players will only be healed by Campfires up to 8 hearts.
                    """)
            .defineInRange("campfireMaxHealToPercentage", 1.0, 0.0, 1.0);

    private static final ModConfigSpec.DoubleValue CAMPFIRE_SATURATED_HEAL_MULTIPLIER = BUILDER
            .comment("""
                    Multiplier for Campfire heal amount when the player is saturated.
                    For reference, natural regeneration in vanilla heals 8 times faster when the player is fully saturated.
                    Set to 1.0 to make Campfires heal at the same rate regardless of saturation.""")
            .defineInRange("campfireSaturatedHealMultiplier", 8.0, 1.0, 16.0);

    private static final ModConfigSpec.DoubleValue CAMPFIRE_RANGE_CONFIG = BUILDER
            .comment("Range (in blocks) around the Campfire within which players must be present to activate the effect.")
            .defineInRange("campfireRange", 3.0, 1.0, 50.0);

    private static final ModConfigSpec.IntValue CAMPFIRE_REQUIRED_PLAYERS = BUILDER
            .comment("Number of players required to be near the same Campfire to activate regeneration.")
            .defineInRange("campfireRequiredPlayers", 1, 1, 100);

    private static final ModConfigSpec.IntValue CAMPFIRE_MIN_Y_OVERWORLD = BUILDER
            .comment("The minimum Y level a Campfire must be placed at to provide regeneration in the Overworld.")
            .defineInRange("campfireMinYOverworld", 63, -64, 2033);

    private static final ModConfigSpec.IntValue CAMPFIRE_MIN_Y_NETHER = BUILDER
            .comment("The minimum Y level a Campfire must be placed at to provide regeneration in the Nether.")
            .defineInRange("campfireMinYNether", 1000, 0, 1000);

    private static final ModConfigSpec.IntValue CAMPFIRE_MIN_Y_OTHER = BUILDER
            .comment("The minimum Y level a Campfire must be placed at to provide regeneration in other dimensions (just the End in vanilla).")
            .defineInRange("campfireMinYOther", 1000, -1000, 1000);

    public static final ModConfigSpec SPEC = BUILDER.build();

    @Override
    public float getNaturalRegenSpeedMultiplier() {
        return NATURAL_REGEN_SPEED_MULTIPLIER.get().floatValue();
    }

    @Override
    public float getNaturalRegenMaxHealToPercentage() {
        return NATURAL_REGEN_MAX_HEAL_TO_PERCENTAGE.get().floatValue();
    }

    @Override
    public float getCampfireHealAmount() {
        return CAMPFIRE_HEAL_AMOUNT.get().floatValue();
    }

    @Override
    public float getCampfireExhaustion() {
        return CAMPFIRE_EXHAUSTION.get().floatValue();
    }

    @Override
    public float getCampfireSecondsBetweenHeals() {
        return CAMPFIRE_INTERVAL_SECONDS.get().floatValue();
    }

    @Override
    public float getCampfireMaxHealToPercentage() {
        return CAMPFIRE_MAX_HEAL_TO_PERCENTAGE.get().floatValue();
    }

    @Override
    public float getCampfireSaturatedHealMultiplier() {
        return CAMPFIRE_SATURATED_HEAL_MULTIPLIER.get().floatValue();
    }

    @Override
    public float getCampfireRange() {
        return CAMPFIRE_RANGE_CONFIG.get().floatValue();
    }

    @Override
    public int getCampfireRequiredPlayers() {
        return CAMPFIRE_REQUIRED_PLAYERS.get();
    }

    @Override
    public int getCampfireMinYLevelOverworld() {
        return CAMPFIRE_MIN_Y_OVERWORLD.get();
    }

    @Override
    public int getCampfireMinYLevelNether() {
        return CAMPFIRE_MIN_Y_NETHER.get();
    }

    @Override
    public int getCampfireMinYLevelOther() {
        return CAMPFIRE_MIN_Y_OTHER.get();
    }
}
