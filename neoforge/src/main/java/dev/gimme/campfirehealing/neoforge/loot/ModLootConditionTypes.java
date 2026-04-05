package dev.gimme.campfirehealing.neoforge.loot;

import com.mojang.serialization.MapCodec;
import dev.gimme.campfirehealing.domain.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModLootConditionTypes {

    public static final DeferredRegister<MapCodec<? extends LootItemCondition>> REGISTRY = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, Constants.MOD_ID);

    public static final Supplier<MapCodec<ConfigCondition>> CONFIG_CONDITION_TYPE = REGISTRY.register("config", () -> ConfigCondition.CODEC);
}
