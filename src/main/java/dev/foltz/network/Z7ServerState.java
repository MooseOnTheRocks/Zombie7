package dev.foltz.network;

import dev.foltz.Zombie7;
import net.minecraft.entity.LivingEntity;
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
        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, playerState) -> {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.putBoolean("isShooting", playerState.isShooting);
            playerNbt.putBoolean("isReloading", playerState.isReloading);
            playersNbt.put(uuid.toString(), playerNbt);
        });
        nbt.put("players", playersNbt);
        return nbt;
    }

    public static Z7ServerState createFromNbt(NbtCompound nbt) {
        Z7ServerState serverState = new Z7ServerState();
        NbtCompound playersNbt = nbt.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            Z7PlayerState playerState = new Z7PlayerState();
            playerState.isShooting = playersNbt.getCompound(key).getBoolean("isShooting");
            playerState.isShooting = playersNbt.getCompound(key).getBoolean("isReloading");
            UUID uuid = UUID.fromString(key);
            serverState.players.put(uuid, playerState);
        });
        return serverState;
    }

    public static Z7ServerState getServerState(MinecraftServer server) {
        PersistentStateManager stateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        Z7ServerState serverState = stateManager.getOrCreate(
                Z7ServerState::createFromNbt,
                Z7ServerState::new,
                Zombie7.MODID
        );
        return serverState;
    }

    public static Z7PlayerState getPlayerState(LivingEntity player) {
        Z7ServerState serverState = getServerState(player.world.getServer());
        Z7PlayerState playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> new Z7PlayerState());
        return playerState;
    }

    public void setShooting(LivingEntity player, boolean shooting) {
        getPlayerState(player).isShooting = shooting;
        this.markDirty();
    }

    public void setReloading(LivingEntity player, boolean reloading) {
        getPlayerState(player).isReloading = reloading;
        this.markDirty();
    }
}
