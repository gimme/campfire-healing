package dev.gimme.campfirehealing.application;

import dev.gimme.campfirehealing.InfestedRemovalQueue;
import net.minecraft.server.MinecraftServer;

public class ServerHandler {

    public void onServerTick(MinecraftServer server) {
        InfestedRemovalQueue.flush(server);
    }
}
