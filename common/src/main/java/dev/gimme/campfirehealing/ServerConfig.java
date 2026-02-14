package dev.gimme.campfirehealing;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

public abstract class ServerConfig {

    public abstract float getNaturalRegenSpeedMultiplier();
    public abstract float getNaturalRegenMaxHealToPercentage();

    protected abstract float getCampfireHealAmount();
    protected abstract float getCampfireExhaustion();
    public abstract float getCampfireSecondsBetweenHeals();
    protected abstract float getCampfireMaxHealToPercentage();
    public abstract float getCampfireSaturatedHealMultiplier();
    public abstract int getCampfireRequiredPlayers();
    public abstract float getCampfireRange();
    protected abstract int getCampfireMinYLevelOverworld();
    protected abstract int getCampfireMinYLevelNether();
    protected abstract int getCampfireMinYLevelOther();

    protected abstract float getSoulfireHealMultiplier();
    protected abstract float getSoulfireExhaustionMultiplier();
    protected abstract float getSoulfireMaxHealToPercentage();
    protected abstract int getSoulfireMinYLevelOverworld();
    protected abstract int getSoulfireMinYLevelNether();
    protected abstract int getSoulfireMinYLevelOther();
    protected abstract int getSoulfireMaxYLevelNether();

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
        var level = campfire.getLevel();
        if (level.dimensionType().skybox() == DimensionType.Skybox.OVERWORLD) {
            if (campfire.getBlockState().is(Blocks.SOUL_CAMPFIRE)) {
                return Main.INSTANCE.getServerConfig().getSoulfireMinYLevelOverworld();
            } else {
                return Main.INSTANCE.getServerConfig().getCampfireMinYLevelOverworld();
            }
        } else if (level.dimensionTypeRegistration().is(BuiltinDimensionTypes.NETHER)) {
            if (campfire.getBlockState().is(Blocks.SOUL_CAMPFIRE)) {
                return Main.INSTANCE.getServerConfig().getSoulfireMinYLevelNether();
            } else {
                return Main.INSTANCE.getServerConfig().getCampfireMinYLevelNether();
            }
        } else {
            if (campfire.getBlockState().is(Blocks.SOUL_CAMPFIRE)) {
                return Main.INSTANCE.getServerConfig().getSoulfireMinYLevelOther();
            } else {
                return Main.INSTANCE.getServerConfig().getCampfireMinYLevelOther();
            }
        }
    }

    public int getCampfireMaxYLevel(CampfireBlockEntity campfire) {
        var level = campfire.getLevel();
        if (level.dimensionTypeRegistration().is(BuiltinDimensionTypes.NETHER)) {
            if (campfire.getBlockState().is(Blocks.SOUL_CAMPFIRE)) {
                return Main.INSTANCE.getServerConfig().getSoulfireMaxYLevelNether();
            }
        }
        return Integer.MAX_VALUE;
    }
}
