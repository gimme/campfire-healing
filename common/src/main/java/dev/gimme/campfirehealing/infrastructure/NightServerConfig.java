package dev.gimme.campfirehealing.infrastructure;

import dev.gimme.campfirehealing.domain.Constants;
import dev.gimme.campfirehealing.domain.ServerConfig;
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
            Note: When using this feature, saturation does not speed up natural regeneration like it does in vanilla.
             Vanilla: -1""")
        .define("naturalRegen.speedMultiplier", 0.0);

    private static final ConfigValue<Number> NATURAL_REGEN_MAX_HEAL_TO_PERCENTAGE = SPEC.variable()
        .comment("""
            Maximum health percentage (0.0–1.0) up to which natural regeneration can heal players.
            For example, setting this to 0.5 means players will only be healed by natural regeneration up to 5 hearts.""")
        .define("naturalRegen.maxHealToPercentage", 1.0);

    private static final ConfigValue<Number> CAMPFIRE_HEAL_AMOUNT = SPEC.variable()
        .comment("""
            Amount of health restored to each player when Campfire regeneration triggers.
            For reference, natural regeneration in vanilla heals 1 (half heart) every 4 seconds (or every 0.5 seconds
            when fully saturated).
            Set to 0 to disable Campfire regeneration entirely.""")
        .define("campfire.healAmount", 0.125);

    private static final ConfigValue<Number> CAMPFIRE_EXHAUSTION = SPEC.variable()
        .comment("""
            Amount of flat exhaustion applied to players when they receive a heal from Campfire regeneration.
            For reference, natural regeneration in vanilla applies 6.0 exhaustion per 1 (half heart) healed.
            If this is above 0, players will only heal if they have foodLevel >= 18.""")
        .define("campfire.exhaustion", 0.75);

    private static final ConfigValue<Number> CAMPFIRE_INTERVAL_SECONDS = SPEC.variable()
        .comment("Seconds between each heal tick when Campfire regeneration is active.")
        .define("campfire.healIntervalSeconds", 0.5);

    private static final ConfigValue<Number> CAMPFIRE_SATURATION_HEAL_MULTIPLIER = SPEC.variable()
        .comment("""
            Multiplier for Campfire heal amount when the player is saturated.
            For reference, natural regeneration in vanilla heals 8 times faster when the player is fully saturated.
            Set to 1.0 to make Campfires heal at the same rate regardless of saturation.""")
        .define("campfire.saturationHealMultiplier", 8.0);

    private static final ConfigValue<Number> CAMPFIRE_RANGE_CONFIG = SPEC.variable()
        .comment("Range (in blocks) around the Campfire within which players must be present to activate the effect.")
        .define("campfire.range", 3.0);

    private static final ConfigValue<Number> CAMPFIRE_MAX_HEAL_TO_PERCENTAGE = SPEC.variable()
        .comment("""
            Maximum health percentage (0.0–1.0) up to which Campfires can heal players.
            For example, setting this to 0.8 means players will only be healed by Campfires up to 8 hearts.""")
        .define("campfire.maxHealToPercentage", 1.0);

    private static final ConfigValue<Number> CAMPFIRE_REQUIRED_FOOD_LEVEL = SPEC.variable()
        .comment("""
            Minimum food level players must have to be healed by Campfire regeneration.
            Natural regeneration in vanilla requires 18.""")
        .define("campfire.requiredFoodLevel", 18);

    private static final ConfigValue<Number> CAMPFIRE_REQUIRED_PLAYERS = SPEC.variable()
        .comment("Number of players required to be near the same Campfire to activate regeneration.")
        .define("campfire.requiredPlayers", 1);

    private static final ConfigValue<Boolean> CAMPFIRE_BLOCK_BED_RESPAWN = SPEC.variable()
        .comment("""
            If true, beds placed at Y-levels where Campfire regeneration is blocked will not work as respawn points.
            This prevents players from abusing intentional deaths to respawn at full health in areas where
            Campfire healing is restricted by the Y-level settings.""")
        .define("campfire.blockBedRespawnBelowMinY", true);

    private static final ConfigValue<Number> CAMPFIRE_MIN_Y_OVERWORLD = SPEC.variable()
        .comment("""
            The minimum Y-level a Campfire must be placed at to provide regeneration in the Overworld.
            For reference, sea level is at Y=63, and bottom bedrock is at Y=-64.""")
        .define("campfire.minYOverworld", 63);

    private static final ConfigValue<Number> CAMPFIRE_MIN_Y_NETHER = SPEC.variable()
        .comment("The minimum Y-level a Campfire must be placed at to provide regeneration in the Nether.")
        .define("campfire.minYNether", 1000);

    private static final ConfigValue<Number> CAMPFIRE_MIN_Y_OTHER = SPEC.variable()
        .comment("The minimum Y-level a Campfire must be placed at to provide regeneration in other dimensions (just affects the End in vanilla).")
        .define("campfire.minYOther", 1000);

    private static final ConfigValue<Number> SOULFIRE_HEAL_MULTIPLIER = SPEC.variable()
        .comment("""
            Multiplier for the amount of health restored by Soul Campfire compared to Campfire.
            For example, setting this to 0.5 makes Soul Campfire heal half as much per tick.""")
        .define("soulfire.healMultiplier", 0.75);

    private static final ConfigValue<Number> SOULFIRE_EXHAUSTION_MULTIPLIER = SPEC.variable()
        .comment("""
            Multiplier for the amount of exhaustion applied by Soul Campfire compared to Campfire.
            For example, setting this to 2.0 makes Soul Campfire apply double the exhaustion per tick.""")
        .define("soulfire.exhaustionMultiplier", 1.5);

    private static final ConfigValue<Number> SOULFIRE_MAX_HEAL_TO_PERCENTAGE = SPEC.variable()
        .comment("""
            Maximum health percentage (0.0–1.0) up to which Soul Campfires can heal players.""")
        .define("soulfire.maxHealToPercentage", 1.0);

    private static final ConfigValue<Number> SOULFIRE_REQUIRED_FOOD_LEVEL = SPEC.variable()
        .comment("""
            Minimum food level players must have to be healed by Soul Campfire regeneration.
            Setting this to 1 allows it to starve you out completely.
            Setting this to 0 allows it to keep healing you even when starving.""")
        .define("soulfire.requiredFoodLevel", 1);

    private static final ConfigValue<List<String>> SOULFIRE_FUEL = SPEC.variable()
        .comment("""
            List of items that can be used as fuel for Soul Campfires, along with how many seconds of fuel they provide.
            The item can be specified as a regex matching the item with or without namespace (e.g. "minecraft:rotten_flesh").
            A duration of -1 means infinite.
            Format: "item,seconds"
            Example: ["rotten_flesh,45", "bone,15", "ender_pearl,15", "blaze_rod,15", "ghast_tear,300", "magma_cream,5"]""")
        .define("soulfire.fuel", List.of("rotten_flesh,45"));

    private static final ConfigValue<Boolean> SOULFIRE_LIT_BY_FUEL = SPEC.variable()
        .comment("""
            If true, Soul Campfires will only be lit when you put fuel in them.
            This is for visual purposes, but a consequence is that you then also need fuel to cook normal food on them.""")
        .define("soulfire.litByFuel", true);

    private static final ConfigValue<List<String>> SOULFIRE_HEALING_EFFECTS = SPEC.variable()
        .comment("""
            List of effects players get when they heal from a Soul Campfire.
            A duration of -1 means infinite.
            Format: "effect,seconds[0],level[1]"
            Example: ["hunger", "darkness,1", "nausea,4", "weakness,1,255", "infested,-1", "weakness,-1", "slowness,-1", "mining_fatigue,-1"]""")
        .define("soulfire.healingEffects", List.of("hunger", "nausea,1", "darkness,1", "weakness,1,255", "infested,-1"));

    private static final ConfigValue<Number> SOULFIRE_MIN_Y_OVERWORLD = SPEC.variable()
        .comment("The minimum Y-level a Soul Campfire must be placed at to provide regeneration in the Overworld.")
        .define("soulfire.minYOverworld", -1000);

    private static final ConfigValue<Number> SOULFIRE_MIN_Y_NETHER = SPEC.variable()
        .comment("The minimum Y-level a Soul Campfire must be placed at to provide regeneration in the Nether.")
        .define("soulfire.minYNether", -1000);

    private static final ConfigValue<Number> SOULFIRE_MIN_Y_OTHER = SPEC.variable()
        .comment("The minimum Y-level a Soul Campfire must be placed at to provide regeneration in other dimensions (just affects the End in vanilla).")
        .define("soulfire.minYOther", -1000);

    private static final ConfigValue<Number> SOULFIRE_MAX_Y_NETHER = SPEC.variable()
        .comment("""
            The maximum Y-level a Soul Campfire must be placed at to provide regeneration in the Nether.
            For example, setting this to 31 means that it has to be placed below the lava level.""")
        .define("soulfire.maxYNether", 1000);

    private static final ConfigValue<Boolean> INFESTED_ENDS_WHEN_SILVERFISH_COME_OUT = SPEC.variable()
        .comment("""
            If true, when Silverfish spawn from an Infested player, the Infested effect and all other effects that have the same
            duration will be cleared from the player.
            This is a thematic feature that allows you to, for example, apply an infinite Infested effect (combined with
            any other negative/positive effects) that only ends when the Silverfish that "infest" you actually come out.""")
        .define("infested.endsWhenTriggered", true);

    private static final ConfigValue<Boolean> MILK_CURES_INFESTED = SPEC.variable()
        .comment("""
            If true, drinking milk will force the Silverfish to spawn out of an "infested" player, curing all associated
            effects in the process. Otherwise, milk will have no effect on the "infested" status.""")
        .define("infested.curedByMilk", true);

    private static final ConfigValue<Boolean> INFESTED_HEARS_MOVING_SOUND = SPEC.variable()
        .comment("""
            If true, infested players will hear the sound of Silverfish moving in their head whenever they take damage.""")
        .define("infested.hearsMovingSound", true);

    private static final ConfigValue<Number> INFESTED_DAMAGE_MULTIPLIER = SPEC.variable()
        .comment("""
            Multiplier for damage dealt when Infested.
            For example, 0.67 means the player deals 67% of their normal damage (a 33% reduction).""")
        .define("infested.damageMultiplier", 0.67);

    private static final ConfigValue<Number> OXEYE_DAISY_MAX_STACK_SIZE = SPEC.variable()
        .comment("""
            Maximum stack size for Oxeye Daisies. This limits how many Oxeye Daisies players can carry at once,
            making it harder to mass-produce Suspicious Stew (Regeneration) for healing.
            Set to -1 to use the vanilla stack size (64).""")
        .define("items.oxeyeDaisyMaxStackSize", 1);

    private static final ConfigValue<Boolean> EXTRA_LOOT_ENABLED = SPEC.variable()
        .comment("""
            When true, the mod injects extra loot pools with healing-related items (e.g. Potions, Apples and Suspicious Stews)
            into structure chest loot tables. Setting this to false disables these custom additions.""")
        .define("loot.extraLootEnabled", true);

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
                    Constants.LOG.warn("Invalid itemRegex for soulfire.fuel: \"{}\"", fuelString);
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
                    Constants.LOG.warn("Invalid durationSeconds for soulfire.fuel: \"{}\"", fuelString);
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
                    Constants.LOG.warn("Invalid effectId for soulfire.healingEffects: \"{}\"", effectString);
                    return null;
                }

                double durationSeconds = 0;
                if (parts.length > 1) {
                    try {
                        durationSeconds = Double.parseDouble(parts[1].trim());
                    } catch (Exception e) {
                        Constants.LOG.warn("Invalid durationSeconds for soulfire.healingEffects: \"{}\"", effectString);
                    }
                }

                int amplifier = 0;
                if (parts.length > 2) {
                    try {
                        amplifier = Integer.parseInt(parts[2].trim()) - 1; // Config is 1-based for user-friendliness, but MobEffectInstance expects 0-based
                    } catch (NumberFormatException e) {
                        Constants.LOG.warn("Invalid level for soulfire.healingEffects: \"{}\"", effectString);
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

    @Override
    public boolean doesInfestedEndWhenSilverfishComeOut() {
        return INFESTED_ENDS_WHEN_SILVERFISH_COME_OUT.get();
    }

    @Override
    public boolean doesMilkCureInfested() {
        return MILK_CURES_INFESTED.get();
    }

    @Override
    public boolean doesInfestedHearMovingSound() {
        return INFESTED_HEARS_MOVING_SOUND.get();
    }

    @Override
    public float getInfestedDamageMultiplier() {
        return INFESTED_DAMAGE_MULTIPLIER.get().floatValue();
    }

    @Override
    public int getOxeyeDaisyMaxStackSize() {
        return OXEYE_DAISY_MAX_STACK_SIZE.get().intValue();
    }

    @Override
    public boolean isExtraLootEnabled() {
        return EXTRA_LOOT_ENABLED.get();
    }

    @Override
    public boolean isBlockBedRespawnBelowMinY() {
        return CAMPFIRE_BLOCK_BED_RESPAWN.get();
    }
}
