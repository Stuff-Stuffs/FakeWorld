package io.github.stuff_stuffs.fake_world.client.mixin;

import io.github.stuff_stuffs.fake_world.client.api.ClientChunkManagerExtensions;
import io.github.stuff_stuffs.fake_world.client.api.ClientOnlyWorld;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientChunkManager.class)
public class MixinClientChunkManager implements ClientChunkManagerExtensions {
    @Shadow
    volatile ClientChunkManager.ClientChunkMap chunks;

    @Shadow
    @Final
    ClientWorld world;

    @Override
    public void fake_world$putChunk(final WorldChunk chunk, final int x, final int z) {
        if (chunks.isInRadius(x, z)) {
            final int index = chunks.getIndex(x, z);
            chunks.set(index, chunk);
            final LightingProvider lightingProvider = world.getChunkManager().getLightingProvider();
            final ChunkSection[] chunkSections = chunk.getSectionArray();
            final ChunkPos chunkPos = chunk.getPos();

            for (int i = 0; i < chunkSections.length; ++i) {
                final ChunkSection chunkSection = chunkSections[i];
                final int j = world.sectionIndexToCoord(i);
                lightingProvider.setSectionStatus(ChunkSectionPos.from(chunkPos, j), chunkSection.isEmpty());
                world.scheduleBlockRenders(x, j, z);
            }
        }
    }

    @Inject(method = "setChunkMapCenter", at = @At("RETURN"))
    private void centerHook(final int x, final int z, final CallbackInfo ci) {
        if (world instanceof final ClientOnlyWorld clientOnlyWorld) {
            clientOnlyWorld.fake_world$setChunkMapCenter(x, z);
        }
    }

    @Override
    public boolean fake_world$inBounds(final int x, final int z) {
        return chunks.isInRadius(x, z);
    }
}
