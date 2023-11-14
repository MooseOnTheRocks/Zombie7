package dev.foltz.network;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class Z7PlayerState {
    private LivingEntity player;
    private Z7ServerState serverState;
    protected boolean isShooting;
    protected boolean isReloading;
    protected boolean isAiming;
    protected long lastFiredTime;
    protected ItemStack lastHeldItemStack;
    protected boolean isPressingShoot;
    protected boolean isPressingReload;
    protected boolean isPressingAim;

    public Z7PlayerState(LivingEntity player) {
        this.player = player;
        this.serverState = Z7ServerState.getServerState(player.getWorld().getServer());
        this.isShooting = false;
        this.isReloading = false;
        this.isAiming = false;
        this.lastFiredTime = 0;
        this.lastHeldItemStack = ItemStack.EMPTY;
        this.isPressingShoot = false;
        this.isPressingReload = false;
        this.isPressingAim = false;
    }

    public boolean isPressingShoot() {
        return isPressingShoot;
    }

    public void setPressingShoot(boolean pressing) {
        serverState.setPressingShoot(player, pressing);
    }

    public boolean isPressingReload() {
        return isPressingReload;
    }

    public void setPressingReload(boolean pressing) {
        serverState.setPressingReload(player, pressing);
    }

    public boolean isPressingAim() {
        return isPressingAim;
    }

    public void setPressingAim(boolean pressing) {
        serverState.setPressingShoot(player, pressing);
    }

    public boolean isShooting() {
        return isShooting;
    }

    public void setShooting(boolean shooting) {
        serverState.setShooting(player, shooting);
    }

    public boolean isReloading() {
        return isReloading;
    }

    public void setReloading(boolean reloading) {
        serverState.setReloading(player, reloading);
    }

    public boolean isAiming() {
        return isAiming;
    }

    public void setAiming(boolean aiming) {
        serverState.setAiming(player, aiming);
    }

    public long getLastFiredTime() {
        return lastFiredTime;
    }

    public void setLastFiredTime(long time) {
        this.lastFiredTime = time;
    }

    public ItemStack getLastHeldItemStack() {
        return lastHeldItemStack;
    }

    public void setLastHeldItemStack(ItemStack heldStack) {
        serverState.setLastHeldStack(player, heldStack);
    }
}
