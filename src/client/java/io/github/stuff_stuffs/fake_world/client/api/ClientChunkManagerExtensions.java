package io.github.stuff_stuffs.fake_world.client.api;

import net.minecraft.world.chunk.WorldChunk;

public interface ClientChunkManagerExtension {
    void putChunk(WorldChunk chunk, int x, int z);
}
