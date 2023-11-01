package dev.foltz.item.grenade;

import dev.foltz.Z7Util;
import dev.foltz.entity.Z7Entities;
import dev.foltz.entity.grenade.FragGrenadeEntity;
import dev.foltz.entity.grenade.Z7GrenadeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

import static dev.foltz.Z7Util.*;

public class FragGrenadeItem extends GrenadeStagedItem {
    public static final String STAGE_DEFAULT = "default";
    public static final String STAGE_PRIMING = "priming";
    public static final String STAGE_PRIMED = "primed";

    public FragGrenadeItem() {
        super(16, Map.of(
            STAGE_DEFAULT, new GrenadeStageBuilder()
                .barColor(stack -> GREEN)
                .onPressShoot(view -> STAGE_PRIMING),

            STAGE_PRIMING, new GrenadeStageBuilder()
                .barColor(stack -> YELLOW)
                .onPressReload(doCancel(STAGE_DEFAULT))
                .onReleaseShoot(doReady(STAGE_PRIMED))
                .onUnselected(view -> STAGE_DEFAULT),

            STAGE_PRIMED, new GrenadeStageBuilder(ticksFromSeconds(7f))
                .tickWhileUnselected()
                .barColor(stack -> RED)
                .barProgress(stack -> 1 - ((FragGrenadeItem) stack.getItem()).getStageTicks(stack) / (float) ((FragGrenadeItem) stack.getItem()).getMaxStageTicks(stack))
                .onLastTick(view -> {
                    view.world.createExplosion(null, view.entity.getX(), view.entity.getY(), view.entity.getZ(), 2.0f, World.ExplosionSourceType.TNT);
                    view.stack.decrement(1);
                    return STAGE_DEFAULT;
                })
        ));
    }

    @Override
    public List<? extends Z7GrenadeEntity> createGrenadeEntities(LivingEntity entity, ItemStack stack) {
        FragGrenadeEntity grenade = new FragGrenadeEntity(Z7Entities.FRAG_GRENADE_ENTITY, entity.getWorld());
        if (getStageName(stack).equals(STAGE_PRIMED)) {
            int fuseTime = Math.max(1, getMaxStageTicks(stack) - getStageTicks(stack));
            grenade.setFuseTime(fuseTime);
        }
        else if (getStageName(stack).equals(STAGE_PRIMING)) {
            grenade.setFuseTime(Z7Util.ticksFromSeconds(7f));
        }
        else {
            grenade.setFuseTime(-1);
        }
        return List.of(grenade);
    }
}
