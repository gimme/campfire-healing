package dev.gimme.campfirehealing;

import dev.gimme.campfirehealing.application.ServerHandler;
import dev.gimme.campfirehealing.infrastructure.NightServerConfig;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;

public class Main {

    public static Main INSTANCE;

    public static Main init(Path configDir, MinecraftServer server) {
        INSTANCE = new Main(configDir, server);
        return INSTANCE;
    }

    private final ServerConfig serverConfig;
    private final ServerHandler serverHandler;

    private Main(Path configDir, MinecraftServer server) {
        NightServerConfig.SPEC.init(configDir, Constants.MOD_ID + "-server.toml");
        this.serverConfig = new NightServerConfig();
        this.serverHandler = new ServerHandler(server);
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public ServerHandler getServerHandler() {
        return serverHandler;
    }
}
