package dev.foltz.network;

import dev.foltz.Zombie7;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class Z7ServerState extends PersistentState {
//    public static final Type<Z7ServerState> TYPE = new Type<>(
//        Z7ServerState::new,
//        Z7ServerState::createFromNbt,
//        null
//    );

    public final HashMap<UUID, Z7PlayerState> players = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return nbt;
    }

    public static Z7ServerState createFromNbt(NbtCompound nbt) {
        return new Z7ServerState();
    }

    public static Z7ServerState getServerState(MinecraftServer server) {
        PersistentStateManager stateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
//        var state = stateManager.getOrCreate(TYPE, Zombie7.MODID);
//        state.markDirty();
//        return state;
//        return stateManager.getOrCreate(Zombie7.MODID);
//        return stateManager.getOrCreate(Z7ServerState::createFromNbt, Zombie7.MODID);
        return stateManager.getOrCreate(Z7ServerState::createFromNbt, Z7ServerState::new, Zombie7.MODID);
    }

    public static Z7PlayerState getPlayerState(LivingEntity player) {
        Z7ServerState serverState = getServerState(player.getWorld().getServer());
        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new Z7PlayerState(player));
    }

    public void setPressingShoot(LivingEntity player, boolean pressing) {
        getPlayerState(player).isPressingShoot = pressing;
        this.markDirty();
    }

    public void setPressingReload(LivingEntity player, boolean pressing) {
        getPlayerState(player).isPressingReload = pressing;
        this.markDirty();
    }

    public void setShooting(LivingEntity player, boolean shooting) {
        getPlayerState(player).isShooting = shooting;
        this.markDirty();
    }

    public void setReloading(LivingEntity player, boolean reloading) {
        getPlayerState(player).isReloading = reloading;
        this.markDirty();
    }

    public void setAiming(LivingEntity player, boolean aiming) {
        getPlayerState(player).isAiming = aiming;
        this.markDirty();
    }

    public void setLastHeldStack(LivingEntity player, ItemStack stack) {
        getPlayerState(player).lastHeldItemStack = stack;
        this.markDirty();
    }
}
