package io.github.stuff_stuffs.fake_world.client.mixin;

import io.github.stuff_stuffs.fake_world.client.api.ClientOnlyWorld;
import io.github.stuff_stuffs.fake_world.client.api.FakeWorldClientApi;
import io.github.stuff_stuffs.fake_world.client.impl.net.FakeWorldClientNet;
import io.github.stuff_stuffs.fake_world.common.api.FakeWorldData;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.WorldLoadingState;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {
    @Shadow
    private ClientWorld world;

    @Shadow
    private @Nullable WorldLoadingState worldLoadingState;

    @Redirect(method = "onPlayerRespawn", at = @At(value = "NEW", target = "(Lnet/minecraft/client/network/ClientPlayNetworkHandler;Lnet/minecraft/client/world/ClientWorld$Properties;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/entry/RegistryEntry;IILjava/util/function/Supplier;Lnet/minecraft/client/render/WorldRenderer;ZJ)Lnet/minecraft/client/world/ClientWorld;"))
    private ClientWorld fakeWorldHook(final ClientPlayNetworkHandler networkHandler, final ClientWorld.Properties properties, final RegistryKey<World> worldKey, final RegistryEntry<DimensionType> type, final int loadDistance, final int simulationDistance, final Supplier<Profiler> profiler, final WorldRenderer worldRenderer, final boolean debugWorld, final long seed) {
        final FakeWorldData data = FakeWorldClientNet.DATA_THREAD_LOCAL.get();
        if (data == null) {
            return new ClientWorld(networkHandler, properties, worldKey, type, loadDistance, simulationDistance, profiler, worldRenderer, debugWorld, seed);
        } else {
            return FakeWorldClientNet.createFakeWorld(networkHandler, properties, worldKey, type, loadDistance, simulationDistance, profiler, worldRenderer, debugWorld, seed, data);
        }
    }

    @ModifyVariable(method = "onPlayerRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;getWorldEntryReason(ZLnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/client/gui/screen/DownloadingTerrainScreen$WorldEntryReason;"))
    private boolean modifyVar(final boolean v) {
        return v || FakeWorldClientNet.DATA_THREAD_LOCAL.get() != null || FakeWorldClientNet.LEAVING.compareAndSet(true, false);
    }

    @Inject(method = "onPlayerRespawn", at = @At("RETURN"))
    private void chunkHook(final PlayerRespawnS2CPacket packet, final CallbackInfo ci) {
        final FakeWorldData data = FakeWorldClientNet.DATA_THREAD_LOCAL.get();
        if (data != null) {
            FakeWorldClientNet.DATA_THREAD_LOCAL.remove();
            if (worldLoadingState != null) {
                worldLoadingState.handleChunksComingPacket();
            }
        }
    }

    @Inject(method = {
            "onBlockBreakingProgress",
            "onBlockEntityUpdate",
            "onBlockEvent",
            "onBlockUpdate",
            "onBossBar",
            "onChunkBiomeData",
            "onChunkData",
            "onChunkDeltaUpdate",
            "onEntitySpawn",
            "onExplosion",
            "onLightUpdate",
            "onParticle",
            "onPlaySound",
            "onPlaySoundFromEntity",
            "onStopSound",
            "onUnloadChunk",
            "onWorldEvent",
            "onWorldBorderCenterChanged",
            "onWorldBorderInitialize",
            "onWorldBorderInterpolateSize",
            "onWorldBorderSizeChanged"
    }, at = @At("HEAD"), cancellable = true)
    private void cancelHook(@Coerce final Packet<ClientPlayPacketListener> packet, final CallbackInfo ci) {
        if (world instanceof ClientOnlyWorld) {
            ci.cancel();
        }
    }
}
