package io.github.stuff_stuffs.fake_world.client.api;

import net.minecraft.world.chunk.WorldChunk;

public interface ClientChunkManagerExtensions {
    void fake_world$putChunk(WorldChunk chunk, int x, int z);

    boolean fake_world$inBounds(int x, int z);
}
