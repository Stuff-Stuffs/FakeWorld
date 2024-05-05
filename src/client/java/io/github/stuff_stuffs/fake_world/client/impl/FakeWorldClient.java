package io.github.stuff_stuffs.fake_world.client.impl;

import io.github.stuff_stuffs.fake_world.client.impl.net.FakeWorldClientNet;
import net.fabricmc.api.ClientModInitializer;

public class FakeWorldClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FakeWorldClientNet.init();
    }
}
