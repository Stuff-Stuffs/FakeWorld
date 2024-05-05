package io.github.stuff_stuffs.fake_world.client.impl.net;

import io.github.stuff_stuffs.fake_world.client.api.FakeWorldClientApi;
import io.github.stuff_stuffs.fake_world.common.api.FakeWorldData;
import io.github.stuff_stuffs.fake_world.common.api.FakeWorldDataType;
import io.github.stuff_stuffs.fake_world.common.impl.net.FakeWorldCommonNet;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.CommonPlayerSpawnInfo;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public final class FakeWorldClientNet {
    public static final ThreadLocal<FakeWorldData> DATA_THREAD_LOCAL = new ThreadLocal<>();
    public static final AtomicBoolean LEAVING = new AtomicBoolean(false);

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(FakeWorldCommonNet.FAKE_WORLD_TRAVEL_PACKET, (payload, context) -> {
            final Optional<FakeWorldData> data = payload.data();
            final FakeWorldData value = data.orElse(null);
            DATA_THREAD_LOCAL.set(value);
            if(value==null) {
                LEAVING.set(true);
            }
            final PlayerRespawnS2CPacket packet = new PlayerRespawnS2CPacket(new CommonPlayerSpawnInfo(payload.dimension(), payload.worldKey(), payload.seed(), GameMode.SPECTATOR, null, false, false, Optional.empty(), 0), PlayerRespawnS2CPacket.KEEP_ALL);
            packet.apply(MinecraftClient.getInstance().getNetworkHandler());
        });
    }

    public static <T extends FakeWorldData> ClientWorld createFakeWorld(final ClientPlayNetworkHandler networkHandler, final ClientWorld.Properties properties, final RegistryKey<World> worldKey, final RegistryEntry<DimensionType> type, final int loadDistance, final int simulationDistance, final Supplier<Profiler> profiler, final WorldRenderer worldRenderer, final boolean debugWorld, final long seed, final T data) {
        final FakeWorldClientApi.WorldFactory<? super T> factory = FakeWorldClientApi.getFactory((FakeWorldDataType<T>) data.type());
        return factory.create(networkHandler, properties, worldKey, type, loadDistance, simulationDistance, profiler, worldRenderer, seed, data);
    }

    private FakeWorldClientNet() {
    }
}
