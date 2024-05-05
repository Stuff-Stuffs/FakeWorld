package io.github.stuff_stuffs.fake_world.common.mixin;

import com.mojang.authlib.GameProfile;
import io.github.stuff_stuffs.fake_world.common.api.FakeWorldApi;
import io.github.stuff_stuffs.fake_world.common.api.FakeWorldData;
import io.github.stuff_stuffs.fake_world.common.impl.ServerPlayerExtensions;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity implements ServerPlayerExtensions {
    @Shadow
    public abstract boolean changeGameMode(GameMode gameMode);

    @Shadow
    @Final
    public ServerPlayerInteractionManager interactionManager;
    @Unique
    private @Nullable FakeWorldData fake_world$data = null;
    @Unique
    private GameMode beforeFakeGameMode;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void gameModeHook(final MinecraftServer server, final ServerWorld world, final GameProfile profile, final SyncedClientOptions clientOptions, final CallbackInfo ci) {
        beforeFakeGameMode = interactionManager.getGameMode();
    }

    @Override
    public @Nullable FakeWorldData fake_world$data() {
        return fake_world$data;
    }

    @Override
    public GameMode fake_world$prevGameMode() {
        return beforeFakeGameMode;
    }

    @Inject(method = "moveToWorld", at = @At("HEAD"))
    private void leaveHook(final ServerWorld destination, final CallbackInfoReturnable<Entity> cir) {
        fake_world$data = null;
        changeGameMode(beforeFakeGameMode);
    }

    @Override
    public void fake_world$prevGameMode(final GameMode mode) {
        beforeFakeGameMode = mode;
    }

    @Override
    public void fake_world$setData(@Nullable final FakeWorldData data) {
        fake_world$data = data;
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void disconnectHook(final CallbackInfo ci) {
        FakeWorldApi.leaveFakeWorld((ServerPlayerEntity) (Object) this);
    }
}
