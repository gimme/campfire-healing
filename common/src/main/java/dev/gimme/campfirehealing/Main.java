package dev.gimme.campfirehealing;

import dev.gimme.campfirehealing.infrastructure.NightServerConfig;

import java.nio.file.Path;

public class Main {

    public static Main INSTANCE;

    public static Main init(Path configDir) {
        INSTANCE = new Main(configDir);
        return INSTANCE;
    }

    private final ServerConfig serverConfig;

    private Main(Path configDir) {
        NightServerConfig.SPEC.init(configDir, Constants.MOD_ID + "-server.toml");
        this.serverConfig = new NightServerConfig();
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }
}
