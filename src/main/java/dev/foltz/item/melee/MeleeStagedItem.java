package dev.foltz.item.melee;

import dev.foltz.item.ammo.AmmoItem;
import dev.foltz.item.gun.GunStagedItem;
import dev.foltz.item.stage.StageBuilder;
import dev.foltz.item.stage.StagedItem;
import dev.foltz.item.stage.StagedItemGraphBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

import java.util.Map;

public class MeleeStagedItem<T extends StagedItem<?>> extends StagedItem<T> {
    public static final String AMMO_LIST = "AmmoList";

    public final AmmoItem.AmmoCategory ammoCategory;
    public final int maxAmmoCapacity;

    public MeleeStagedItem(int maxDurability, AmmoItem.AmmoCategory ammoCategory, int maxAmmoCapacity, Map<String, StageBuilder<? extends StagedItem<?>>> stagesMap) {
        super(new FabricItemSettings().maxDamage(maxDurability), new StagedItemGraphBuilder<>(stagesMap).build());
        this.ammoCategory = ammoCategory;
        this.maxAmmoCapacity = maxAmmoCapacity;
    }

}
