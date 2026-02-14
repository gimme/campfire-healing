package dev.gimme.campfirehealing.infrastructure;

import dev.gimme.campfirehealing.ServerConfig;
import dev.gimme.config.ModConfigSpec;
import dev.gimme.config.ModConfigSpec.ConfigValue;

public class NightServerConfig extends ServerConfig {

    public static final ModConfigSpec SPEC = new ModConfigSpec();

    private static final ConfigValue<Number> NATURAL_REGEN_SPEED_MULTIPLIER = SPEC.variable()
        .comment("""
            Multiplier for natural health regeneration speed. For example, setting this to 0.1 makes it take 40 seconds
            per half heart instead of 4 seconds. Setting this to 0 disables natural regeneration entirely.
            Note: with this feature enabled, saturation does not speed up natural regeneration.
             Vanilla: -1""")
        .define("naturalRegenSpeedMultiplier", 0.0);

    private static final ConfigValue<Number> NATURAL_REGEN_MAX_HEAL_TO_PERCENTAGE = SPEC.variable()
        .comment("""
            Maximum health percentage (0.0–1.0) up to which natural regeneration can heal players.
            For example, setting this to 0.5 means players will only be healed by natural regeneration up to 5 hearts.""")
        .define("naturalRegenMaxHealToPercentage", 1.0);

    private static final ConfigValue<Number> CAMPFIRE_HEAL_AMOUNT = SPEC.variable()
        .comment("""
            Amount of health restored to each player when Campfire regeneration triggers.
            For reference, natural regeneration in vanilla heals 1 (half heart) every 4 seconds (or every 0.5 seconds
            when fully saturated).
            Set to 0 to disable Campfire regeneration entirely.""")
        .define("campfireHealAmount", 0.025);

    private static final ConfigValue<Number> CAMPFIRE_EXHAUSTION = SPEC.variable()
        .comment("""
            Amount of exhaustion applied to players when they receive a heal from Campfire regeneration.
            For reference, natural regeneration in vanilla applies 6.0 exhaustion per 1 (half heart) healed.
            If this is above 0, players will only heal if they have foodLevel >= 18.""")
        .define("campfireExhaustion", 0.15);

    private static final ConfigValue<Number> CAMPFIRE_INTERVAL_SECONDS = SPEC.variable()
        .comment("Seconds between each heal tick when Campfire regeneration is active.")
        .define("campfireHealIntervalSeconds", 0.1);

    private static final ConfigValue<Number> CAMPFIRE_MAX_HEAL_TO_PERCENTAGE = SPEC.variable()
        .comment("""
            Maximum health percentage (0.0–1.0) up to which Campfires can heal players.
            For example, setting this to 0.8 means players will only be healed by Campfires up to 8 hearts.""")
        .define("campfireMaxHealToPercentage", 1.0);

    private static final ConfigValue<Number> CAMPFIRE_SATURATED_HEAL_MULTIPLIER = SPEC.variable()
        .comment("""
            Multiplier for Campfire heal amount when the player is saturated.
            For reference, natural regeneration in vanilla heals 8 times faster when the player is fully saturated.
            Set to 1.0 to make Campfires heal at the same rate regardless of saturation.""")
        .define("campfireSaturatedHealMultiplier", 8.0);

    private static final ConfigValue<Number> CAMPFIRE_RANGE_CONFIG = SPEC.variable()
        .comment("Range (in blocks) around the Campfire within which players must be present to activate the effect.")
        .define("campfireRange", 3.0);

    private static final ConfigValue<Number> CAMPFIRE_REQUIRED_PLAYERS = SPEC.variable()
        .comment("Number of players required to be near the same Campfire to activate regeneration.")
        .define("campfireRequiredPlayers", 1);

    private static final ConfigValue<Number> CAMPFIRE_MIN_Y_OVERWORLD = SPEC.variable()
        .comment("""
            The minimum Y-level a Campfire must be placed at to provide regeneration in the Overworld.
            For reference, sea level is at Y=63, and bottom bedrock is at Y=-64.""")
        .define("campfireMinYOverworld", 63);

    private static final ConfigValue<Number> CAMPFIRE_MIN_Y_NETHER = SPEC.variable()
        .comment("The minimum Y-level a Campfire must be placed at to provide regeneration in the Nether.")
        .define("campfireMinYNether", 1000);

