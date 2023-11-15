package dev.foltz.network;

import dev.foltz.block.GoreBlock;
import dev.foltz.block.Z7Blocks;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import static dev.foltz.Zombie7.MODID;

public abstract class Z7Networking {
    public static final Identifier SHOOT_PRESS_PACKET_ID = new Identifier(MODID, "shoot_press");
    public static final Identifier SHOOT_RELEASE_PACKET_ID = new Identifier(MODID, "shoot_release");

    public static final Identifier RELOAD_PRESS_PACKET_ID = new Identifier(MODID, "reload_press");
    public static final Identifier RELOAD_RELEASE_PACKET_ID = new Identifier(MODID, "reload_release");

    public static final Identifier AIM_PRESS_PACKET_ID = new Identifier(MODID, "aim_press");
    public static final Identifier AIM_RELEASE_PACKET_ID = new Identifier(MODID, "aim_release");


    public static void registerAllEvents() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (state.getBlock() != Z7Blocks.GORE_BLOCK) {
                return true;
            }

            if (player.getAbilities().creativeMode) {
                return true;
            }

            ItemStack item = player.getMainHandStack();
            if (item.isIn(ItemTags.SHOVELS)) {
                System.out.println("Broken with a shovel!");
                return true;
            }
            else {
                System.out.println("Not broken with a shovel :c");
                int layers = state.get(GoreBlock.LAYERS);
                if (layers > 1) {
                    System.out.println("More than one layer...");
                    world.setBlockState(pos, state.with(GoreBlock.LAYERS, layers - 1));
                    Block.dropStacks(state, world, pos, null, player, item);
                    return false;
                }
                else {
                    System.out.println("Last layer, breaking.");
                    return true;
                }
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            server.execute(() -> {
                Z7ServerState serverState = Z7ServerState.getServerState(server);
                serverState.players.forEach((uuid, playerState) -> {
                    var player = server.getPlayerManager().getPlayer(uuid);
                    if (player == null) {
                        return;
                    }

//                    if (!ItemStack.areItemsEqual(player.getMainHandStack(), playerState.getLastHeldItemStack())) {
                    if (player.getMainHandStack() != playerState.getLastHeldItemStack()) {
//                        Z7PlayerStateEventHandler.onHeldItemChange(player);
                        Z7PlayerStateEventHandler.onHeldItemChange(player);
                    }
                    else {
//                        Z7PlayerStateEventHandler.onTick(player);
                        Z7PlayerStateEventHandler.onTick(player);
                    }
                });
            });
        });

        ServerPlayConnectionEvents.INIT.register((handler, server) ->
            server.execute(() -> Z7PlayerStateEventHandler.onHeldItemChange(handler.getPlayer())));


        ServerPlayNetworking.registerGlobalReceiver(SHOOT_PRESS_PACKET_ID, (server, player, handler, buf, responseSender) ->
            server.execute(() -> Z7PlayerStateEventHandler.onShootPress(player)));

        ServerPlayNetworking.registerGlobalReceiver(SHOOT_RELEASE_PACKET_ID, (server, player, handler, buf, responseSender) ->
            server.execute(() -> Z7PlayerStateEventHandler.onShootRelease(player)));


        ServerPlayNetworking.registerGlobalReceiver(RELOAD_PRESS_PACKET_ID, (server, player, handler, buf, responseSender) ->
            server.execute(() -> Z7PlayerStateEventHandler.onReloadPress(player)));

        ServerPlayNetworking.registerGlobalReceiver(RELOAD_RELEASE_PACKET_ID, (server, player, handler, buf, responseSender) ->
            server.execute(() -> Z7PlayerStateEventHandler.onReloadRelease(player)));


        ServerPlayNetworking.registerGlobalReceiver(AIM_PRESS_PACKET_ID, (server, player, handler, buf, responseSender) ->
            server.execute(() -> Z7PlayerStateEventHandler.onAimPress(player)));

        ServerPlayNetworking.registerGlobalReceiver(AIM_RELEASE_PACKET_ID, (server, player, handler, buf, responseSender) ->
            server.execute(() -> Z7PlayerStateEventHandler.onAimRelease(player)));
    }
}
