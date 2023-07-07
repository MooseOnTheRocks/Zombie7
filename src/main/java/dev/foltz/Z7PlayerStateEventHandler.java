package dev.foltz;

import dev.foltz.item.Z7IReloadable;
import dev.foltz.item.Z7IShootable;
import dev.foltz.item.gun.Z7IGunlike;
import dev.foltz.network.Z7PlayerState;
import dev.foltz.network.Z7ServerState;
import net.minecraft.entity.player.PlayerEntity;

public class Z7PlayerStateEventHandler {
    public static void onShootPress(PlayerEntity player) {
        var stack = player.getMainHandStack();
        var item = stack.getItem();
        var world = player.world;
        Z7PlayerState playerState = Z7ServerState.getPlayerState(player);

        playerState.setPressingShoot(true);

        playerState.setShooting(true);

        if (!(item instanceof Z7IGunlike gunlike)) {
            return;
        }

        boolean isShooting = gunlike.isShooting(stack, player);
        boolean canShoot = gunlike.canShoot(stack, player);
        boolean hasAmmo = gunlike.hasAmmoInGun(stack);

        if (((!isShooting && !canShoot) || gunlike.isInFiringCooldown(stack)) && !hasAmmo) {
            // Note: tickReload gets handled on server tick.
            playerState.setReloading(true);
        }
    }

    public static void onShootRelease(PlayerEntity player) {
        var stack = player.getMainHandStack();
        var item = stack.getItem();
        var world = player.world;
        var playerState = Z7ServerState.getPlayerState(player);

        playerState.setPressingShoot(false);
        playerState.setShooting(false);
        if (playerState.isReloading() && !playerState.isPressingReload()) {
            playerState.setReloading(false);
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
        playerState.setReloading(true);
    }

    public static void onReloadRelease(PlayerEntity player) {
        var stack = player.getMainHandStack();
        var item = stack.getItem();
        var world = player.world;
        var server = world.getServer();
        var serverState = Z7ServerState.getServerState(server);
        var playerState = Z7ServerState.getPlayerState(player);

        playerState.setPressingReload(false);
        playerState.setReloading(false);
    }

    public static void onAimPress(PlayerEntity player) {
        System.out.println("On zoom press!");
    }

    public static void onAimRelease(PlayerEntity player) {
        System.out.println("On zoom release!");
    }

    public static void onHeldItemChange(PlayerEntity player) {
        Z7PlayerState playerState = Z7ServerState.getPlayerState(player);
        playerState.setShooting(false);
        playerState.setReloading(false);
        playerState.setAiming(false);
        playerState.setLastHeldItemStack(player.getMainHandStack());
        playerState.setLastFiredTime(player.world.getTime());
    }

    public static void onTick(PlayerEntity player) {
        var playerState = Z7ServerState.getPlayerState(player);
        var stack = player.getMainHandStack();
        var item = stack.getItem();
        var world = player.world;

        var reloadable = item instanceof Z7IReloadable r ? r : null;
        var shootable = item instanceof Z7IShootable s ? s : null;
        var isReloadable = reloadable != null;
        var isShootable = shootable != null;
        var isFiringCooldown = item instanceof Z7IGunlike gunlike && gunlike.isInFiringCooldown(stack);
        var isShooting = isShootable && shootable.isShooting(stack, player);
        var isReloading = isReloadable && reloadable.isReloading(stack);
        var canShoot = isShootable && shootable.canShoot(stack, player);
        var canReload = isReloadable && reloadable.canReload(stack, player);

        if (isFiringCooldown) {
            return;
        }


        var playerReloading = playerState.isReloading();
        var playerShooting = playerState.isShooting();

//        System.out.println("player (R, S) :: " + playerReloading + ", " + playerShooting);

        if (playerReloading && playerShooting) {
            if (isReloadable && isShootable) {
                if (reloadable.tryReloading(stack, player)) {
                    reloadable.tickReload(stack, player);
                }
                else if (isReloading) {
                    reloadable.endReload(stack, player);
                    playerReloading = false;
                }
                else {
                    playerReloading = false;
                }

                if (!playerReloading && !playerState.isPressingReload()) {
                    if (shootable.tryShooting(stack, player)) {
                        shootable.tickShoot(stack, player);
                    }
                    else if (isShooting) {
                        shootable.endShoot(stack, player);
                        playerShooting = false;
                    }
                    else {
                        playerShooting = false;
                    }
                }

                if (!playerReloading && playerShooting && playerState.isPressingReload()) {
                    playerShooting = false;
                }
            }
            else if (isReloadable) {
                if (reloadable.tryReloading(stack, player)) {
                    reloadable.tickReload(stack, player);
                }
                else if (isReloading) {
                    reloadable.endReload(stack, player);
                    playerReloading = false;
                }
                else {
                    playerReloading = false;
                }
            }
            else if (isShootable) {
                if (shootable.tryShooting(stack, player)) {
                    shootable.tickShoot(stack, player);
                }
                else if (isShooting) {
                    shootable.endShoot(stack, player);
                    playerShooting = false;
                }
                else {
                    playerShooting = false;
                }
            }
            else {
                playerReloading = false;
                playerShooting = false;
            }
        }
        else if (playerReloading) {
            if (isReloadable) {
                if (reloadable.tryReloading(stack, player)) {
                    reloadable.tickReload(stack, player);
                }
                else if (isReloading) {
                    reloadable.endReload(stack, player);
                    playerReloading = false;
                }
                else {
                    playerReloading = false;
                }
            }
            else {
                playerReloading = false;
            }
        }
        else if (playerShooting) {
//            if (item instanceof Z7IGunlike gunlike) {
////                System.out.println("Ticking for playerShooting, gunStage = " + gunlike.getGunStage(stack));
//            }
            if (isShootable) {
                if (shootable.tryShooting(stack, player)) {
//                    System.out.println("Ticking shoot!");
                    shootable.tickShoot(stack, player);
                }
                else if (isShooting) {
                    shootable.endShoot(stack, player);
                    playerShooting = false;
                }
                else {
                    playerShooting = false;
                }
            }
            else if (isReloadable && reloadable.tryReloading(stack, player)) {
                reloadable.tickReload(stack, player);
                playerReloading = true;
//                    playerShooting = true;
            }
            else {
                playerShooting = false;
            }

//            if (isReloadable && reloadable.canReload(stack, player) && !playerShooting) {
//                if (reloadable.tryReloading(stack, player)) {
//                    reloadable.tickReload(stack, player);
//                    playerReloading = true;
//                    playerShooting = true;
//                }
//            }
        }
        else {
            if (isReloadable && isShootable) {
                if (isReloading) {
                    reloadable.endReload(stack, player);
                }
                else if (isShooting) {
                    shootable.endShoot(stack, player);
                }
            }
            else if (isReloadable) {
                if (isReloading) {
                    reloadable.endReload(stack, player);
                }
            }
            else if (isShootable) {
                if (isShooting) {
                    shootable.endShoot(stack, player);
                }
            }
            else {
                // ???
            }
        }

        playerState.setShooting(playerShooting);
        playerState.setReloading(playerReloading);
    }
}
