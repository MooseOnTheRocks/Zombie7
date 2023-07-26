package dev.foltz.item.grenade;

import dev.foltz.Z7Util;
import dev.foltz.entity.Z7Entities;
import dev.foltz.entity.Z7FragGrenadeEntity;
import dev.foltz.entity.Z7GrenadeEntity;
import dev.foltz.entity.Z7MolotovGrenadeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

import static dev.foltz.Z7Util.*;

public class MolotovGrenadeItem extends StagedGrenadeItem {
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
        Z7MolotovGrenadeEntity grenade = new Z7MolotovGrenadeEntity(Z7Entities.MOLOTOV_GRENADE_ENTITY, entity.world);
        if (getStageName(stack).equals(STAGE_LIT)) {
            grenade.setLit(true);
        }

        else {
            grenade.setLit(false);
        }
        return List.of(grenade);
    }
}
