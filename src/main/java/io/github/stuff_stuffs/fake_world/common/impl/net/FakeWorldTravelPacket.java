package io.github.stuff_stuffs.fake_world.common.impl.net;

import net.minecraft.network.packet.CustomPayload;

public record FakeWorldSendPacket() implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return null;
    }
}
