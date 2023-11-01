package dev.foltz;

import dev.foltz.item.Z7Items;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.List;

public class Z7LootTableModifiers {
    private static final Identifier ZOMBIE_ID = new Identifier("minecraft", "entities/zombie");

    public static void modifyLootTables() {
//        LootTableEvents.MODIFY.
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (ZOMBIE_ID.equals(id)) {
                LootPool.Builder poolGuns = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(Z7Items.ITEM_PISTOL_GLOCK))
                    .with(ItemEntry.builder(Z7Items.ITEM_PISTOL_BASIC))
                    .with(ItemEntry.builder(Z7Items.ITEM_PISTOL_FLINTLOCK))
                    .with(ItemEntry.builder(Z7Items.ITEM_PISTOL_DEAGLE))
                    .with(ItemEntry.builder(Z7Items.ITEM_SHOTGUN_BASIC))
                    .with(ItemEntry.builder(Z7Items.ITEM_SHOTGUN_AA12))
                    .with(ItemEntry.builder(Z7Items.ITEM_SHOTGUN_DBLBRL))
                    .with(ItemEntry.builder(Z7Items.ITEM_RIFLE_AK))
                    .conditionally(KilledByPlayerLootCondition.builder())
                    .conditionally(RandomChanceWithLootingLootCondition.builder(0.15f, 0.01f))
                    .apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0, 0.10f)));
                tableBuilder.pool(poolGuns);

                LootPool.Builder poolPistolAmmo = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(Z7Items.ITEM_AMMO_PISTOL))
                    .conditionally(RandomChanceWithLootingLootCondition.builder(0.1f, 0.02f))
                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 4.0f)));
                tableBuilder.pool(poolPistolAmmo);

                LootPool.Builder poolShotgunAmmo = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(Z7Items.ITEM_AMMO_SHOTGUN))
                    .conditionally(RandomChanceWithLootingLootCondition.builder(0.1f, 0.02f))
                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f)));
                tableBuilder.pool(poolShotgunAmmo);

                LootPool.Builder poolShotgunRubberAmmo = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(Z7Items.ITEM_AMMO_RUBBER_SHOTGUN))
                    .conditionally(RandomChanceWithLootingLootCondition.builder(0.1f, 0.02f))
                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 6.0f)));
                tableBuilder.pool(poolShotgunRubberAmmo);

                LootPool.Builder poolMagnumAmmo = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(Z7Items.ITEM_AMMO_MAGNUM))
                    .conditionally(RandomChanceWithLootingLootCondition.builder(0.1f, 0.02f))
                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f)));
                tableBuilder.pool(poolMagnumAmmo);

                LootPool.Builder poolGunRepairKit = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(Z7Items.ITEM_REPAIR_KIT))
                    .conditionally(KilledByPlayerLootCondition.builder())
                    .conditionally(RandomChanceWithLootingLootCondition.builder(0.02f, 0.02f));
                tableBuilder.pool(poolGunRepairKit);
            }
        });
    }
}
