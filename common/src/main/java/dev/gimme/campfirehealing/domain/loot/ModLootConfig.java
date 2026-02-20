package dev.gimme.campfirehealing.domain.loot;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetStewEffectFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Configuration of extra loot pools to be added to existing loot tables.
 * Note: NeoForge requires the data gen task for changes to take effect.
 */
public class ModLootConfig {

    private static final LootPool.Builder LOW_TIER_LOOT = LootPool.lootPool()
        .setRolls(ConstantValue.exactly(1))
        .add(EmptyLootItem.emptyItem().setWeight(10))
        .add(LootItem.lootTableItem(Items.APPLE).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
        .add(LootItem.lootTableItem(Items.RED_MUSHROOM).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
        .add(LootItem.lootTableItem(Items.BROWN_MUSHROOM).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
        .add(potionLootEntry(1, Potions.HEALING));

    private static final LootPool.Builder SUSPICIOUS_LOOT = LootPool.lootPool()
        .setRolls(ConstantValue.exactly(1))
        .add(EmptyLootItem.emptyItem().setWeight(10))
        .add(
            LootItem.lootTableItem(Items.SUSPICIOUS_STEW).setWeight(2)
                .apply(
                    SetStewEffectFunction.stewEffect()
                        .withEffect(MobEffects.BLINDNESS, UniformGenerator.between(5, 45))
                        .withEffect(MobEffects.NAUSEA, UniformGenerator.between(5, 90))
                        .withEffect(MobEffects.SATURATION, ConstantValue.exactly(0.35f))
                )
        )
        .add(
            LootItem.lootTableItem(Items.SUSPICIOUS_STEW).setWeight(1)
                .apply(SetStewEffectFunction.stewEffect().withEffect(MobEffects.POISON, BinomialDistributionGenerator.binomial(63, 0.1f))) // Average ~4 damage
        )
        .add(
            LootItem.lootTableItem(Items.SUSPICIOUS_STEW).setWeight(3)
                .apply(SetStewEffectFunction.stewEffect().withEffect(MobEffects.REGENERATION, UniformGenerator.between(2.5f, 17.49f))) // 1–6 health; average ~3.5
        );

    private static LootPool.Builder goodHealingLoot() {
        return LootPool.lootPool()
            .add(LootItem.lootTableItem(Items.APPLE).setWeight(4).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
            .add(LootItem.lootTableItem(Items.APPLE).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3))))
            .add(potionLootEntry(5, Potions.HEALING))
            .add(potionLootEntry(2, Potions.STRONG_HEALING))
            .add(splashPotionLootEntry(2, Potions.HEALING))
            .add(splashPotionLootEntry(1, Potions.STRONG_HEALING));
    }

    private static final LootPool.Builder MID_TIER_LOOT = goodHealingLoot()
        .setRolls(ConstantValue.exactly(1))
        .add(EmptyLootItem.emptyItem().setWeight(16));

    private static final LootPool.Builder HIGH_TIER_LOOT = goodHealingLoot()
        .setRolls(ConstantValue.exactly(2))
        .add(EmptyLootItem.emptyItem().setWeight(10))
        .add(LootItem.lootTableItem(Items.GOLDEN_APPLE).setWeight(5).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
        .add(potionLootEntry(2, Potions.HEALING, UniformGenerator.between(2, 3)));

    private static final LootPool.Builder NETHER_LOOT = LootPool.lootPool()
        .setRolls(ConstantValue.exactly(1))
        .add(EmptyLootItem.emptyItem().setWeight(6))
        // Soulfire fuel
        .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(1).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
        // Nether tables already have good healing options (incl. stew), so we add some risk here
        .add(
            LootItem.lootTableItem(Items.SUSPICIOUS_STEW).setWeight(1)
                .apply(SetStewEffectFunction.stewEffect().withEffect(MobEffects.WITHER, BinomialDistributionGenerator.binomial(80, 0.1f))) // Average ~4 damage
        );

    private static LootPoolEntryContainer.Builder<?> potionLootEntry(int weight, Holder<Potion> potion) {
        return potionLootEntry(weight, potion, ConstantValue.exactly(1));
    }

    private static LootPoolEntryContainer.Builder<?> potionLootEntry(int weight, Holder<Potion> potion, NumberProvider numberProvider) {
        return LootItem.lootTableItem(Items.POTION).setWeight(weight).apply(SetItemCountFunction.setCount(numberProvider))
            .apply(SetComponentsFunction.setComponent(
                DataComponents.POTION_CONTENTS,
                new PotionContents(potion)
            ));
    }

    private static LootPoolEntryContainer.Builder<?> splashPotionLootEntry(int weight, Holder<Potion> potion) {
        return LootItem.lootTableItem(Items.SPLASH_POTION).setWeight(weight).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
            .apply(SetComponentsFunction.setComponent(
                DataComponents.POTION_CONTENTS,
                new PotionContents(potion)
            ));
    }

    private static final Set<ResourceKey<LootTable>> LOW_TIER_TABLES = Set.of(
        BuiltInLootTables.RUINED_PORTAL,
        BuiltInLootTables.SHIPWRECK_SUPPLY,
        BuiltInLootTables.UNDERWATER_RUIN_SMALL,
        BuiltInLootTables.VILLAGE_DESERT_HOUSE,
        BuiltInLootTables.VILLAGE_PLAINS_HOUSE,
        BuiltInLootTables.VILLAGE_SAVANNA_HOUSE,
        BuiltInLootTables.VILLAGE_SNOWY_HOUSE,
        BuiltInLootTables.VILLAGE_TAIGA_HOUSE,
        BuiltInLootTables.VILLAGE_WEAPONSMITH
    );

    private static final Set<ResourceKey<LootTable>> MID_TIER_TABLES = Set.of(
        BuiltInLootTables.ABANDONED_MINESHAFT,
        BuiltInLootTables.ANCIENT_CITY,
        BuiltInLootTables.ANCIENT_CITY_ICE_BOX,
        BuiltInLootTables.BASTION_BRIDGE,
        BuiltInLootTables.BASTION_HOGLIN_STABLE,
        BuiltInLootTables.BASTION_OTHER,
        BuiltInLootTables.DESERT_PYRAMID,
        BuiltInLootTables.IGLOO_CHEST,
        BuiltInLootTables.JUNGLE_TEMPLE,
        BuiltInLootTables.NETHER_BRIDGE,
        BuiltInLootTables.PILLAGER_OUTPOST,
        BuiltInLootTables.SHIPWRECK_TREASURE,
        BuiltInLootTables.SIMPLE_DUNGEON,
        BuiltInLootTables.STRONGHOLD_CORRIDOR,
        BuiltInLootTables.STRONGHOLD_CROSSING,
        BuiltInLootTables.STRONGHOLD_LIBRARY,
        BuiltInLootTables.UNDERWATER_RUIN_BIG,
        BuiltInLootTables.VILLAGE_TEMPLE,
        BuiltInLootTables.WOODLAND_MANSION
    );

    private static final Set<ResourceKey<LootTable>> HIGH_TIER_TABLES = Set.of(
        BuiltInLootTables.BASTION_TREASURE,
        BuiltInLootTables.BURIED_TREASURE
    );

    private static final Set<ResourceKey<LootTable>> SUSPICIOUS_TABLES = Stream.concat(
        LOW_TIER_TABLES.stream(),
        MID_TIER_TABLES.stream()
    ).collect(Collectors.toSet());

    private static final Set<ResourceKey<LootTable>> NETHER_TABLES = Set.of(
        BuiltInLootTables.BASTION_BRIDGE,
        BuiltInLootTables.BASTION_HOGLIN_STABLE,
        BuiltInLootTables.BASTION_OTHER,
        BuiltInLootTables.NETHER_BRIDGE,
        BuiltInLootTables.RUINED_PORTAL
    );

    public static final Set<ExtraLootPool> EXTRA_LOOT_POOLS = Set.of(
        new ExtraLootPool("low_tier", LOW_TIER_LOOT, LootContextParamSets.CHEST, LOW_TIER_TABLES),
        new ExtraLootPool("mid_tier", MID_TIER_LOOT, LootContextParamSets.CHEST, MID_TIER_TABLES),
        new ExtraLootPool("suspicious_stew", SUSPICIOUS_LOOT, LootContextParamSets.CHEST, SUSPICIOUS_TABLES),
        new ExtraLootPool("high_tier", HIGH_TIER_LOOT, LootContextParamSets.CHEST, HIGH_TIER_TABLES),
        new ExtraLootPool("nether", NETHER_LOOT, LootContextParamSets.CHEST, NETHER_TABLES)
    );

    public record ExtraLootPool(
        String name,
        LootPool.Builder content,
        ContextKeySet context,
        Set<ResourceKey<LootTable>> tablesToModify
    ) {
        public LootTable.Builder toLootTable() {
            return LootTable.lootTable().withPool(content);
        }
    }
}
