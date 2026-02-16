package dev.gimme.campfirehealing.application;

import dev.gimme.campfirehealing.InfestedRemovalQueue;
import net.minecraft.server.MinecraftServer;

public class ServerHandler {

    private final MinecraftServer server;

    public ServerHandler(MinecraftServer server) {
        this.server = server;
    }

    public void onServerTick() {
        InfestedRemovalQueue.flush(server);
    }
}
