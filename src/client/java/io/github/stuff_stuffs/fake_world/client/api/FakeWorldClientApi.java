package io.github.stuff_stuffs.fake_world.client.api;

import io.github.stuff_stuffs.fake_world.common.api.FakeWorldData;
import io.github.stuff_stuffs.fake_world.common.api.FakeWorldDataType;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.Map;
import java.util.function.Supplier;

public final class FakeWorldClientApi {
    private static final Map<FakeWorldDataType<?>, WorldFactory<?>> FACTORY_MAP = new Reference2ObjectOpenHashMap<>();

    public static <T extends FakeWorldData> void registerFactory(final FakeWorldDataType<T> type, final WorldFactory<? super T> factory) {
        if (FACTORY_MAP.putIfAbsent(type, factory) != null) {
            throw new RuntimeException("Tried to register world factory twice!");
        }
    }

    public static <T extends FakeWorldData> WorldFactory<? super T> getFactory(final FakeWorldDataType<T> type) {
        //noinspection unchecked
        final WorldFactory<T> factory = (WorldFactory<T>) FACTORY_MAP.get(type);
        if (factory == null) {
            throw new RuntimeException("Tried to get world factory for unregistered type!");
        }
        return factory;
    }

    public interface WorldFactory<T extends FakeWorldData> {
        ClientWorld create(ClientPlayNetworkHandler handler, ClientWorld.Properties properties, RegistryKey<World> worldKey, RegistryEntry<DimensionType> dimension, int chunkLoadDistance, int simulationDistance, Supplier<Profiler> profiler, WorldRenderer renderer, long seed, T data);
    }

    private FakeWorldClientApi() {
    }
}
