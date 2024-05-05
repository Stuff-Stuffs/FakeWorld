package io.github.stuff_stuffs.fake_world.common.mixin;

import io.github.stuff_stuffs.fake_world.common.impl.ServerPlayerExtensions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {
    @Shadow
    @Final
    protected ServerPlayerEntity player;

    @Inject(method = "changeGameMode", at = @At("HEAD"), cancellable = true)
    private void cancelGameModeChange(final GameMode gameMode, final CallbackInfoReturnable<Boolean> cir) {
        if (((ServerPlayerExtensions) player).fake_world$data() != null) {
            cir.setReturnValue(false);
        }
    }
}
