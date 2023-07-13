package dev.foltz.item.stage;

import dev.foltz.item.StagedGunItem;
import dev.foltz.network.Z7PlayerState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GunStageView {
    public final String stageId;
    public final int stageTicks;
    public final int maxStageTicks;
    public final Z7PlayerState playerState;
    public final StagedGunItem gun;
    public final ItemStack stack;
    public final Entity entity;
    public final World world;

    public GunStageView(String stageId, int stageTicks, int maxStageTicks, Z7PlayerState playerState, StagedGunItem gun, ItemStack stack, Entity entity, World world) {
        this.stageId = stageId;
        this.stageTicks = stageTicks;
        this.maxStageTicks = maxStageTicks;
        this.playerState = playerState;
        this.gun = gun;
        this.stack = stack;
        this.entity = entity;
        this.world = world;
    }

    public boolean isPressingShoot() {
        return playerState.isPressingShoot();
    }

    public boolean isPressingReload() {
        return playerState.isPressingReload();
    }
}
