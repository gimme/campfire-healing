package dev.gimme.campfirehealing.domain.infested;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class InfestedBehavior {

    /**
     * Plays the sound of Silverfish moving in the player's head.
     */
    public static void playInfestedSound(ServerPlayer player) {
        var packet = new ClientboundSoundPacket(
            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.SILVERFISH_STEP),
            SoundSource.HOSTILE,
            player.getX(), player.getY(), player.getZ(),
            1.0f,
            1.0f,
            player.getRandom().nextLong()
        );
        player.connection.send(packet);
    }
}
