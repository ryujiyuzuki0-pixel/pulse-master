package dev.pulse.client.mixin;

import dev.pulse.client.module.ModuleManager;
import dev.pulse.client.module.impl.CapeModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Swaps the local player's cape for the one picked in the Cape module. Only affects you. */
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {
    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void pulse$customCape(CallbackInfoReturnable<PlayerSkin> cir) {
        if ((Object) this != Minecraft.getInstance().player) return;
        CapeModule cape = ModuleManager.cape();
        if (cape == null || !cape.enabled) return;
        cir.setReturnValue(cape.applyTo(cir.getReturnValue()));
    }
}
