package dev.gimme.campfirehealing;

import dev.gimme.campfirehealing.application.ServerHandler;
import dev.gimme.campfirehealing.domain.Constants;
import dev.gimme.campfirehealing.domain.ServerConfig;
import dev.gimme.campfirehealing.infrastructure.FcapServerConfig;

public class Main {

    public static Main INSTANCE;

    public static Main init() {
        INSTANCE = new Main();
        return INSTANCE;
    }

    private final ServerConfig serverConfig;
    private final ServerHandler serverHandler;

    private Main() {
        this.serverConfig = new FcapServerConfig();
        this.serverHandler = new ServerHandler();
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public ServerHandler getServerHandler() {
        return serverHandler;
    }
}
