package io.github.stuff_stuffs.fake_world.common.impl;

import io.github.stuff_stuffs.fake_world.common.api.FakeWorldData;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public interface ServerPlayerExtensions {
    @Nullable FakeWorldData fake_world$data();

    GameMode fake_world$prevGameMode();

    void fake_world$prevGameMode(GameMode mode);

    void fake_world$setData(@Nullable FakeWorldData data);
}
