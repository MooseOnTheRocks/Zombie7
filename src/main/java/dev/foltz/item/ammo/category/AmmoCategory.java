package dev.foltz.item.ammo.category;

import dev.foltz.item.ammo.type.AmmoType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;

import java.util.List;
import java.util.Optional;

public class AmmoCategory {
    private final List<AmmoType> ammoTypes;

    public AmmoCategory(List<AmmoType> ammoTypes) {
        this.ammoTypes = List.copyOf(ammoTypes);
    }

    public AmmoCategory(AmmoType... ammoTypes) {
        this.ammoTypes = List.of(ammoTypes);
    }

    public ItemStack getDefaultItemStack() {
        return ammoTypes.get(0).getDefaultItemStack();
    }

    public int findAmmoSlot(PlayerEntity player) {
        for (var ammoType : ammoTypes) {
            int slot = ammoType.findAmmoSlot(player);
            if (slot != -1) {
                return slot;
            }
        }
        return -1;
    }

    public Optional<AmmoType> getAmmoTypeOf(ItemStack stack) {
        return ammoTypes.stream().filter(type -> stack.isIn(type.getTypeKey())).findFirst();
    }
}
