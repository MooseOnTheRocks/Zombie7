package dev.foltz.item.stage;

import dev.foltz.network.Z7PlayerState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class StagedItemView<T extends StagedItem> {
    public final String stageId;
    public final int stageTicks;
    public final int maxStageTicks;
    public final Z7PlayerState playerState;
    public final T item;
    public final ItemStack stack;
    public final Entity entity;
    public final World world;

    public StagedItemView(String stageId, int stageTicks, int maxStageTicks, Z7PlayerState playerState, T item, ItemStack stack, Entity entity, World world) {
        this.stageId = stageId;
        this.stageTicks = stageTicks;
        this.maxStageTicks = maxStageTicks;
        this.playerState = playerState;
        this.item = item;
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
