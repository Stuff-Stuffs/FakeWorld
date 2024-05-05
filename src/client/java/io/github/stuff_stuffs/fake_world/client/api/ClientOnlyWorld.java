package io.github.stuff_stuffs.fake_world.client.api;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;

public interface ClientOnlyWorld {
    void fake_world$setChunkMapCenter(int x, int z);

    default void fake_world$setLight(final int x, final int y, final int z, final ChunkNibbleArray data, final LightType type) {
        ((ClientWorld) this).getLightingProvider().enqueueSectionData(type, ChunkSectionPos.from(x, y, z), data);
        ((ClientWorld) this).scheduleBlockRenders(x * 16, y * 16 + ((ClientWorld) this).getBottomY(), z * 16);
    }
}
