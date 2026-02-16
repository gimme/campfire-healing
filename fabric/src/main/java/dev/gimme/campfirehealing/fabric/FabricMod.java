package dev.gimme.campfirehealing.fabric;

import dev.gimme.campfirehealing.Main;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;

public class FabricMod implements ModInitializer {

    @Override
    public void onInitialize() {
        // Register server starting event
        ServerLifecycleEvents.SERVER_STARTED.register(mainServer -> {
            var main = Main.init(FabricLoader.getInstance().getConfigDir(), mainServer);

            // Register server tick event
            ServerTickEvents.END_SERVER_TICK.register(tickServer -> main.getServerHandler().onServerTick());
        });
    }
}
