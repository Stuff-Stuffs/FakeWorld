package io.github.stuff_stuffs.fake_world.common.impl.net;

import io.github.stuff_stuffs.fake_world.common.impl.FakeWorld;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.packet.CustomPayload;

public final class FakeWorldCommonNet {
    public static final CustomPayload.Id<FakeWorldTravelPacket> FAKE_WORLD_TRAVEL_PACKET = new CustomPayload.Id<>(FakeWorld.id("travel"));

    public static void init() {
        PayloadTypeRegistry.playS2C().register(FAKE_WORLD_TRAVEL_PACKET, FakeWorldTravelPacket.CODEC);
    }

    private FakeWorldCommonNet() {
    }
}
