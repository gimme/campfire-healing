package dev.gimme.campfirehealing.domain;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.Set;

public abstract class ServerConfig {

    public abstract float getNaturalRegenSpeedMultiplier();
    public abstract float getNaturalRegenMaxHealToPercentage();

    protected abstract float getCampfireHealAmount();
    protected abstract float getCampfireExhaustion();
    public abstract float getCampfireSecondsBetweenHeals();
    public abstract float getCampfireSaturationHealMultiplier();
    public abstract float getCampfireRange();
    protected abstract float getCampfireMaxHealToPercentage();
    public abstract int getCampfireRequiredFoodLevel();
    public abstract int getCampfireRequiredPlayers();
    protected abstract int getCampfireMinYLevelOverworld();
    protected abstract int getCampfireMinYLevelNether();
    protected abstract int getCampfireMinYLevelOther();

    protected abstract float getSoulfireHealMultiplier();
    protected abstract float getSoulfireExhaustionMultiplier();
    protected abstract float getSoulfireMaxHealToPercentage();
    public abstract int getSoulfireRequiredFoodLevel();
    public abstract Set<SoulfireFuel> getSoulfireFuel();
    public abstract boolean isSoulfireLitByFuel();
    public abstract Set<Effect> getSoulfireHealingEffects();
    protected abstract int getSoulfireMinYLevelOverworld();
    protected abstract int getSoulfireMinYLevelNether();
    protected abstract int getSoulfireMinYLevelOther();
    protected abstract int getSoulfireMaxYLevelNether();

    public abstract boolean doesInfestedEndWhenSilverfishComeOut();
    public abstract boolean doesMilkCureInfested();
    public abstract boolean doesInfestedHearMovingSound();
    public abstract float getInfestedDamageMultiplier();

    public abstract boolean isExtraLootEnabled();

    public abstract boolean isBlockBedRespawnBelowMinY();

    public abstract int getOxeyeDaisyMaxStackSize();

    public float getCampfireHealAmount(CampfireBlockEntity campfire) {
        var healAmount = getCampfireHealAmount();
        if (campfire.getBlockState().is(Blocks.SOUL_CAMPFIRE)) {
            healAmount *= getSoulfireHealMultiplier();
        }
        return Math.max(0, healAmount);
    }

    public float getCampfireExhaustion(CampfireBlockEntity campfire) {
        var exhaustion = getCampfireExhaustion();
        if (campfire.getBlockState().is(Blocks.SOUL_CAMPFIRE)) {
            exhaustion *= getSoulfireExhaustionMultiplier();
        }
        return Math.max(0, exhaustion);
    }

    public float getCampfireMaxHealToPercentage(CampfireBlockEntity campfire) {
        if (campfire.getBlockState().is(Blocks.SOUL_CAMPFIRE)) {
            return getSoulfireMaxHealToPercentage();
        } else {
            return getCampfireMaxHealToPercentage();
        }
    }

    public int getCampfireMinYLevel(CampfireBlockEntity campfire) {
        boolean isSoulfire = campfire.getBlockState().is(Blocks.SOUL_CAMPFIRE);
        return getMinYLevelForDimension(campfire.getLevel(), isSoulfire);
    }

    public boolean isCampfireBlockedAtYLevel(CampfireBlockEntity campfire) {
        int yLevel = campfire.getBlockPos().getY();
        return yLevel < getCampfireMinYLevel(campfire) || yLevel > getCampfireMaxYLevel(campfire);
    }

    public int getMinYLevelForDimension(Level level, boolean isSoulfire) {
        if (level.dimensionType().skybox() == DimensionType.Skybox.OVERWORLD) {
            return isSoulfire ? getSoulfireMinYLevelOverworld() : getCampfireMinYLevelOverworld();
        } else if (level.dimensionTypeRegistration().is(BuiltinDimensionTypes.NETHER)) {
            return isSoulfire ? getSoulfireMinYLevelNether() : getCampfireMinYLevelNether();
        } else {
            return isSoulfire ? getSoulfireMinYLevelOther() : getCampfireMinYLevelOther();
        }
    }

    public int getCampfireMaxYLevel(CampfireBlockEntity campfire) {
        var level = campfire.getLevel();
        if (level.dimensionTypeRegistration().is(BuiltinDimensionTypes.NETHER)) {
            if (campfire.getBlockState().is(Blocks.SOUL_CAMPFIRE)) {
                return getSoulfireMaxYLevelNether();
            }
        }
        return Integer.MAX_VALUE;
    }

    public record Effect(
        Identifier effectId,
        int duration,
        int amplifier
    ) {}

    public record SoulfireFuel(
        String itemRegex,
        int duration
    ) {}
}
