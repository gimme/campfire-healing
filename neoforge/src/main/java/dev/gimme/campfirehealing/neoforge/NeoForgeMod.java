package dev.gimme.campfirehealing.neoforge;

import dev.gimme.campfirehealing.Constants;
import dev.gimme.campfirehealing.Main;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(Constants.MOD_ID)
public class NeoForgeMod {

    public NeoForgeMod() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    private void onServerStarting(ServerStartingEvent event) {
        Main.init(FMLPaths.CONFIGDIR.get());
    }
}
