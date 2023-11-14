package dev.foltz.item.stage;

import net.minecraft.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class StageBuilder<T extends StagedItem<?>> {
    private int maxStageTicks;
    private StagedItemEventHandler<T> handleInit;
    private StagedItemEventHandler<T> handlePressShoot;
    private StagedItemEventHandler<T> handleReleaseShoot;
    private StagedItemEventHandler<T> handlePressReload;
    private StagedItemEventHandler<T> handleReleaseReload;
    private StagedItemEventHandler<T> handleTick;
    private StagedItemEventHandler<T> handleLastTick;
    private StagedItemEventHandler<T> handleUnselected;
    private boolean tickWhileUnselected = false;
    private Function<ItemStack, Float> barProgress;
    private Function<ItemStack, Integer> barColor;

    public StageBuilder() {
    }

    public StageBuilder(int maxTicks) {
        this.maxStageTicks = maxTicks;
    }

    public StageBuilder<T> maxStageTicks(int maxTicks) {
        this.maxStageTicks = maxTicks;
        return this;
    }

    public StageBuilder<T> barProgress(Function<ItemStack, Float> barProgress) {
        this.barProgress = barProgress;
        return this;
    }

    public StageBuilder<T> barColor(Function<ItemStack, Integer> barColor) {
        this.barColor = barColor;
        return this;
    }

    public StageBuilder<T> tickWhileUnselected() {
        this.tickWhileUnselected = true;
        return this;
    }

    public StageBuilder<T> onInitDo(Consumer<StagedItemView<? extends T>> handleInit) {
        this.handleInit = view -> { handleInit.accept(view); return view.stageId; };
        return this;
    }

    public StageBuilder<T> onInit(StagedItemEventHandler<T> handleInit) {
        this.handleInit = handleInit;
        return this;
    }

    public StageBuilder<T> onPressShootDo(Consumer<StagedItemView<? extends T>> handlePressShoot) {
        this.handlePressShoot = view -> { handlePressShoot.accept(view); return view.stageId; };
        return this;
    }

    public StageBuilder<T> onPressShoot(StagedItemEventHandler<T> handlePressShoot) {
        this.handlePressShoot = handlePressShoot;
        return this;
    }

    public StageBuilder<T> onReleaseShootDo(Consumer<StagedItemView<? extends T>> handleReleaseShoot) {
        this.handleReleaseShoot = view -> { handleReleaseShoot.accept(view); return view.stageId; };
        return this;
    }

    public StageBuilder<T> onReleaseShoot(StagedItemEventHandler<T> handleReleaseShoot) {
        this.handleReleaseShoot = handleReleaseShoot;
        return this;
    }

    public StageBuilder<T> onPressReloadDo(Consumer<StagedItemView<? extends T>> handlePressReload) {
        this.handlePressReload = view -> { handlePressReload.accept(view); return view.stageId; };
        return this;
    }

    public StageBuilder<T> onPressReload(StagedItemEventHandler<T> handlePressReload) {
        this.handlePressReload = handlePressReload;
        return this;
    }

    public StageBuilder<T> onReleaseReloadDo(Consumer<StagedItemView<? extends T>> handleReleaseReload) {
        this.handleReleaseReload = view -> { handleReleaseReload.accept(view); return view.stageId; };
        return this;
    }

    public StageBuilder<T> onReleaseReload(StagedItemEventHandler<T> handleReleaseReload) {
        this.handleReleaseReload = handleReleaseReload;
        return this;
    }

    public StageBuilder<T> onTickDo(Consumer<StagedItemView<? extends T>> handleTick) {
        this.handleTick = view -> { handleTick.accept(view); return view.stageId; };
        return this;
    }

    public StageBuilder<T> onTick(StagedItemEventHandler<T> handleTick) {
        this.handleTick = handleTick;
        return this;
    }

    public StageBuilder<T> onLastTickDo(Consumer<StagedItemView<? extends T>> handleLastTick) {
        this.handleLastTick = view -> { handleLastTick.accept(view); return view.stageId; };
        return this;
    }

    public StageBuilder<T> onLastTick(StagedItemEventHandler<T> handleLastTick) {
        this.handleLastTick = handleLastTick;
        return this;
    }

    public StageBuilder<T> onUnselectedDo(Consumer<StagedItemView<? extends T>> handleUnselected) {
        this.handleUnselected = view -> { handleUnselected.accept(view); return view.stageId; };
        return this;
    }

    public StageBuilder<T> onUnselected(StagedItemEventHandler<T> handleUnselected) {
        this.handleUnselected = handleUnselected;
        return this;
    }

    public Stage<T> build() {
        return new Stage<T>(maxStageTicks, tickWhileUnselected) {

            @Override
            public float barProgress(ItemStack stack) {
                return barProgress == null ? super.barProgress(stack) : barProgress.apply(stack);
            }

            @Override
            public int barColor(ItemStack stack) {
                return barColor == null ? super.barColor(stack) : barColor.apply(stack);
            }

            @Override
            public String handleInit(StagedItemView<? extends T> view) {
                return handleInit == null ? super.handleInit(view) : handleInit.handleEvent(view);
            }

            @Override
            public String handlePressShoot(StagedItemView<? extends T> view) {
                return handlePressShoot == null ? super.handlePressShoot(view) : handlePressShoot.handleEvent(view);
            }

            @Override
            public String handleReleaseShoot(StagedItemView<? extends T> view) {
                return handleReleaseShoot == null ? super.handleReleaseShoot(view) : handleReleaseShoot.handleEvent(view);
            }

            @Override
            public String handlePressReload(StagedItemView<? extends T> view) {
                return handlePressReload == null ? super.handlePressReload(view) : handlePressReload.handleEvent(view);
            }

            @Override
            public String handleReleaseReload(StagedItemView<? extends T> view) {
                return handleReleaseReload == null ? super.handleReleaseReload(view) : handleReleaseReload.handleEvent(view);
            }

            @Override
            public String handleTick(StagedItemView<? extends T> view) {
                return handleTick == null ? super.handleTick(view) : handleTick.handleEvent(view);
            }

            @Override
            public String handleLastTick(StagedItemView<? extends T> view) {
                return handleLastTick == null ? super.handleLastTick(view) : handleLastTick.handleEvent(view);
            }

            @Override
            public String handleUnselected(StagedItemView<? extends T> view) {
                return handleUnselected == null ? super.handleUnselected(view) : handleUnselected.handleEvent(view);
            }
        };
    }
}
