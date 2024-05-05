package io.github.stuff_stuffs.fake_world.common.api;

import io.github.stuff_stuffs.fake_world.common.impl.FakeWorld;
import io.github.stuff_stuffs.fake_world.common.impl.ServerPlayerExtensions;
import io.github.stuff_stuffs.fake_world.common.impl.net.FakeWorldTravelPacket;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;

import java.util.Optional;

public final class FakeWorldApi {
    public static final RegistryKey<Registry<FakeWorldDataType<?>>> FAKE_WORLD_DATA_REGISTRY_KEY = RegistryKey.ofRegistry(FakeWorld.id("data_types"));
    public static final Registry<FakeWorldDataType<?>> FAKE_WORLD_DATA_TYPE_REGISTRY = FabricRegistryBuilder.createSimple(FAKE_WORLD_DATA_REGISTRY_KEY).buildAndRegister();
    public static final PacketCodec<RegistryByteBuf, FakeWorldDataType<?>> FAKE_WORLD_DATA_TYPE_CODEC = PacketCodecs.registryValue(FAKE_WORLD_DATA_REGISTRY_KEY);
    public static final Event<Leave> LEAVE_EVENT = EventFactory.createArrayBacked(Leave.class, events -> (entity, data) -> {
        for (Leave event : events) {
            event.onLeave(entity, data);
        }
    });

    public static void sendToFakeWorld(final ServerPlayerEntity entity, final ServerWorld parentWorld, final RegistryKey<World> fakeWorldKey, final FakeWorldData data) {
        final FakeWorldData old = ((ServerPlayerExtensions) entity).fake_world$data();
        if (old == null) {
            ((ServerPlayerExtensions) entity).fake_world$prevGameMode(entity.interactionManager.getGameMode());
        }
        if (entity.interactionManager.getGameMode() != GameMode.SPECTATOR) {
            entity.changeGameMode(GameMode.SPECTATOR);
        }
        if (entity.getWorld() != parentWorld) {
            entity.moveToWorld(parentWorld);
        }
        ((ServerPlayerExtensions) entity).fake_world$setData(data);
        ServerPlayNetworking.send(entity, new FakeWorldTravelPacket(fakeWorldKey, parentWorld.getDimensionEntry(), /*TODO*/0, Optional.of(data)));
        if (old != null) {
            LEAVE_EVENT.invoker().onLeave(entity, old);
        }
    }

    public static Optional<FakeWorldData> getData(final ServerPlayerEntity entity) {
        return Optional.ofNullable(((ServerPlayerExtensions) entity).fake_world$data());
    }

    public static void leaveFakeWorld(final ServerPlayerEntity entity) {
        final FakeWorldData data = ((ServerPlayerExtensions) entity).fake_world$data();
        if (data == null) {
            return;
        }
        ((ServerPlayerExtensions) entity).fake_world$setData(null);
        final ServerWorld world = entity.getServerWorld();
        world.getChunkManager().unloadEntity(entity);
        world.getChunkManager().loadEntity(entity);
        ServerPlayNetworking.send(entity, new FakeWorldTravelPacket(world.getRegistryKey(), world.getDimensionEntry(), BiomeAccess.hashSeed(world.getSeed()), Optional.empty()));
        ServerPlayNetworking.getSender(entity).sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.INITIAL_CHUNKS_COMING, 0));
        final PlayerManager manager = entity.getServer().getPlayerManager();
        manager.sendWorldInfo(entity, entity.getServerWorld());
        manager.sendCommandTree(entity);
        entity.changeGameMode(((ServerPlayerExtensions) entity).fake_world$prevGameMode());
        LEAVE_EVENT.invoker().onLeave(entity, data);
    }

    public interface Leave {
        void onLeave(ServerPlayerEntity entity, FakeWorldData data);
    }

    private FakeWorldApi() {
    }
}
