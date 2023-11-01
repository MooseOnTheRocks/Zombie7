package dev.foltz.item.grenade;

import dev.foltz.entity.*;
import dev.foltz.entity.grenade.Z7GrenadeEntity;
import dev.foltz.entity.grenade.MolotovGrenadeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;

import static dev.foltz.Z7Util.*;

public class MolotovGrenadeItem extends GrenadeStagedItem {
    public static final String STAGE_DEFAULT = "default";
    public static final String STAGE_LIT = "lit";

    public MolotovGrenadeItem() {
        super(16, Map.of(
            STAGE_DEFAULT, new GrenadeStageBuilder()
                .barColor(stack -> GREEN)
                .onPressShoot(view -> STAGE_LIT),

            STAGE_LIT, new GrenadeStageBuilder()
                .barColor(stack -> ORANGE)
                .barProgress(stack -> 1f)
                .onUnselected(view -> STAGE_DEFAULT)
                .onPressReload(view -> STAGE_DEFAULT)
        ));
    }

    @Override
    public List<? extends Z7GrenadeEntity> createGrenadeEntities(LivingEntity entity, ItemStack stack) {
        MolotovGrenadeEntity grenade = new MolotovGrenadeEntity(Z7Entities.MOLOTOV_GRENADE_ENTITY, entity.getWorld());
        if (getStageName(stack).equals(STAGE_LIT)) {
            grenade.setLit(true);
        }

        else {
            grenade.setLit(false);
        }
        return List.of(grenade);
    }
}
