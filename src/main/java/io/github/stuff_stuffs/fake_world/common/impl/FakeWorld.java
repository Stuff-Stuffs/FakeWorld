package io.github.stuff_stuffs.fake_world.common.impl;

import io.github.stuff_stuffs.fake_world.common.api.FakeWorldApi;
import io.github.stuff_stuffs.fake_world.common.impl.net.FakeWorldCommonNet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FakeWorld implements ModInitializer {
    public static final String MOD_ID = "fake_world";

    @Override
    public void onInitialize() {
        FakeWorldCommonNet.init();
        FakeWorldApi.LEAVE_EVENT.invoker();
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            for (final ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                FakeWorldApi.leaveFakeWorld(player);
            }
        });
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (entity instanceof final ServerPlayerEntity player) {
                return ((ServerPlayerExtensions) player).fake_world$data() == null;
            }
            return true;
        });
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> FakeWorldApi.leaveFakeWorld(player));
    }

    public static Identifier id(final String path) {
        return new Identifier(MOD_ID, path);
    }
}
