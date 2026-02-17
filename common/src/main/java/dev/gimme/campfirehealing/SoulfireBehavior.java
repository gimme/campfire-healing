package dev.gimme.campfirehealing;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;

import java.util.Set;

public class SoulfireBehavior {

    public static ServerConfig getServerConfig() {
        return Main.INSTANCE.getServerConfig();
    }

    /**
     * Plays effects when Soul fuel is placed in the campfire.
     */
    public static void onFuelPlaced(CampfireBlockEntity campfire, int slot) {
        if (getServerConfig().isSoulfireLitByFuel()) {
            setLitState(campfire, true);
        }
        playSoulEffect(campfire, slot);
    }

    /**
     * Plays a sound effect when the Soul fuel runs out.
     */
    public static void onFuelBurned(CampfireBlockEntity campfire, ServerLevel level) {
        long fuelItemsCount = campfire.getItems().stream().filter(stack -> isSoulFuelItem(stack, level)).count();
        boolean noMoreFuelLeft = fuelItemsCount == 1;
        if (noMoreFuelLeft) {
            if (getServerConfig().isSoulfireLitByFuel()) {
                setLitState(campfire, false);
            }
            playFuelBurnedOutEffect(campfire, level);
        }
    }

    private static void setLitState(CampfireBlockEntity campfire, boolean lit) {
        var level = campfire.getLevel();
        if (level == null) return;
        level.setBlockAndUpdate(campfire.getBlockPos(), campfire.getBlockState().setValue(CampfireBlock.LIT, lit));
    }

    /**
     * Plays a sound effect indicating the Soul fuel has run out.
     */
    private static void playFuelBurnedOutEffect(CampfireBlockEntity campfire, ServerLevel level) {
        var pos = campfire.getBlockPos();
        var random = level.getRandom();
        level.playSound(
            null,
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            SoundEvents.CANDLE_EXTINGUISH,
            SoundSource.BLOCKS,
            1f,
            0.6f + (random.nextFloat() - random.nextFloat()) * 0.4f
        );
    }

    /**
     * Plays an effect of a soul escaping from the given slot.
     */
    private static void playSoulEffect(CampfireBlockEntity campfire, int slot) {
        ServerLevel level = (ServerLevel) campfire.getLevel();
        if (level == null) return;

        var pos = campfire.getBlockPos();
        int l = campfire.getBlockState().getValue(CampfireBlock.FACING).get2DDataValue();
        var random = level.getRandom();

        Direction direction = Direction.from2DDataValue(Math.floorMod(slot + l, 4));
        double x = pos.getX() + 0.5 - (direction.getStepX() * 0.3125) + (direction.getClockWise().getStepX() * 0.3125);
        double y = pos.getY() + 0.75;
        double z = pos.getZ() + 0.5 - (direction.getStepZ() * 0.3125) + (direction.getClockWise().getStepZ() * 0.3125);

        level.sendParticles(
            ParticleTypes.SOUL,
            x, y, z,
            1,
            0, 0, 0,
            0.01
        );
        level.playSound(
            null, x, y, z,
            SoundEvents.SOUL_ESCAPE, SoundSource.BLOCKS,
            3.0f,
            0.6f + random.nextFloat() * 0.8f
        );
    }

    /**
     * Calculates the total burn time of the next added fuel, stacking it with existing fuel if present.
     */
    public static int getNextFuelBurnTime(ItemStack fuelItemStack, NonNullList<ItemStack> items, int[] cookingProgress, int[] cookingTime, ServerLevel level) {
        int maxFuelDurationLeft = 0;
        for (int i = 0; i < items.size(); i++) {
            if (!isSoulFuelItem(items.get(i), level)) continue;

            var fuelDurationLeft = cookingTime[i] - cookingProgress[i];
            if (fuelDurationLeft > maxFuelDurationLeft) {
                maxFuelDurationLeft = fuelDurationLeft;
            }
        }

        int fuelValue = getServerConfig().getSoulfireFuel().stream()
                .filter(fuel -> {
                    var itemId = level.registryAccess().lookupOrThrow(Registries.ITEM).getKey(fuelItemStack.getItem());
                    var regex = fuel.itemRegex();
                    return matchesRegex(itemId, regex);
                })
                .findFirst()
                .map(ServerConfig.SoulfireFuel::duration)
                .orElse(0);

        return maxFuelDurationLeft + fuelValue;
    }

    /**
     * Returns if the campfire currently has Soul fuel in it.
     */
    public static boolean isSoulFueled(CampfireBlockEntity campfire) {
        var level = campfire.getLevel();
        if (level == null) return false;
        return campfire.getItems().stream().anyMatch(itemStack -> isSoulFuelItem(itemStack, level));
    }

    /**
     * Returns if the given item stack is Soul fuel for a Soul Campfire.
     */
    public static boolean isSoulFuel(CampfireBlockEntity campfire, ItemStack itemStack, Level level) {
        return campfire.getBlockState().is(Blocks.SOUL_CAMPFIRE) && isSoulFuelItem(itemStack, level);
    }

    /**
     * Returns if the given item stack is valid Soul fuel.
     */
    public static boolean isSoulFuelItem(ItemStack itemStack, Level level) {
        if (itemStack.isEmpty()) return false;

        Set<ServerConfig.SoulfireFuel> fuelItems = getServerConfig().getSoulfireFuel();
        return fuelItems.stream().anyMatch(fuel -> {
            var itemId = level.registryAccess().lookupOrThrow(Registries.ITEM).getKey(itemStack.getItem());
            var regex = fuel.itemRegex();
            return matchesRegex(itemId, regex);
        });
    }

    /**
     * Returns if the given identifier matches the given regex, either as a full string or just the path (ignoring namespace).
     */
    private static boolean matchesRegex(Identifier identifier, String regex) {
        if (identifier == null) return false;
        return identifier.toString().matches(regex) || identifier.getPath().matches(regex);
    }

    public static void onPlayerIsRegenerating(ServerPlayer player, CampfireBlockEntity campfire, boolean isFirstTick) {
        boolean wasPlayerInfested = player.hasEffect(MobEffects.INFESTED);
        applySoulfireHealingEffects(player);
        boolean isPlayerInfested = player.hasEffect(MobEffects.INFESTED);

        if (!wasPlayerInfested && isPlayerInfested) {
            playInfestedSound(player);
        }
        if (isFirstTick) {
            playEerieSound(campfire);
        }
    }

    private static void applySoulfireHealingEffects(ServerPlayer player) {
        var mobEffectRegistry = player.registryAccess().lookupOrThrow(Registries.MOB_EFFECT);
        for (ServerConfig.Effect effect : getServerConfig().getSoulfireHealingEffects()) {
            var mobEffect = mobEffectRegistry.get(effect.effectId());
            if (mobEffect.isEmpty()) {
                Constants.LOG.warn("Effect with ID {} not found, skipping", effect.effectId());
                continue;
            }
            player.addEffect(new MobEffectInstance(mobEffect.get(), effect.duration(), effect.amplifier(), true, true));
        }
    }

    private static void playInfestedSound(ServerPlayer player) {
        player.level().playSound(
            null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.SILVERFISH_STEP,
            SoundSource.NEUTRAL,
            1.0f,
            1.0f
        );
    }

    private static void playEerieSound(CampfireBlockEntity campfire) {
        var level = campfire.getLevel();
        if (level == null) return;

        var pos = campfire.getBlockPos();
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.75;
        double z = pos.getZ() + 0.5;

        level.playSound(
            null, x, y, z,
            SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD,
            SoundSource.AMBIENT,
            0.5f,
            2f
        );
    }
}
