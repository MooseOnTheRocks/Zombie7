package dev.foltz.network;

import dev.foltz.item.stage.StagedItem;
import dev.foltz.item.stage.StagedItemView;
import dev.foltz.network.Z7PlayerState;
import dev.foltz.network.Z7ServerState;
import net.minecraft.entity.player.PlayerEntity;

public abstract class Z7PlayerStateEventHandler {
    public static void onShootPress(PlayerEntity player) {
        var stack = player.getMainHandStack();
        var item = stack.getItem();
        var world = player.world;
        Z7PlayerState playerState = Z7ServerState.getPlayerState(player);

        playerState.setPressingShoot(true);
//        playerState.setShooting(true);

        if (item instanceof StagedItem gun) {
            int stageId = gun.getStageId(stack);
            gun.handlePressShoot(new StagedItemView<>(
                gun.stagesGraph.nameFromId(stageId), gun.getStageTicks(stack), gun.getMaxStageTicks(stack),
                playerState,
                    gun, stack, player, world
            ));
        }
    }

    public static void onShootRelease(PlayerEntity player) {
        var stack = player.getMainHandStack();
        var item = stack.getItem();
        var world = player.world;
        var playerState = Z7ServerState.getPlayerState(player);

        playerState.setPressingShoot(false);
//        playerState.setShooting(false);

        if (item instanceof StagedItem gun) {
            int stageId = gun.getStageId(stack);
            gun.handleReleaseShoot(new StagedItemView<>(
                gun.stagesGraph.nameFromId(stageId), gun.getStageTicks(stack), gun.getMaxStageTicks(stack),
                playerState,
                    gun, stack, player, world
            ));
        }
    }

    public static void onReloadPress(PlayerEntity player) {
        var stack = player.getMainHandStack();
        var item = stack.getItem();
        var world = player.world;
        var server = world.getServer();
        var serverState = Z7ServerState.getServerState(server);
        var playerState = Z7ServerState.getPlayerState(player);

        playerState.setPressingReload(true);
//        playerState.setReloading(true);

        if (item instanceof StagedItem gun) {
            int stageId = gun.getStageId(stack);
            gun.handlePressReload(new StagedItemView<>(
                gun.stagesGraph.nameFromId(stageId), gun.getStageTicks(stack), gun.getMaxStageTicks(stack),
                playerState,
                    gun, stack, player, world
            ));
        }
    }

    public static void onReloadRelease(PlayerEntity player) {
        var stack = player.getMainHandStack();
        var item = stack.getItem();
        var world = player.world;
        var server = world.getServer();
        var serverState = Z7ServerState.getServerState(server);
        var playerState = Z7ServerState.getPlayerState(player);

        playerState.setPressingReload(false);
//        playerState.setReloading(false);

        if (item instanceof StagedItem gun) {
            int stageId = gun.getStageId(stack);
            gun.handleReleaseReload(new StagedItemView<>(
                gun.stagesGraph.nameFromId(stageId), gun.getStageTicks(stack), gun.getMaxStageTicks(stack),
                playerState,
                    gun, stack, player, world
            ));
        }
    }

    public static void onAimPress(PlayerEntity player) {
        System.out.println("On zoom press!");
    }

    public static void onAimRelease(PlayerEntity player) {
        System.out.println("On zoom release!");
    }

    public static void onHeldItemChange(PlayerEntity player) {
        Z7PlayerState playerState = Z7ServerState.getPlayerState(player);
//        playerState.setShooting(false);
//        playerState.setReloading(false);
//        playerState.setAiming(false);
        var stack = playerState.getLastHeldItemStack();
        playerState.setPressingShoot(false);
        playerState.setPressingReload(false);
        playerState.setLastHeldItemStack(player.getMainHandStack());
        playerState.setLastFiredTime(player.world.getTime());
        System.out.println("Held item change: " + stack.getItem() + " -> " + player.getMainHandStack().getItem());

//        var stack = lastStack;
        var item = stack.getItem();
        var world = player.world;
        var server = world.getServer();
//        var serverState = Z7ServerState.getServerState(server);
//        var playerState = Z7ServerState.getPlayerState(player);

        if (item instanceof StagedItem gun) {
            int stageId = gun.getStageId(stack);
            gun.handleUnselected(new StagedItemView<>(
                gun.stagesGraph.nameFromId(stageId), gun.getStageTicks(stack), gun.getMaxStageTicks(stack),
                playerState,
                    gun, stack, player, world
            ));
        }
    }

    public static void onTick(PlayerEntity player) {
        var playerState = Z7ServerState.getPlayerState(player);
        var stack = player.getMainHandStack();
        var item = stack.getItem();
        var world = player.world;

        if (item instanceof StagedItem gun) {
            System.out.println("About to tick a StagedItem:");
            System.out.println("item = " + item);
            int stageId = gun.getStageId(stack);
            System.out.println("stageId = " + stageId);
            gun.handleTick(new StagedItemView<>(
                gun.stagesGraph.nameFromId(stageId), gun.getStageTicks(stack), gun.getMaxStageTicks(stack),
                playerState,
                    gun, stack, player, world
            ));
        }
    }
}
