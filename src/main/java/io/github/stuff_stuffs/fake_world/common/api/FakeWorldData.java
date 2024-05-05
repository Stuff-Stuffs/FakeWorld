package io.github.stuff_stuffs.fake_world.common.api;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface FakeWorldData {
    PacketCodec<RegistryByteBuf, FakeWorldData> CODEC = FakeWorldApi.FAKE_WORLD_DATA_TYPE_CODEC.dispatch(FakeWorldData::type, FakeWorldDataType::codec);

    FakeWorldDataType<?> type();
}
