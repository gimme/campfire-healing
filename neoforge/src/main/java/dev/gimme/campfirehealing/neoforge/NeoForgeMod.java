package dev.gimme.campfirehealing.neoforge;

import dev.gimme.campfirehealing.domain.Constants;
import dev.gimme.campfirehealing.Main;
import dev.gimme.campfirehealing.infrastructure.FcapServerConfig;
import dev.gimme.campfirehealing.neoforge.loot.LootProviders;
import dev.gimme.campfirehealing.neoforge.loot.ModLootConditionTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@Mod(Constants.MOD_ID)
public class NeoForgeMod {

    public NeoForgeMod(ModContainer container, IEventBus modBus) {
        container.registerConfig(ModConfig.Type.COMMON, FcapServerConfig.SPEC, FcapServerConfig.FILE_NAME);

        Main.init();
        NeoForge.EVENT_BUS.register(this);
        ModLootConditionTypes.REGISTRY.register(modBus);
        modBus.register(new LootProviders());
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        Main.INSTANCE.getServerHandler().onServerTick(event.getServer());
    }
}
