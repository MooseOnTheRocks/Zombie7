package dev.foltz.item.stage;

import net.minecraft.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class GunStageBuilder {
    private int maxStageTicks;
    private GunStageEventHandler handleInit;
    private GunStageEventHandler handlePressShoot;
    private GunStageEventHandler handleReleaseShoot;
    private GunStageEventHandler handlePressReload;
    private GunStageEventHandler handleReleaseReload;
    private GunStageEventHandler handleTick;
    private GunStageEventHandler handleLastTick;
    private GunStageEventHandler handleUnselected;
    private boolean tickWhileUnselected = false;
    private Function<ItemStack, Float> barProgress;
    private Function<ItemStack, Integer> barColor;

    public GunStageBuilder() {
    }

    public GunStageBuilder(int maxTicks) {
        this.maxStageTicks = maxTicks;
    }

    public GunStageBuilder maxStageTicks(int maxTicks) {
        this.maxStageTicks = maxTicks;
        return this;
    }

    public GunStageBuilder barProgress(Function<ItemStack, Float> barProgress) {
        this.barProgress = barProgress;
        return this;
    }

    public GunStageBuilder barColor(Function<ItemStack, Integer> barColor) {
        this.barColor = barColor;
        return this;
    }

    public GunStageBuilder tickWhileUnselected() {
        this.tickWhileUnselected = true;
        return this;
    }

    public GunStageBuilder onInitDo(Consumer<GunStageView> handleInit) {
        this.handleInit = view -> { handleInit.accept(view); return view.stageId; };
        return this;
    }

    public GunStageBuilder onInit(GunStageEventHandler handleInit) {
        this.handleInit = handleInit;
        return this;
    }

    public GunStageBuilder onPressShootDo(Consumer<GunStageView> handlePressShoot) {
        this.handlePressShoot = view -> { handlePressShoot.accept(view); return view.stageId; };
        return this;
    }

    public GunStageBuilder onPressShoot(GunStageEventHandler handlePressShoot) {
        this.handlePressShoot = handlePressShoot;
        return this;
    }

    public GunStageBuilder onReleaseShootDo(Consumer<GunStageView> handleReleaseShoot) {
        this.handleReleaseShoot = view -> { handleReleaseShoot.accept(view); return view.stageId; };
        return this;
    }

    public GunStageBuilder onReleaseShoot(GunStageEventHandler handleReleaseShoot) {
        this.handleReleaseShoot = handleReleaseShoot;
        return this;
    }

    public GunStageBuilder onPressReloadDo(Consumer<GunStageView> handlePressReload) {
        this.handlePressReload = view -> { handlePressReload.accept(view); return view.stageId; };
        return this;
    }

    public GunStageBuilder onPressReload(GunStageEventHandler handlePressReload) {
        this.handlePressReload = handlePressReload;
        return this;
    }

    public GunStageBuilder onReleaseReloadDo(Consumer<GunStageView> handleReleaseReload) {
        this.handleReleaseReload = view -> { handleReleaseReload.accept(view); return view.stageId; };
        return this;
    }

    public GunStageBuilder onReleaseReload(GunStageEventHandler handleReleaseReload) {
        this.handleReleaseReload = handleReleaseReload;
        return this;
    }

    public GunStageBuilder onTickDo(Consumer<GunStageView> handleTick) {
        this.handleTick = view -> { handleTick.accept(view); return view.stageId; };
        return this;
    }

    public GunStageBuilder onTick(GunStageEventHandler handleTick) {
        this.handleTick = handleTick;
        return this;
    }

    public GunStageBuilder onLastTickDo(Consumer<GunStageView> handleLastTick) {
        this.handleLastTick = view -> { handleLastTick.accept(view); return view.stageId; };
        return this;
    }

    public GunStageBuilder onLastTick(GunStageEventHandler handleLastTick) {
        this.handleLastTick = handleLastTick;
        return this;
    }

    public GunStageBuilder onUnselectedDo(Consumer<GunStageView> handleUnselected) {
        this.handleUnselected = view -> { handleUnselected.accept(view); return view.stageId; };
        return this;
    }

    public GunStageBuilder onUnselected(GunStageEventHandler handleUnselected) {
        this.handleUnselected = handleUnselected;
        return this;
    }

    public GunStage build() {
        return new GunStage(maxStageTicks, tickWhileUnselected) {

            @Override
            public float barProgress(ItemStack stack) {
                return barProgress == null ? super.barProgress(stack) : barProgress.apply(stack);
            }

            @Override
            public int barColor(ItemStack stack) {
                return barColor == null ? super.barColor(stack) : barColor.apply(stack);
            }

            @Override
            public String handleInit(GunStageView view) {
                return handleInit == null ? super.handleInit(view) : handleInit.handleEvent(view);
            }

            @Override
            public String handlePressShoot(GunStageView view) {
                return handlePressShoot == null ? super.handlePressShoot(view) : handlePressShoot.handleEvent(view);
            }

            @Override
            public String handleReleaseShoot(GunStageView view) {
                return handleReleaseShoot == null ? super.handleReleaseShoot(view) : handleReleaseShoot.handleEvent(view);
            }

            @Override
            public String handlePressReload(GunStageView view) {
                return handlePressReload == null ? super.handlePressReload(view) : handlePressReload.handleEvent(view);
            }

            @Override
            public String handleReleaseReload(GunStageView view) {
                return handleReleaseReload == null ? super.handleReleaseReload(view) : handleReleaseReload.handleEvent(view);
            }

            @Override
            public String handleTick(GunStageView view) {
                return handleTick == null ? super.handleTick(view) : handleTick.handleEvent(view);
            }

            @Override
            public String handleLastTick(GunStageView view) {
                return handleLastTick == null ? super.handleLastTick(view) : handleLastTick.handleEvent(view);
            }

            @Override
            public String handleUnselected(GunStageView view) {
                return handleUnselected == null ? super.handleUnselected(view) : handleUnselected.handleEvent(view);
            }
        };
    }
}
