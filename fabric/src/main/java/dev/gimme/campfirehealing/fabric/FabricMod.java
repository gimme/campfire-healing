package dev.gimme.campfirehealing.fabric;

import dev.gimme.campfirehealing.Main;
import dev.gimme.campfirehealing.domain.loot.ModLootConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.loader.api.FabricLoader;

public class FabricMod implements ModInitializer {

    @Override
    public void onInitialize() {
        var main = Main.init(FabricLoader.getInstance().getConfigDir());

        // Register server tick event
        ServerTickEvents.END_SERVER_TICK.register(tickServer -> main.getServerHandler().onServerTick(tickServer));

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
