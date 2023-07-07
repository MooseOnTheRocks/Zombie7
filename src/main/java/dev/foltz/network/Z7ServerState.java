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
    public final HashMap<UUID, Z7PlayerState> players = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
//        NbtCompound playersNbt = new NbtCompound();
//        players.forEach((uuid, playerState) -> {
//            NbtCompound playerNbt = playerState.writeNbt(new NbtCompound());
//            playersNbt.put(uuid.toString(), playerNbt);
//        });
//        nbt.put("players", playersNbt);
        return nbt;
    }

    public static Z7ServerState createFromNbt(NbtCompound nbt) {
//        Z7ServerState serverState = new Z7ServerState();
//        NbtCompound playersNbt = nbt.getCompound("players");
//        playersNbt.getKeys().forEach(key -> {
//            NbtCompound playerNbt = playersNbt.getCompound(key);
//            Z7PlayerState playerState = new Z7PlayerState();
//            UUID uuid = UUID.fromString(key);
//            serverState.players.put(uuid, playerState);
//        });
//        return serverState;
        return new Z7ServerState();
    }

    public static Z7ServerState getServerState(MinecraftServer server) {
        PersistentStateManager stateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        return stateManager.getOrCreate(Z7ServerState::createFromNbt, Z7ServerState::new, Zombie7.MODID);
    }

    public static Z7PlayerState getPlayerState(LivingEntity player) {
        Z7ServerState serverState = getServerState(player.world.getServer());
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
