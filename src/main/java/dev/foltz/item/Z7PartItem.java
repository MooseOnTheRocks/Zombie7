package dev.foltz.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;

public class Z7PartItem extends Item {
    public Z7PartItem(Settings settings) {
        super(settings);
    }

    public Z7PartItem() {
        super(new FabricItemSettings());
    }
}
