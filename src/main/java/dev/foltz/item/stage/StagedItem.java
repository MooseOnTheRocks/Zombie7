package dev.foltz.item.stage;

import dev.foltz.item.CompositeResourceItem;
import dev.foltz.network.Z7ServerState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public abstract class StagedItem<T extends StagedItem<?>> extends CompositeResourceItem {
    public static final String STAGE_ID = "StageId";
    public static final String STAGE_TICKS = "StageTicks";
    public final StagedItemGraph<T> stagesGraph;

    public StagedItem(Settings settings, StagedItemGraph<? extends StagedItem<?>> graph) {
        super(settings);
        this.stagesGraph = (StagedItemGraph<T>) graph;
    }

    @Override
    public ItemStack getDefaultStack() {
        var stack = super.getDefaultStack();
        var id = stagesGraph.idFromName("default");
//        System.out.println("getDefaultStack 'default' = " + id);
        setStageId(stack, id);
        resetStageTicks(stack);
        return stack;
    }

    public void updateStageGraph(String newStage, StagedItemView<T> view) {
        if (!newStage.equals(view.stageId)) {
//            System.out.println(view.stageId + " -> " + newStage);
            setStageId(view.stack, stagesGraph.idFromName(newStage));
            setStageTicks(view.stack, 0);
            handleInit(new StagedItemView<>(
                newStage, view.item.getStageTicks(view.stack), view.item.getMaxStageTicks(view.stack),
                view.playerState,
                view.item, view.stack, view.entity, view.world
            ));
        }
    }

    public void handleInit(StagedItemView<T> view) {
//        System.out.println("init: " + view.stageId + " :: " + view.stageTicks + " / " + view.maxStageTicks);
        String newStage = stagesGraph.stages.get(view.stageId).handleInit(view);
        updateStageGraph(newStage, view);
    }

    public void handlePressShoot(StagedItemView<T> view) {
//        System.out.println("pressShoot: " + view.stageId + " :: " + view.stageTicks + " / " + view.maxStageTicks);
        String newStage = stagesGraph.stages.get(view.stageId).handlePressShoot(view);
        updateStageGraph(newStage, view);
    }

    public void handleReleaseShoot(StagedItemView<T> view) {
//        System.out.println("releaseShoot: " + view.stageId + " :: " + view.stageTicks + " / " + view.maxStageTicks);
        String newStage = stagesGraph.stages.get(view.stageId).handleReleaseShoot(view);
        updateStageGraph(newStage, view);
    }

    public void handlePressReload(StagedItemView<T> view) {
//        System.out.println("pressReload: " + view.stageId + " :: " + view.stageTicks + " / " + view.maxStageTicks);
        String newStage = stagesGraph.stages.get(view.stageId).handlePressReload(view);
        updateStageGraph(newStage, view);
    }

    public void handleReleaseReload(StagedItemView<T> view) {
//        System.out.println("releaseReload: " + view.stageId + " :: " + view.stageTicks + " / " + view.maxStageTicks);
        String newStage = stagesGraph.stages.get(view.stageId).handleReleaseReload(view);
        updateStageGraph(newStage, view);
    }

    public void handleTickInventory(StagedItemView<T> view) {
//        System.out.println("handleTickInventory: " + view.stageId + " :: " + view.stageTicks + " / " + view.maxStageTicks);
        handleTick(view);
    }

    public void handleTick(StagedItemView<T> view) {
        if (view.maxStageTicks > 0) {
//            System.out.println("handleTick: " + view.stageId + " :: " + view.stageTicks + " / " + view.maxStageTicks);
        }

        String newStage = stagesGraph.stages.get(view.stageId).handleTick(view);
        var stageTicks = getStageTicks(view.stack);
        if (!newStage.equals(view.stageId)) {
            updateStageGraph(newStage, view);
        }
        else if (view.maxStageTicks > 0) {
            if (stageTicks >= view.maxStageTicks) {
                handleLastTick(view);
            }
            else {
                // Note: Put this part in Stage itself as opposed to here?
                setStageTicks(view.stack, stageTicks + 1);
            }
        }
    }

    public void handleLastTick(StagedItemView<T> view) {
//        System.out.println("handleLastTick: " + view.stageId + " :: " + view.stageTicks + " / " + view.maxStageTicks);
        String newStage = stagesGraph.stages.get(view.stageId).handleLastTick(view);
        updateStageGraph(newStage, view);
    }

    public void handleUnselected(StagedItemView<T> view) {
//        System.out.println("unselected: " + view.stageId + " :: " + view.stageTicks + " / " + view.maxStageTicks);
        String newStage = stagesGraph.stages.get(view.stageId).handleUnselected(view);
        updateStageGraph(newStage, view);
    }

    public int getMaxStageTicks(ItemStack stack) {
        var stage = getStage(stack);
        return stage == null ? 0 : stage.maxStageTicks;
    }

    public Stage<T> getStage(ItemStack stack) {
        return stagesGraph.stageFromId(getStageId(stack));
    }

    public String getStageName(ItemStack stack) {
        return stagesGraph.nameFromId(getStageId(stack));
    }

    public int getStageId(ItemStack stack) {
        var nbt = stack.getNbt();
        return nbt != null && nbt.contains(STAGE_ID) ? nbt.getInt(STAGE_ID) : -1;
    }

    public ItemStack setStageId(ItemStack stack, int stage) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt(STAGE_ID, stage);
        stack.setNbt(nbt);
        return stack;
    }

    public int getStageTicks(ItemStack stack) {
        var nbt = stack.getNbt();
        return nbt != null && nbt.contains(STAGE_TICKS) ? nbt.getInt(STAGE_TICKS) : 0;
    }

    public ItemStack setStageTicks(ItemStack stack, int ticks) {
        var nbt = stack.getOrCreateNbt();
        nbt.putInt(STAGE_TICKS, ticks);
        stack.setNbt(nbt);
        return stack;
    }

    public ItemStack resetStageTicks(ItemStack stack) {
        return setStageTicks(stack, 0);
    }


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!selected && !world.isClient && getStage(stack) != null && getStage(stack).tickWhileUnselected && entity instanceof PlayerEntity player) {
            var playerState = Z7ServerState.getPlayerState(player);
            T item = (T) stack.getItem();
            handleTickInventory(new StagedItemView<>(
                getStageName(stack), item.getStageTicks(stack), item.getMaxStageTicks(stack),
                playerState,
                item, stack, entity, world
            ));
        }
    }
}
