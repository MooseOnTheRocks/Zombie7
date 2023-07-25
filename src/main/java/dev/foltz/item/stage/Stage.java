package dev.foltz.item.stage;

import dev.foltz.Z7Util;
import net.minecraft.item.ItemStack;

public class Stage<T extends StagedItem> {
    public final int maxStageTicks;
    public final boolean tickWhileUnselected;

    protected Stage(int maxStageTicks, boolean tickWhileUnselected) {
        this.maxStageTicks = maxStageTicks;
        this.tickWhileUnselected = tickWhileUnselected;
    }

    public float barProgress(ItemStack stack) {
        return -1;
    }

    public int barColor(ItemStack stack) {
        return Z7Util.GREEN;
    }

    public String handleInit(StagedItemView<T> view) {
        return view.stageId;
    }

    public String handlePressShoot(StagedItemView<T> view) {
        return view.stageId;
    }

    public String handleReleaseShoot(StagedItemView<T> view) {
        return view.stageId;
    }

    public String handlePressReload(StagedItemView<T> view) {
        return view.stageId;
    }

    public String handleReleaseReload(StagedItemView<T> view) {
        return view.stageId;
    }

    public String handleTick(StagedItemView<T> view) {
        return view.stageId;
    }
    public String handleLastTick(StagedItemView<T> view) {
        return view.stageId;
    }

    public String handleUnselected(StagedItemView<T> view) {
        return view.stageId;
    }
}
