import io.github.stuff_stuffs.fake_world.client.api.ClientChunkManagerExtensions;
import io.github.stuff_stuffs.fake_world.client.api.ClientOnlyWorld;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class BasicClientOnlyWorld extends ClientWorld implements ClientOnlyWorld {
    public BasicClientOnlyWorld(final ClientPlayNetworkHandler networkHandler, final Properties properties, final RegistryKey<World> registryRef, final RegistryEntry<DimensionType> dimensionTypeEntry, final int loadDistance, final int simulationDistance, final Supplier<Profiler> profiler, final WorldRenderer worldRenderer, final long seed, final BasicFakeWorldData data) {
        super(networkHandler, properties, registryRef, dimensionTypeEntry, loadDistance, simulationDistance, profiler, worldRenderer, false, seed);
    }

    @Override
    public void tick(final BooleanSupplier shouldKeepTicking) {
        super.tick(shouldKeepTicking);
        if (getTime() % 10 != 0) {
            return;
        }
        for (int i = -8; i <= 8; i++) {
            for (int j = -8; j <= 8; j++) {
                if (getChunk(i, j) instanceof final WorldChunk chunk) {
                    final LightingProvider lightingProvider = getChunkManager().getLightingProvider();
                    setBlockState(new BlockPos(i * 16, 0, j * 16), Blocks.RED_CONCRETE.getDefaultState());
                    lightingProvider.setColumnEnabled(new ChunkPos(i, j), true);
                    final ChunkSection[] chunkSections = chunk.getSectionArray();
                    final ChunkPos chunkPos = chunk.getPos();

                    for (int index = 0; index < chunkSections.length; ++index) {
                        final ChunkSection chunkSection = chunkSections[index];
                        final int sectionCoord = sectionIndexToCoord(index);
                        lightingProvider.setSectionStatus(ChunkSectionPos.from(chunkPos, sectionCoord), chunkSection.isEmpty());
                        scheduleBlockRenders(i, sectionCoord, j);
                    }
                }
            }
        }
    }

    @Override
    public void fake_world$setChunkMapCenter(final int x, final int z) {
        for (int i = -8; i <= 8; i++) {
            for (int j = -8; j <= 8; j++) {
                if (getChunk(i, j) instanceof EmptyChunk && ((ClientChunkManagerExtensions) getChunkManager()).fake_world$inBounds(i, j)) {
                    final WorldChunk worldChunk = new WorldChunk(this, new ChunkPos(i, j));
                    ((ClientChunkManagerExtensions) getChunkManager()).fake_world$putChunk(worldChunk, i, j);
                }
            }
        }
    }
}