    private static final ConfigValue<Number> CAMPFIRE_MIN_Y_OTHER = SPEC.variable()
        .comment("The minimum Y-level a Campfire must be placed at to provide regeneration in other dimensions (just affects the End in vanilla).")
        .define("campfireMinYOther", 1000);

    private static final ConfigValue<Double> SOULFIRE_HEAL_MULTIPLIER = SPEC.variable()
        .comment("""
            Multiplier for the amount of health restored by Soul Campfire compared to Campfire.
            For example, setting this to 0.5 makes Soul Campfire heal half as much per tick.""")
        .define("soulfireHealMultiplier", 1.0);

    private static final ConfigValue<Double> SOULFIRE_EXHAUSTION_MULTIPLIER = SPEC.variable()
        .comment("""
            Multiplier for the amount of exhaustion applied by Soul Campfire compared to Campfire.
            For example, setting this to 2.0 makes Soul Campfire apply double the exhaustion per tick.""")
        .define("soulfireExhaustionMultiplier", 4.0);

    private static final ConfigValue<Double> SOULFIRE_MAX_HEAL_TO_PERCENTAGE = SPEC.variable()
        .comment("""
            Maximum health percentage (0.0–1.0) up to which Soul Campfires can heal players.""")
        .define("soulfireMaxHealToPercentage", 1.0);

    private static final ConfigValue<Number> SOULFIRE_MIN_Y_OVERWORLD = SPEC.variable()
        .comment("The minimum Y-level a Soul Campfire must be placed at to provide regeneration in the Overworld.")
        .define("soulfireMinYOverworld", -1000);

    private static final ConfigValue<Number> SOULFIRE_MIN_Y_NETHER = SPEC.variable()
        .comment("The minimum Y-level a Soul Campfire must be placed at to provide regeneration in the Nether.")
        .define("soulfireMinYNether", -1000);

    private static final ConfigValue<Number> SOULFIRE_MIN_Y_OTHER = SPEC.variable()
        .comment("The minimum Y-level a Soul Campfire must be placed at to provide regeneration in other dimensions (just affects the End in vanilla).")
        .define("soulfireMinYOther", -1000);

    private static final ConfigValue<Number> SOULFIRE_MAX_Y_NETHER = SPEC.variable()
        .comment("""
            The maximum Y-level a Soul Campfire must be placed at to provide regeneration in the Nether.
            For example, setting this to 31 means that it has to be placed below the lava level.""")
        .define("soulfireMaxYNether", 1000);

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
        return CAMPFIRE_REQUIRED_PLAYERS.get().intValue();
    }

    @Override
    public int getCampfireMinYLevelOverworld() {
        return CAMPFIRE_MIN_Y_OVERWORLD.get().intValue();
    }

    @Override
    public int getCampfireMinYLevelNether() {
        return CAMPFIRE_MIN_Y_NETHER.get().intValue();
    }

    @Override
    public int getCampfireMinYLevelOther() {
        return CAMPFIRE_MIN_Y_OTHER.get().intValue();
    }

    @Override
    public float getSoulfireHealMultiplier() {
        return SOULFIRE_HEAL_MULTIPLIER.get().floatValue();
    }

    @Override
    public float getSoulfireExhaustionMultiplier() {
        return SOULFIRE_EXHAUSTION_MULTIPLIER.get().floatValue();
    }

    @Override
    public float getSoulfireMaxHealToPercentage() {
        return SOULFIRE_MAX_HEAL_TO_PERCENTAGE.get().floatValue();
    }

    @Override
    public int getSoulfireMinYLevelOverworld() {
        return SOULFIRE_MIN_Y_OVERWORLD.get().intValue();
    }

    @Override
    public int getSoulfireMinYLevelNether() {
        return SOULFIRE_MIN_Y_NETHER.get().intValue();
    }

    @Override
    public int getSoulfireMinYLevelOther() {
        return SOULFIRE_MIN_Y_OTHER.get().intValue();
    }

    @Override
    public int getSoulfireMaxYLevelNether() {
        return SOULFIRE_MAX_Y_NETHER.get().intValue();
    }
}
