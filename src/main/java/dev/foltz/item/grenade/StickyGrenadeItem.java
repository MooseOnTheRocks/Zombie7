package dev.foltz.item.grenade;

import dev.foltz.entity.Z7Entities;
import dev.foltz.entity.grenade.Z7GrenadeEntity;
import dev.foltz.entity.grenade.StickyGrenadeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;

import static dev.foltz.Z7Util.*;

public class StickyGrenadeItem extends GrenadeStagedItem {
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
        StickyGrenadeEntity grenade = new StickyGrenadeEntity(Z7Entities.STICKY_GRENADE_ENTITY, entity.world);
        grenade.setFuseTime(getStageName(stack).equals(STAGE_PRIMED) ? ticksFromSeconds(7f) : -1);
        return List.of(grenade);
    }
}
