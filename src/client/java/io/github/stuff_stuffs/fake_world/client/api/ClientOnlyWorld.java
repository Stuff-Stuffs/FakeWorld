package io.github.stuff_stuffs.fake_world.client.api;

import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public interface PacketFilteringClientWorld {
    boolean filter(Packet<ClientPlayPacketListener> packet);
}
