package io.github.stuff_stuffs.fake_world.common.impl.net;

import io.github.stuff_stuffs.fake_world.common.api.FakeWorldData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.Optional;

public record FakeWorldTravelPacket(RegistryKey<World> worldKey, RegistryEntry<DimensionType> dimension, long seed,
                                    Optional<FakeWorldData> data) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, FakeWorldTravelPacket> CODEC = PacketCodec.tuple(RegistryKey.createPacketCodec(RegistryKeys.WORLD).cast(), FakeWorldTravelPacket::worldKey, PacketCodecs.registryEntry(RegistryKeys.DIMENSION_TYPE), FakeWorldTravelPacket::dimension, PacketCodecs.VAR_LONG.cast(), FakeWorldTravelPacket::seed, PacketCodecs.optional(FakeWorldData.CODEC), FakeWorldTravelPacket::data, FakeWorldTravelPacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return FakeWorldCommonNet.FAKE_WORLD_TRAVEL_PACKET;
    }
}
