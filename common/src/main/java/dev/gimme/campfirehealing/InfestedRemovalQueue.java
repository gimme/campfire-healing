package dev.gimme.campfirehealing;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class InfestedRemovalQueue {

    private static final Set<UUID> QUEUE = new HashSet<>();

    /**
     * Adds a player to the queue to have their "infested" effects removed later.
     */
    public static void queue(ServerPlayer player) {
        QUEUE.add(player.getUUID());
    }

    /**
     * Removes all "infested" effects from players in the queue, and then clears the queue.
     */
    public static void flush(MinecraftServer server) {
        QUEUE.stream()
            .map(id -> server.getPlayerList().getPlayer(id)).filter(Objects::nonNull)
            .forEach(player -> getInfestedEffects(player).forEach(effect -> player.removeEffect(effect.getEffect())));
        QUEUE.clear();
    }

    /**
     * Returns all "infested" effects currently on the player.
     */
    public static Stream<MobEffectInstance> getInfestedEffects(ServerPlayer player) {
        var infestedEffect = player.getEffect(MobEffects.INFESTED);
        if (infestedEffect == null) return Stream.empty();
        int infestedDurationLeft = infestedEffect.getDuration();

        return List.copyOf(player.getActiveEffects()).stream()
            .filter(effect -> effect.getDuration() == infestedDurationLeft);
    }
}
