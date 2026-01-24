package dev.gimme.campfirehealing;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(Constants.MOD_ID)
public class NeoForgeMod {

    public NeoForgeMod(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, NeoForgeServerConfig.SPEC, Constants.MOD_ID + "-server.toml");
        ServerConfig.INSTANCE = new NeoForgeServerConfig();
    }
}
