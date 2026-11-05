package dev.pulse.client;

import dev.pulse.client.module.ModuleManager;
import dev.pulse.client.spotify.SpotifyClient;
import dev.pulse.client.ui.HudRenderer;
import dev.pulse.client.ui.MenuScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.Minecraft;
//? if >=1.21.11 {
import net.minecraft.resources.Identifier;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
*///?}
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PulseClient implements ClientModInitializer {
    public static final String ID = "pulse";
    public static final Logger LOG = LoggerFactory.getLogger("Pulse");

    private boolean menuKeyWasDown;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        PulseConfig.load();
        SpotifyClient.init();

        //? if >=1.21.11 {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(ID, "hud"), HudRenderer::render);
        //?} else {
        /*HudElementRegistry.addLast(ResourceLocation.fromNamespaceAndPath(ID, "hud"), HudRenderer::render);
        *///?}
        HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, original -> (g, dt) -> {
            var c = ModuleManager.crosshair();
            if (c != null && c.enabled) c.render(g, Minecraft.getInstance());
            else original.render(g, dt);
        });
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        ClientLifecycleEvents.CLIENT_STOPPING.register(c -> {
            ModuleManager.shutdown();
            PulseConfig.save();
        });
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
            if (level.isClientSide() && entity instanceof LivingEntity) ModuleManager.onAttack(player, entity);
            return InteractionResult.PASS;
        });
        ClientCommandRegistrationCallback.EVENT.register(PulseCommands::register);
        LOG.info("Pulse Client loaded. Press Right Shift in game.");
    }

    private void tick(Minecraft mc) {
        if (mc.getWindow() == null) return;
        boolean down = GLFW.glfwGetKey(mc.getWindow().handle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
        if (down && !menuKeyWasDown && mc.screen == null && mc.player != null) {
            mc.setScreen(new MenuScreen(null));
        }
        menuKeyWasDown = down;
        ModuleManager.tick(mc);
    }
}
