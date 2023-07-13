package dev.foltz.item.stage;

import dev.foltz.Z7Util;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class GunStage {
    public final int maxStageTicks;
    public final boolean tickWhileUnselected;

    protected GunStage(int maxStageTicks, boolean tickWhileUnselected) {
        this.maxStageTicks = maxStageTicks;
        this.tickWhileUnselected = tickWhileUnselected;
    }

    public float barProgress(ItemStack stack) {
        return -1;
    }

    public int barColor(ItemStack stack) {
        return Z7Util.GREEN;
    }

    public String handleInit(GunStageView view) {
        return view.stageId;
    }

    public String handlePressShoot(GunStageView view) {
        return view.stageId;
    }

    public String handleReleaseShoot(GunStageView view) {
        return view.stageId;
    }

    public String handlePressReload(GunStageView view) {
        return view.stageId;
    }

    public String handleReleaseReload(GunStageView view) {
        return view.stageId;
    }

    public String handleTick(GunStageView view) {
        return view.stageId;
    }
    public String handleLastTick(GunStageView view) {
        return view.stageId;
    }

    public String handleUnselected(GunStageView view) {
        return view.stageId;
    }
}
