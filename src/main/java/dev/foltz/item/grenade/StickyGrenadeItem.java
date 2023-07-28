package dev.foltz.item.grenade;

import dev.foltz.Z7Util;
import dev.foltz.entity.Z7Entities;
import dev.foltz.entity.Z7FragGrenadeEntity;
import dev.foltz.entity.Z7GrenadeEntity;
import dev.foltz.entity.Z7StickyGrenadeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

import static dev.foltz.Z7Util.*;

public class StickyGrenadeItem extends StagedGrenadeItem {
    public static final String STAGE_DEFAULT = "default";
    public static final String STAGE_PRIMED = "primed";

    public StickyGrenadeItem() {
        super(16, Map.of(
            STAGE_DEFAULT, new GrenadeStageBuilder()
                .barColor(stack -> GREEN)
                .onPressShoot(view -> STAGE_PRIMED),

            STAGE_PRIMED, new GrenadeStageBuilder()
                .barColor(stack -> ORANGE)
                .onPressReload(doCancel(STAGE_DEFAULT))
                .onUnselected(view -> STAGE_DEFAULT)
        ));
    }

    @Override
    public List<? extends Z7GrenadeEntity> createGrenadeEntities(LivingEntity entity, ItemStack stack) {
        Z7StickyGrenadeEntity grenade = new Z7StickyGrenadeEntity(Z7Entities.STICKY_GRENADE_ENTITY, entity.world);
        grenade.setFuseTime(getStageName(stack).equals(STAGE_PRIMED) ? ticksFromSeconds(7f) : -1);
        return List.of(grenade);
    }
}
