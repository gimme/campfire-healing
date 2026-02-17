package dev.gimme.campfirehealing.infrastructure;

import dev.gimme.campfirehealing.Constants;
import dev.gimme.campfirehealing.ServerConfig;
import dev.gimme.config.ModConfigSpec;
import dev.gimme.config.ModConfigSpec.ConfigValue;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;
import java.util.Objects;
import java.util.Set;

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
        .define("campfireHealAmount", 0.125);

    private static final ConfigValue<Number> CAMPFIRE_EXHAUSTION = SPEC.variable()
        .comment("""
            Amount of exhaustion applied to players when they receive a heal from Campfire regeneration.
            For reference, natural regeneration in vanilla applies 6.0 exhaustion per 1 (half heart) healed.
            If this is above 0, players will only heal if they have foodLevel >= 18.""")
        .define("campfireExhaustion", 0.75);

    private static final ConfigValue<Number> CAMPFIRE_INTERVAL_SECONDS = SPEC.variable()
        .comment("Seconds between each heal tick when Campfire regeneration is active.")
        .define("campfireHealIntervalSeconds", 0.5);

    private static final ConfigValue<Number> CAMPFIRE_SATURATION_HEAL_MULTIPLIER = SPEC.variable()
        .comment("""
            Multiplier for Campfire heal amount when the player is saturated.
            For reference, natural regeneration in vanilla heals 8 times faster when the player is fully saturated.
            Set to 1.0 to make Campfires heal at the same rate regardless of saturation.""")
        .define("campfireSaturationHealMultiplier", 8.0);

    private static final ConfigValue<Number> CAMPFIRE_RANGE_CONFIG = SPEC.variable()
        .comment("Range (in blocks) around the Campfire within which players must be present to activate the effect.")
        .define("campfireRange", 3.0);

    private static final ConfigValue<Number> CAMPFIRE_MAX_HEAL_TO_PERCENTAGE = SPEC.variable()
        .comment("""
            Maximum health percentage (0.0–1.0) up to which Campfires can heal players.
            For example, setting this to 0.8 means players will only be healed by Campfires up to 8 hearts.""")
        .define("campfireMaxHealToPercentage", 1.0);

    private static final ConfigValue<Number> CAMPFIRE_REQUIRED_FOOD_LEVEL = SPEC.variable()
        .comment("""
            Minimum food level players must have to be healed by Campfire regeneration.
            Natural regeneration in vanilla requires 18.""")
        .define("campfireRequiredFoodLevel", 18);

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

    private static final ConfigValue<Number> SOULFIRE_HEAL_MULTIPLIER = SPEC.variable()
        .comment("""
            Multiplier for the amount of health restored by Soul Campfire compared to Campfire.
            For example, setting this to 0.5 makes Soul Campfire heal half as much per tick.""")
        .define("soulfireHealMultiplier", 0.75);

    private static final ConfigValue<Number> SOULFIRE_EXHAUSTION_MULTIPLIER = SPEC.variable()
        .comment("""
            Multiplier for the amount of exhaustion applied by Soul Campfire compared to Campfire.
            For example, setting this to 2.0 makes Soul Campfire apply double the exhaustion per tick.""")
        .define("soulfireExhaustionMultiplier", 2.0);

    private static final ConfigValue<Number> SOULFIRE_MAX_HEAL_TO_PERCENTAGE = SPEC.variable()
        .comment("""
            Maximum health percentage (0.0–1.0) up to which Soul Campfires can heal players.""")
        .define("soulfireMaxHealToPercentage", 1.0);

    private static final ConfigValue<Number> SOULFIRE_REQUIRED_FOOD_LEVEL = SPEC.variable()
        .comment("""
            Minimum food level players must have to be healed by Soul Campfire regeneration.
            Setting this to 1 allows it to starve you out completely.
            Setting this to 0 allows it to keep healing you even when starving.""")
        .define("soulfireRequiredFoodLevel", 1);

    private static final ConfigValue<List<String>> SOULFIRE_FUEL = SPEC.variable()
        .comment("""
            List of items that can be used as fuel for Soul Campfires, along with how many seconds of fuel they provide.
            A duration of -1 means infinite.
            Format: "itemRegex,durationSeconds"
            Example: ["rotten_flesh,60", "bone,30"]""")
        .define("soulfireFuel", List.of("rotten_flesh,60", "bone,20", "ender_pearl,30", "blaze_rod,30", "ghast_tear,-1", "magma_cream,5"));

    private static final ConfigValue<Boolean> SOULFIRE_LIT_BY_FUEL = SPEC.variable()
        .comment("""
            If true, Soul Campfires will only be lit when you put fuel in them.
            This is for visual purposes, but a consequence is that it's harder to cook normal food on them.""")
        .define("soulfireLitByFuel", true);

    private static final ConfigValue<List<String>> SOULFIRE_HEALING_EFFECTS = SPEC.variable()
        .comment("""
            List of effects players get when they heal from a Soul Campfire.
            A duration of -1 means infinite.
            Format: "effectId,durationSeconds[0],amplifier[1]"
            Example: ["hunger", "darkness,1", "nausea,4", "weakness,1,255", "infested,-1", "weakness,-1", "slowness,-1", "mining_fatigue,-1"]""")
        .define("soulfireHealingEffects", List.of("hunger", "darkness,1", "weakness,1,10", "infested,-1", "weakness,-1"));

    private static final ConfigValue<Boolean> INFESTED_ENDS_WHEN_SILVERFISH_COME_OUT = SPEC.variable()
        .comment("""
            If true, when Silverfish spawn from an Infested player, the Infested effect and all other effects that have the same
            duration will be cleared from the player.
            This is a thematic feature that allows you to, for example, apply an infinite Infested effect (combined with
            any other negative/positive effects) that only ends when the Silverfish that "infest" you actually come out.""")
        .define("infestedEndsWhenSilverfishComeOut", true);

    private static final ConfigValue<Boolean> MILK_CURES_INFESTED = SPEC.variable()
        .comment("""
            If true, drinking milk will force the Silverfish to spawn out of an "infested" player, curing all associated
            effects in the process. Otherwise, milk will have no effect on the "infested" status.""")
        .define("milkCuresInfested", true);

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
    public float getCampfireSaturationHealMultiplier() {
        return CAMPFIRE_SATURATION_HEAL_MULTIPLIER.get().floatValue();
    }

    @Override
    public float getCampfireRange() {
        return CAMPFIRE_RANGE_CONFIG.get().floatValue();
    }

    @Override
    public float getCampfireMaxHealToPercentage() {
        return CAMPFIRE_MAX_HEAL_TO_PERCENTAGE.get().floatValue();
    }

    @Override
    public int getCampfireRequiredFoodLevel() {
        return CAMPFIRE_REQUIRED_FOOD_LEVEL.get().intValue();
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
    public int getSoulfireRequiredFoodLevel() {
        return SOULFIRE_REQUIRED_FOOD_LEVEL.get().intValue();
    }

    @Override
    public Set<SoulfireFuel> getSoulfireFuel() {
        return SOULFIRE_FUEL.get().stream()
            .map(fuelString -> {
                String[] parts = fuelString.split(",");

                String itemRegex = parts[0].trim();
                if (itemRegex.isEmpty()) {
                    Constants.LOG.warn("Invalid itemRegex for soulfireFuel: \"{}\"", fuelString);
                    return null;
                }

                double durationSeconds = 0;
                if (parts.length > 1) {
                    try {
                        durationSeconds = Double.parseDouble(parts[1].trim());
                    } catch (Exception ignored) {
                    }
                }
                if (durationSeconds == 0) {
                    Constants.LOG.warn("Invalid durationSeconds for soulfireFuel: \"{}\"", fuelString);
                    return null;
                }

                int duration = durationSeconds == -1
                    ? -1
                    : durationSeconds > 31536000 // More than 1 year of fuel is probably a mistake, treat it as infinite to avoid overflow issues when converting to ticks
                        ? -1
                        : (int) (durationSeconds * 20);
                return new SoulfireFuel(itemRegex, duration);
            })
            .filter(Objects::nonNull)
            .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public boolean isSoulfireLitByFuel() {
        return SOULFIRE_LIT_BY_FUEL.get();
    }

    @Override
    public Set<Effect> getSoulfireHealingEffects() {
        return SOULFIRE_HEALING_EFFECTS.get().stream()
            .map(effectString -> {
                String[] parts = effectString.split(",");

                Identifier effectId = Identifier.tryParse(parts[0].trim());
                if (effectId == null) {
                    Constants.LOG.warn("Invalid effectId for soulfireHealingEffects: \"{}\"", effectString);
                    return null;
                }

                double durationSeconds = 0;
                if (parts.length > 1) {
                    try {
                        durationSeconds = Double.parseDouble(parts[1].trim());
                    } catch (Exception e) {
                        Constants.LOG.warn("Invalid durationSeconds for soulfireHealingEffects: \"{}\"", effectString);
                    }
                }

                int amplifier = 0;
                if (parts.length > 2) {
                    try {
                        amplifier = Integer.parseInt(parts[2].trim()) - 1; // Convert from 1-based input to the 0-based code
                    } catch (NumberFormatException e) {
                        Constants.LOG.warn("Invalid amplifier for soulfireHealingEffects: \"{}\"", effectString);
                    }
                }

                // Pad the duration by a few ticks to avoid flickering issues for whole second durations.
                int duration = durationSeconds == -1 ? MobEffectInstance.INFINITE_DURATION : 5 + (int) (durationSeconds * 20);
                return new Effect(effectId, duration, amplifier);
            })
            .filter(Objects::nonNull)
            .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public boolean doesInfestedEndWhenSilverfishComeOut() {
        return INFESTED_ENDS_WHEN_SILVERFISH_COME_OUT.get();
    }

    @Override
    public boolean doesMilkCureInfested() {
        return MILK_CURES_INFESTED.get();
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
