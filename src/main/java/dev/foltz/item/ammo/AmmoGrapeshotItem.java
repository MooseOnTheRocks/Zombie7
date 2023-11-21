package dev.foltz.item.ammo;

import dev.foltz.item.CompositeResourceItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

public class AmmoGrapeshotItem extends CompositeResourceItem {
    public AmmoGrapeshotItem() {
        super(new FabricItemSettings().maxCount(16));
    }
}
