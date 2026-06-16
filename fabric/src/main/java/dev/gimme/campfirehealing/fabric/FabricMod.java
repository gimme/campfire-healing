package dev.gimme.campfirehealing.fabric;

import dev.gimme.campfirehealing.domain.Constants;
import dev.gimme.campfirehealing.Main;
import dev.gimme.campfirehealing.domain.loot.ModLootConfig;
import dev.gimme.campfirehealing.infrastructure.FcapServerConfig;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.neoforged.fml.config.ModConfig;

public class FabricMod implements ModInitializer {

    @Override
    public void onInitialize() {
        ConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.COMMON, FcapServerConfig.SPEC, FcapServerConfig.FILE_NAME);

        Main.init();

        // Register server tick event
        ServerTickEvents.END_SERVER_TICK.register(tickServer -> Main.INSTANCE.getServerHandler().onServerTick(tickServer));

        // Modify loot tables
        LootTableEvents.MODIFY.register((resourceKey, builder, lootTableSource, provider) -> {
            if (Main.INSTANCE.getServerConfig().isExtraLootEnabled()) {
                ModLootConfig.EXTRA_LOOT_POOLS.stream()
                    .filter(extraPool -> extraPool.tablesToModify().contains(resourceKey))
                    .forEach(extraPool -> builder.withPool(extraPool.content()));
            }
        });
    }
}
