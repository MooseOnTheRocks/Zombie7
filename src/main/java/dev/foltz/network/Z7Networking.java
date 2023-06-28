package dev.foltz.network;

import dev.foltz.block.Z7Blocks;
import dev.foltz.block.Z7GoreBlock;
import dev.foltz.item.Z7GunItem;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import static dev.foltz.Zombie7.MODID;

public class Z7Networking {
    public static final Identifier BEGIN_SHOOT_PACKET_ID = new Identifier(MODID, "begin_shoot");
    public static final Identifier END_SHOOT_PACKET_ID = new Identifier(MODID, "end_shoot");
    public static final Identifier BEGIN_RELOAD_PACKET_ID = new Identifier(MODID, "begin_reload");
    public static final Identifier END_RELOAD_PACKET_ID = new Identifier(MODID, "end_reload");


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
                int layers = state.get(Z7GoreBlock.LAYERS);
                if (layers > 1) {
                    System.out.println("More than one layer...");
                    world.setBlockState(pos, state.with(Z7GoreBlock.LAYERS, layers - 1));
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
                    if (playerState.isShooting && playerState.isReloading) {
                        var player = server.getPlayerManager().getPlayer(uuid);
                        if (player != null) {
                            var itemStack = player.getMainHandStack();
                            if (itemStack.getItem() instanceof Z7GunItem gunItem) {
                                if (gunItem.isReloading(itemStack)) {
                                    ItemStack newStack = gunItem.tickReload(itemStack, player.world, player);
                                    player.setStackInHand(Hand.MAIN_HAND, newStack);
                                }
                                // Allow transition from reloading -> "shooting" without releasing trigger.
                                else {
                                    ItemStack newStack = gunItem.beginShoot(itemStack, player.world, player);
                                    player.setStackInHand(Hand.MAIN_HAND, newStack);
                                    serverState.setReloading(player, false);
                                }
                            }
                        }
                    }
                    else if (playerState.isShooting) {
                        var player = server.getPlayerManager().getPlayer(uuid);
                        if (player != null) {
                            var itemStack = player.getMainHandStack();
                            if (itemStack.getItem() instanceof Z7GunItem gunItem) {
                                ItemStack newStack = gunItem.tickShoot(itemStack, player.world, player);
                                player.setStackInHand(Hand.MAIN_HAND, newStack);
                            }
                        }
                    }
                    else if (playerState.isReloading) {
                        var player = server.getPlayerManager().getPlayer(uuid);
                        if (player != null) {
                            var itemStack = player.getMainHandStack();
                            if (itemStack.getItem() instanceof Z7GunItem gunItem) {
                                ItemStack newStack = gunItem.tickReload(itemStack, player.world, player);
                                player.setStackInHand(Hand.MAIN_HAND, newStack);
                            }
                        }
                    }
                });
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(BEGIN_SHOOT_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                ItemStack stack = player.getMainHandStack();
                if (!(stack.getItem() instanceof Z7GunItem gun)) {
                    return;
                }

                var playerState = Z7ServerState.getPlayerState(player);

                if (playerState.isShooting || playerState.isReloading) {
                    return;
                }

                var serverState = Z7ServerState.getServerState(server);

                if (gun.isBroken(stack)) {
                    serverState.setShooting(player, false);
                    serverState.setShooting(player, false);

                    System.out.println("Gun broken: left-click");
                }
                else if (gun.hasAmmoInGun(stack)) {
                    serverState.setShooting(player, true);
                    player.setStackInHand(Hand.MAIN_HAND, gun.beginShoot(stack, player.world, player));
//                    System.out.println("beginShoot [BEGIN SHOOT]");
                }
                else if (gun.canReload(stack, player)) {
                    serverState.setReloading(player, true);
                    serverState.setShooting(player, true);
                    player.setStackInHand(Hand.MAIN_HAND, gun.beginReload(stack, player.world, player));
//                    System.out.println("beginReload [BEGIN SHOOT]");
                }
                else {
                    // Cannot shoot and no ammo to reload with.
                    // Play "failure" sound effect here?
//                    System.out.println("Failed to shoot and no ammo!");
                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.PLAYERS, 1.0f, 1.0f);
                }
            });
        });


        ServerPlayNetworking.registerGlobalReceiver(END_SHOOT_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                ItemStack stack = player.getMainHandStack();
                if (!(stack.getItem() instanceof Z7GunItem gun)) {
                    return;
                }

                var serverState = Z7ServerState.getServerState(server);
                var playerState = Z7ServerState.getPlayerState(player);

                if (gun.isBroken(stack)) {
                    serverState.setReloading(player, false);
                    serverState.setShooting(player, false);
                    System.out.println("Gun broken: left-release");
                }
                else if (playerState.isShooting) {
                    serverState.setShooting(player, false);
                    serverState.setReloading(player, false);
                    player.setStackInHand(Hand.MAIN_HAND, gun.endShoot(stack, player.world, player));
                }
//                else if (playerState.isReloading) {
//                    serverState.setShooting(player, false);
//                    serverState.setReloading(player, false);
//                    player.setStackInHand(Hand.MAIN_HAND, gun.endReload(stack, player.world, player));
//                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(BEGIN_RELOAD_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                ItemStack stack = player.getMainHandStack();
                if (!(stack.getItem() instanceof Z7GunItem gun)) {
                    return;
                }

                var serverState = Z7ServerState.getServerState(server);
                var playerState = Z7ServerState.getPlayerState(player);

                if (playerState.isShooting || playerState.isReloading) {
                    return;
                }

                if (gun.isBroken(stack)) {
                    serverState.setReloading(player, true);
                    System.out.println("Gun broken: reload-click");
                }
                else if (gun.canReload(stack, player)) {
                    serverState.setReloading(player, true);
                    player.setStackInHand(Hand.MAIN_HAND, gun.beginReload(stack, player.world, player));
//                    System.out.println("beginReload [BEGIN RELOAD]");
                }
                else {
                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.PLAYERS, 1.0f, 1.0f);
                }
            });
        });


        ServerPlayNetworking.registerGlobalReceiver(END_RELOAD_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                ItemStack stack = player.getMainHandStack();
                if (!(stack.getItem() instanceof Z7GunItem gun)) {
                    return;
                }

                var serverState = Z7ServerState.getServerState(server);
                var playerState = Z7ServerState.getPlayerState(player);

                if (gun.isBroken(stack)) {
                    serverState.setReloading(player, false);
                    System.out.println("Gun broken: reload-release");
                }
                else if (playerState.isShooting) {
                    serverState.setReloading(player, false);
                    player.setStackInHand(Hand.MAIN_HAND, gun.endReload(stack, player.world, player));
                }
                else if (playerState.isReloading) {
                    serverState.setReloading(player, false);
                    player.setStackInHand(Hand.MAIN_HAND, gun.endReload(stack, player.world, player));
//                    System.out.println("endReload [BEGIN RELOAD]");
                }
            });
        });
    }
}
