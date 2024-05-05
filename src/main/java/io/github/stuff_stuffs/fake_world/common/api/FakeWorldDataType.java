package io.github.stuff_stuffs.fake_world.common.api;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public final class FakeWorldDataType<T extends FakeWorldData> {
    private final PacketCodec<RegistryByteBuf, T> codec;

    public FakeWorldDataType(final PacketCodec<RegistryByteBuf, T> codec) {
        this.codec = codec;
    }

    public PacketCodec<RegistryByteBuf, T> codec() {
        return codec;
    }
}
